package com.example.redditapp.model.entry;

import androidx.annotation.NonNull;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Entry from the feed. Each entry is equal to a post
 */
@Root(name = "entry", strict = false)
public class Entry implements Serializable {

    @Element(name = "author", required = false)
    private Author author;

    @Element(name = "content")
    private String content;

    @Element(name = "id")
    private String id;

    @Element(name = "title")
    private String title;

    @Element(name = "updated")
    private String updated;

    public Entry() {
    }

    public Entry(Author author, String content, String title, String update) {
        this.author = author;
        this.content = content;
        this.title = title;
        this.updated = update;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Author getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdated() {
        return updated;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "author=" + author +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", updated='" + updated + '\'' +
                "} \n" +
                "--------------------------------------------------------\n";
    }
}
