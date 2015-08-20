package jv.rabbitfilter.demo;

import jv.rabbitfilter.core.annotation.Filterable;

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
    private String year;

    public Article(String publisher, String category, String author, String date) {
        this.publisher = publisher;
        this.category = category;
        this.author = author;
        this.year = date;
    }
}
