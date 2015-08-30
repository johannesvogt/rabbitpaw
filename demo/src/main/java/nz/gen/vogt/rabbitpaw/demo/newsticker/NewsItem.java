package nz.gen.vogt.rabbitpaw.demo.newsticker;

import nz.gen.vogt.rabbitpaw.core.annotation.RoutingField;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Johannes Vogt on 15/08/15.
 */
public class NewsItem implements Serializable {

    @RoutingField
    private Newspaper newspaper;

    @RoutingField
    private Category category;

    @RoutingField
    private String author;

    @RoutingField
    private Date year;

    private String title;

    private String content;

    public NewsItem(Newspaper newspaper, Category category, String author, Date year, String title, String content) {
        this.newspaper = newspaper;
        this.category = category;
        this.author = author;
        this.year = year;
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "newspaper='" + newspaper + '\'' +
                ", category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    static enum Newspaper {
        NYT,
        WAPO,
        NYPOST,
        LATIMES;

        private static final List<Newspaper> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Newspaper randomNewspaper()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    static enum Category {
        BUSINESS,
        POLITICS,
        SPORTS,
        SCIENCE;

        private static final List<Category> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Category randomCategory()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

}
