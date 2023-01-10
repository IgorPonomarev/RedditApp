package com.example.redditapp.model.entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Author's name and uri from the entry
 */
@Root(name = "author", strict = false)
public class Author implements Serializable {

    @Element(name = "name")
    private String name;

    @Element(name = "uri")
    private String uri;

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name.replace("/u/", "");
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
