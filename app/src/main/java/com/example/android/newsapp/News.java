package com.example.android.newsapp;

import android.graphics.Bitmap;

import java.net.URL;

public class News {
    private String title;
    private String section;
    private String author;
    private String date;
    private Bitmap image;
    private URL url;

    public News(String title, String section, String author, String date, Bitmap image, URL url) {
        this.title = title;
        this.section = section;
        this.author = author;
        this.date = date;
        this.image = image;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date.substring(0, date.indexOf("T"));
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
