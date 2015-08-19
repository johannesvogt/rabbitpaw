package jv.rabbitfilter.consumer;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import jv.rabbitfilter.core.MessageConfig;
import jv.rabbitfilter.core.MessageConfig.FieldEntry;

import java.util.*;

/**
 * Created by johannes on 16/08/15.
 */
public class Binding implements Iterable<Binding.Stage> {

    private final List<Stage> stages;

    private Binding() {
        this.stages = Lists.newArrayList();
    }

    static Binding of(MessageFilter messageFilter) {
        Binding binding = new Binding();

        RoutingPattern routingPattern = new RoutingPattern(messageFilter.getMessageConfig().size());

        Iterator<MessageConfig.FieldEntry> it = messageFilter.getMessageConfig().iterator();
        while (it.hasNext()) {
            FieldEntry field = it.next();

            List<String> param = messageFilter.getParam(field.name);

            if (param != null && !param.isEmpty()) {
                if (param.size() == 1) {
                    routingPattern.addSequential(field.index, param.get(0));

                } else {
                    if (routingPattern.parallel != null) {
                        binding.addLevel(routingPattern.render(), field.isLast);
                        routingPattern = new RoutingPattern(messageFilter.getMessageConfig().size());
                    }
                    routingPattern.setParallel(field.index, param);
                }
            }


        }

        binding.addLevel(routingPattern.render(), true);
        return binding;
    }

    private void addLevel(List<String> keys, boolean isLastLevel) {
        stages.add(new Stage(keys, isLastLevel, stages.size() + 1));
    }

    public Iterator<Stage> iterator() {
        return stages.iterator();
    }

    int getChainSize() {
        return stages.size();
    }

    Stage getFirstStage() {
        return stages.get(0);
    }

    Stage getStage(int i) {
        return stages.get(i);
    }

    static class Stage {
        List<String> keys;

        final boolean isLast;

        final int level;

        public Stage(List<String> keys, boolean lastLevel, int level) {
            this.keys = keys;
            this.isLast = lastLevel;
            this.level = level;
        }
    }

    private static class RoutingPattern {
        Collection<String> parallel;
        int parallelIndex;
        final List<String> sequentials;

        RoutingPattern(int size) {
            this.sequentials = new ArrayList<String>(Collections.nCopies(size, "*"));
        }

        void setParallel(int parallelIndex, Collection<String> parallel) {
            this.parallel = parallel;
            this.parallelIndex = parallelIndex;
        }

        void addSequential(Integer position, String value) {
            this.sequentials.set(position, value);
        }

        List<String> render() {

            List<String> keys = Lists.newArrayList();

            if (parallel == null) {
                keys.add(Joiner.on(".").join(sequentials));

            } else {
                for (String value : parallel) {
                    sequentials.set(parallelIndex, value);
                    keys.add(Joiner.on(".").join(sequentials));
                }
            }

            return keys;
        }
    }
}
