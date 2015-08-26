package nz.gen.vogt.rabbitpaw.subscriber;

import nz.gen.vogt.rabbitpaw.core.MessageConfig;
import nz.gen.vogt.rabbitpaw.core.MessageConfig.FieldEntry;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by johannes on 16/08/15.
 */
public class Binding implements Iterable<Binding.Stage> {

    private final List<Stage> stages;

    private Binding() {
        this.stages = new ArrayList<>();
    }

    static <T> Binding of(MessageFilter<T> messageFilter) {
        Binding binding = new Binding();
        String bindingId = randomId();
        MessageConfig<T> messageConfig = messageFilter.getMessageConfig();

        RoutingKeyPattern routingKeyPattern = new RoutingKeyPattern(messageConfig.size());

        for (FieldEntry field : messageConfig) {
            List<String> param = messageFilter.getParam(field.name);

            if (param != null && param.size() == 1) {
                    routingKeyPattern.addSequential(field.index, param.get(0));

            }
        }
        for (FieldEntry field : messageConfig) {
            List<String> param = messageFilter.getParam(field.name);

            if (param != null && param.size() > 1) {
                if (routingKeyPattern.parallel != null) {
                    binding.addLevel(routingKeyPattern.render(),
                            bindingId, messageConfig.getExchangeName(), field.isLast);
                    routingKeyPattern = new RoutingKeyPattern(messageFilter.getMessageConfig().size());
                }
                routingKeyPattern.setParallel(field.index, param);
            }
        }

        binding.addLevel(routingKeyPattern.render(), bindingId, messageConfig.getExchangeName(), true);
        return binding;
    }

    private void addLevel(List<String> keys, String bindingId, String exchangeName, boolean isQueueBinding) {
        String src;
        String dest;
        if (stages.size() == 0) {
            src = exchangeName;
        } else {
            src = bindingId + "_" +stages.size();
        }
        dest = bindingId + "_" + (stages.size() + 1);
        stages.add(new Stage(keys, isQueueBinding, src, dest));
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

        final boolean isQueueBinding;

        final String src;
        final String dest;

        public Stage(List<String> keys, boolean isQueueBinding, String src, String dest) {
            this.keys = keys;
            this.isQueueBinding = isQueueBinding;
            this.src = src;
            this.dest = dest;
        }
    }

    private static class RoutingKeyPattern {
        Collection<String> parallel;
        int parallelIndex;
        final List<String> sequentials;

        RoutingKeyPattern(int size) {
            this.sequentials = new ArrayList<>(Collections.nCopies(size, "*"));
        }

        void setParallel(int parallelIndex, Collection<String> parallel) {
            this.parallel = parallel;
            this.parallelIndex = parallelIndex;
        }

        void addSequential(Integer position, String value) {
            this.sequentials.set(position, value);
        }

        List<String> render() {

            List<String> keys = new ArrayList<>();

            if (parallel == null) {
                keys.add(sequentials.stream().collect(Collectors.joining(".")));

            } else {
                for (String value : parallel) {
                    sequentials.set(parallelIndex, value);
                    keys.add(sequentials.stream().collect(Collectors.joining(".")));
                }
            }

            return keys;
        }
    }

    private static SecureRandom random = new SecureRandom();

    private static String randomId() {
        return new BigInteger(130, random).toString(32);
    }

}
