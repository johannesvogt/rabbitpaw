package jv.rabbitfilter.consumer;

import jv.rabbitfilter.core.annotation.Filterable;
import jv.rabbitfilter.core.annotation.Message;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by johannes on 17/08/15.
 */
public class RoutingTest {

    @Message
    private static class TestMessage {
        @Filterable
        private String category;
        @Filterable
        private Object location;
        @Filterable
        private String color;
    }

    @Test
    public void testEmptyFilter() {
        MessageFilter filter = MessageFilter.of(TestMessage.class);

        Routing routing = Routing.create(filter);

        assertThat(routing.getChainSize()).isEqualTo(1);

        assertThat(routing.getFirstBinding().keys.size()).isEqualTo(1);

        assertThat(routing.getFirstBinding().keys.get(0)).isEqualTo("*.*.*");
    }

    @Test
    public void testSimpleFilter() {
        MessageFilter filter = MessageFilter.of(TestMessage.class).thatMatches("color", "blue");

        Routing routing = Routing.create(filter);

        assertThat(routing.getChainSize()).isEqualTo(1);

        assertThat(routing.getFirstBinding().keys.size()).isEqualTo(1);

        assertThat(routing.getFirstBinding().keys.get(0)).isEqualTo("*.blue.*");
    }

    @Test
    public void testSimpleFilter2() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .thatMatches("color", "blue")
                .thatMatches("category", "animal")
                .thatMatches("location", "sea");

        Routing routing = Routing.create(filter);

        assertThat(routing.getChainSize()).isEqualTo(1);

        assertThat(routing.getFirstBinding().keys.size()).isEqualTo(1);

        assertThat(routing.getFirstBinding().keys.get(0)).isEqualTo("animal.blue.sea");
    }

    @Test
    public void testParallelFilter() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .thatMatches("color", "blue")
                .thatMatches("color", "orange");

        Routing routing = Routing.create(filter);

        assertThat(routing.getChainSize()).isEqualTo(1);

        assertThat(routing.getFirstBinding().keys.size()).isEqualTo(2);

        assertThat(routing.getFirstBinding().keys).contains("*.blue.*");
        assertThat(routing.getFirstBinding().keys).contains("*.orange.*");
    }

    @Test
    public void testParallelFilter2() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .thatMatches("color", "blue")
                .thatMatches("color", "orange")
                .thatMatches("location", "zoo")
                .thatMatches("location", "city");

        Routing routing = Routing.create(filter);

        assertThat(routing.getChainSize()).isEqualTo(2);

        assertThat(routing.getFirstBinding().keys.size()).isEqualTo(2);
        assertThat(routing.getFirstBinding().keys).contains("*.blue.#");
        assertThat(routing.getFirstBinding().keys).contains("*.orange.#");

        assertThat(routing.getBinding(1).keys.size()).isEqualTo(2);
        assertThat(routing.getBinding(1).keys).contains("#.zoo");
        assertThat(routing.getBinding(1).keys).contains("#.city");
    }

    @Test
    public void testParallelFilter3() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .thatMatches("color", "blue")
                .thatMatches("color", "orange")
                .thatMatches("category", "animal")
                .thatMatches("category", "car");

        Routing routing = Routing.create(filter);

        assertThat(routing.getChainSize()).isEqualTo(2);

        assertThat(routing.getBinding(0).keys.size()).isEqualTo(2);
        assertThat(routing.getBinding(0).keys).contains("animal.#");
        assertThat(routing.getBinding(0).keys).contains("car.#");

        assertThat(routing.getBinding(1).keys.size()).isEqualTo(2);
        assertThat(routing.getBinding(1).keys).contains("#.blue.*");
        assertThat(routing.getBinding(1).keys).contains("#.orange.*");

    }

    @Test
    public void testParallelFilter4() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .thatMatches("location", "city")
                .thatMatches("location", "zoo")
                .thatMatches("category", "animal")
                .thatMatches("category", "car");

        Routing routing = Routing.create(filter);

        assertThat(routing.getChainSize()).isEqualTo(2);

        assertThat(routing.getBinding(0).keys.size()).isEqualTo(2);
        assertThat(routing.getBinding(0).keys).contains("animal.*.#");
        assertThat(routing.getBinding(0).keys).contains("car.*.#");

        assertThat(routing.getBinding(1).keys.size()).isEqualTo(2);
        assertThat(routing.getBinding(1).keys).contains("#.city");
        assertThat(routing.getBinding(1).keys).contains("#.zoo");

    }

    @Test
    public void testParallelFilter5() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .thatMatches("location", "city")
                .thatMatches("location", "zoo")
                .thatMatches("color", "blue")
                .thatMatches("color", "orange")
                .thatMatches("category", "animal")
                .thatMatches("category", "car");

        Routing routing = Routing.create(filter);

        assertThat(routing.getChainSize()).isEqualTo(3);

        assertThat(routing.getBinding(0).keys.size()).isEqualTo(2);
        assertThat(routing.getBinding(0).keys).contains("animal.#");
        assertThat(routing.getBinding(0).keys).contains("car.#");

        assertThat(routing.getBinding(1).keys.size()).isEqualTo(2);
        assertThat(routing.getBinding(1).keys).contains("#.blue.#");
        assertThat(routing.getBinding(1).keys).contains("#.orange.#");

        assertThat(routing.getBinding(2).keys.size()).isEqualTo(2);
        assertThat(routing.getBinding(2).keys).contains("#.city");
        assertThat(routing.getBinding(2).keys).contains("#.zoo");

    }

    @Test
    public void testParallelFilter6() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .thatMatches("location", "city")
                .thatMatches("location", "zoo")
                .thatMatches("color", "orange")
                .thatMatches("category", "animal")
                .thatMatches("category", "car");

        Routing routing = Routing.create(filter);

        assertThat(routing.getChainSize()).isEqualTo(2);

        assertThat(routing.getBinding(0).keys.size()).isEqualTo(2);
        assertThat(routing.getBinding(0).keys).contains("animal.orange.#");
        assertThat(routing.getBinding(0).keys).contains("car.orange.#");

        assertThat(routing.getBinding(1).keys.size()).isEqualTo(2);
        assertThat(routing.getBinding(1).keys).contains("#.city");
        assertThat(routing.getBinding(1).keys).contains("#.zoo");

    }
}
