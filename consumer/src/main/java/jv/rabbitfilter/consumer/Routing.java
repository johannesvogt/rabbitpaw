package jv.rabbitfilter.consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import jv.rabbitfilter.core.MessageFields.FieldEntry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by johannes on 16/08/15.
 */
public class Routing implements Iterable<Routing.Binding> {

    private final List<Binding> routingChain;

    private Routing() {
        this.routingChain = Lists.newArrayList();
    }

    static Routing create(MessageFilter messageFilter) {
        StringBuilder prefix = new StringBuilder();
        StringBuilder postfix = new StringBuilder();
        Collection<String> alternatives = null;
        Routing routing = new Routing();
        for (FieldEntry field : messageFilter.getMessageFields()) {
            if (messageFilter.acceptsAll(field.name)) {
                if (alternatives == null) {
                    prefix.append("*");
                } else {
                    postfix.append("*");
                }

            } else if (messageFilter.acceptsOne(field.name)) {
                if (alternatives == null) {
                    prefix.append(messageFilter.getFirstParam(field.name));
                } else {
                    postfix.append(messageFilter.getFirstParam(field.name));
                }

            } else {
                if (alternatives == null) {
                    alternatives = messageFilter.getParam(field.name);
                } else {
                    postfix.append("#");
                    routing.addLevel(prefix.toString(), alternatives, postfix.toString(), field.isLast);
                    prefix = new StringBuilder("#.");
                    postfix = new StringBuilder();
                    alternatives = messageFilter.getParam(field.name);
                }
            }

            if (!field.isLast) {
                if (alternatives == null) {
                    prefix.append(".");
                } else {
                    postfix.append(".");
                }
            }

            if (field.isLast) {
                routing.addLevel(prefix.toString(), alternatives, postfix.toString(), true);
            }
        }
        routing.cleanup();
        return routing;
    }

    private void cleanup() {
        if (routingChain.size() > 1 && routingChain.get(routingChain.size() - 1).keys.size() == 1) {
            String[] lastKeyArray = routingChain.get(routingChain.size() - 1).keys.get(0).split("\\.");
            String lastKey = lastKeyArray[lastKeyArray.length - 1];
            List<String> newKeyList = Lists.newArrayList();
            for (String key : routingChain.get(routingChain.size() - 2).keys) {
                    newKeyList.add(key.substring(0, key.length() - 1) + lastKey);
            }
            routingChain.get(routingChain.size() - 2).keys = newKeyList;
            routingChain.remove(routingChain.size() - 1);
        }
    }

    private void addLevel(String prefix, Collection<String> alternatives, String postfix, boolean isLastLevel) {
        ImmutableList.Builder<String> levelBindings = ImmutableList.builder();
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        if (alternatives == null) {
            levelBindings.add(new StringBuilder(prefix).toString());
        } else {
            for (String alternative : alternatives) {
                levelBindings.add(new StringBuilder(prefix).append(alternative).append(postfix).toString());
            }
        }
        routingChain.add(new Binding(levelBindings.build(), isLastLevel, routingChain.size() + 1));
    }

    public Iterator<Binding> iterator() {
        return routingChain.iterator();
    }

    public int getChainSize() {
        return routingChain.size();
    }

    public Binding getFirstBinding() {
        return routingChain.get(0);
    }

    public Binding getBinding(int i) {
        return routingChain.get(i);
    }

    static class Binding {
        List<String> keys;

        final boolean isLastLevel;

        final int level;

        public Binding(List<String> keys, boolean lastLevel, int level) {
            this.keys = keys;
            this.isLastLevel = lastLevel;
            this.level = level;
        }
    }
}
