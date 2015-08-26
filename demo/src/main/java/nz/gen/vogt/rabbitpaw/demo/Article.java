package nz.gen.vogt.rabbitpaw.demo;

import nz.gen.vogt.rabbitpaw.core.annotation.Filterable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by johannes on 15/08/15.
 */
public class Article implements Serializable {

    @Filterable
    private String publisher;

    @Filterable
    private String category;

    @Filterable
    private String author;

    @Filterable
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
