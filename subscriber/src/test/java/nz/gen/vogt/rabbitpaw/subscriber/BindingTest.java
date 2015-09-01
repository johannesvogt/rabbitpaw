package nz.gen.vogt.rabbitpaw.subscriber;

import nz.gen.vogt.rabbitpaw.core.annotation.RoutingField;
import nz.gen.vogt.rabbitpaw.core.annotation.Message;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by johannes on 17/08/15.
 */
public class BindingTest {

    @Message
    private static class TestMessage {
        @RoutingField
        private String category;
        @RoutingField
        private Object location;
        @RoutingField
        private String color;
    }

    @Test
    public void testEmptyFilter() {
        MessageFilter filter = MessageFilter.of(TestMessage.class);

        Binding binding = Binding.of(filter);

        assertThat(binding.getChainSize()).isEqualTo(1);

        assertThat(binding.getFirstStage().keys.size()).isEqualTo(1);

        assertThat(binding.getFirstStage().keys.get(0)).isEqualTo("*.*.*");
    }

    @Test
    public void testSimpleFilter() {
        MessageFilter filter = MessageFilter.of(TestMessage.class).setParameter("color", "blue");

        Binding binding = Binding.of(filter);

        assertThat(binding.getChainSize()).isEqualTo(1);

        assertThat(binding.getFirstStage().keys.size()).isEqualTo(1);

        assertThat(binding.getFirstStage().keys.get(0)).isEqualTo("*.blue.*");
    }

    @Test
    public void testSimpleFilter2() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .setParameter("color", "blue")
                .setParameter("category", "animal")
                .setParameter("location", "sea");

        Binding binding = Binding.of(filter);

        assertThat(binding.getChainSize()).isEqualTo(1);

        assertThat(binding.getFirstStage().keys.size()).isEqualTo(1);

        assertThat(binding.getFirstStage().keys.get(0)).isEqualTo("animal.blue.sea");
    }

    @Test
    public void testParallelFilter() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .setParameter("color", "blue", "orange");

        Binding binding = Binding.of(filter);

        assertThat(binding.getChainSize()).isEqualTo(1);

        assertThat(binding.getFirstStage().keys.size()).isEqualTo(2);

        assertThat(binding.getFirstStage().keys).contains("*.blue.*");
        assertThat(binding.getFirstStage().keys).contains("*.orange.*");
    }

    @Test
    public void testParallelFilter2() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .setParameter("color", "blue", "orange")
                .setParameter("location", "zoo", "city");

        Binding binding = Binding.of(filter);

        assertThat(binding.getChainSize()).isEqualTo(2);

        assertThat(binding.getFirstStage().keys.size()).isEqualTo(2);
        assertThat(binding.getFirstStage().keys).contains("*.blue.*");
        assertThat(binding.getFirstStage().keys).contains("*.orange.*");

        assertThat(binding.getStage(1).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(1).keys).contains("*.*.zoo");
        assertThat(binding.getStage(1).keys).contains("*.*.city");
    }

    @Test
    public void testParallelFilter3() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .setParameter("color", "blue", "orange")
                .setParameter("category", "animal", "car");

        Binding binding = Binding.of(filter);

        assertThat(binding.getChainSize()).isEqualTo(2);

        assertThat(binding.getStage(0).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(0).keys).contains("animal.*.*");
        assertThat(binding.getStage(0).keys).contains("car.*.*");

        assertThat(binding.getStage(1).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(1).keys).contains("*.blue.*");
        assertThat(binding.getStage(1).keys).contains("*.orange.*");

    }

    @Test
    public void testParallelFilter4() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .setParameter("location", "city", "zoo")
                .setParameter("category", "animal", "car");

        Binding binding = Binding.of(filter);

        assertThat(binding.getChainSize()).isEqualTo(2);

        assertThat(binding.getStage(0).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(0).keys).contains("animal.*.*");
        assertThat(binding.getStage(0).keys).contains("car.*.*");

        assertThat(binding.getStage(1).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(1).keys).contains("*.*.city");
        assertThat(binding.getStage(1).keys).contains("*.*.zoo");

    }

    @Test
    public void testParallelFilter5() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .setParameter("location", "city", "zoo")
                .setParameter("color", "blue", "orange")
                .setParameter("category", "animal", "car");

        Binding binding = Binding.of(filter);

        assertThat(binding.getChainSize()).isEqualTo(3);

        assertThat(binding.getStage(0).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(0).keys).contains("animal.*.*");
        assertThat(binding.getStage(0).keys).contains("car.*.*");

        assertThat(binding.getStage(1).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(1).keys).contains("*.blue.*");
        assertThat(binding.getStage(1).keys).contains("*.orange.*");

        assertThat(binding.getStage(2).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(2).keys).contains("*.*.city");
        assertThat(binding.getStage(2).keys).contains("*.*.zoo");

    }

    @Test
    public void testParallelFilter6() {
        MessageFilter filter = MessageFilter.of(TestMessage.class)
                .setParameter("location", "city", "zoo")
                .setParameter("color", "orange")
                .setParameter("category", "animal", "car");

        Binding binding = Binding.of(filter);

        assertThat(binding.getChainSize()).isEqualTo(2);

        assertThat(binding.getStage(0).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(0).keys).contains("animal.orange.*");
        assertThat(binding.getStage(0).keys).contains("car.orange.*");

        assertThat(binding.getStage(1).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(1).keys).contains("*.*.city");
        assertThat(binding.getStage(1).keys).contains("*.*.zoo");

    }

    @Message
    private static class TestMessage2 {
        @RoutingField
        private String category;
        @RoutingField
        private Object location;
        @RoutingField
        private String color;
        @RoutingField
        private String year;
    }

    @Test
    public void testParallelFilter7() {
        MessageFilter filter = MessageFilter.of(TestMessage2.class)
                .setParameter("location", "city", "zoo")
                .setParameter("color", "orange")
                .setParameter("category", "animal", "car")
                .setParameter("year", "2015");

        Binding binding = Binding.of(filter);

        assertThat(binding.getChainSize()).isEqualTo(2);

        assertThat(binding.getStage(0).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(0).keys).contains("animal.orange.*.2015");
        assertThat(binding.getStage(0).keys).contains("car.orange.*.2015");

        assertThat(binding.getStage(1).keys.size()).isEqualTo(2);
        assertThat(binding.getStage(1).keys).contains("*.*.city.*");
        assertThat(binding.getStage(1).keys).contains("*.*.zoo.*");

    }
}
