package nz.gen.vogt.rabbitpaw.demo;

import nz.gen.vogt.rabbitpaw.core.annotation.RoutingField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by johannes on 15/08/15.
 */
public class Article implements Serializable {

    @RoutingField
    private String publisher;

    @RoutingField
    private String category;

    @RoutingField
    private String author;

    @RoutingField
    private Date year;

    public Article(String publisher, String category, String author, Date date) {
        this.publisher = publisher;
        this.category = category;
        this.author = author;
        this.year = date;
    }

    @Override
    public String toString() {
        return "Article{" +
                "publisher='" + publisher + '\'' +
                ", category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                '}';
    }
}
