package com.example.redditapp.model;

import androidx.annotation.NonNull;

import com.example.redditapp.model.entry.Entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

/**
 * RSS feed from Reddit converted to Java class
 */
@Root(name = "feed", strict = false)
public class Feed implements Serializable {

    @Element(name = "icon")
    private String icon;

    @Element(name = "id")
    private String id;

    @Element(name = "logo")
    private String logo;

    @Element(name = "title")
    private String title;

    @Element(name = "updated")
    private String updated;

    @Element(name = "subtitle")
    private String subtitle;

    @ElementList(inline = true, name = "entry")
    private List<Entry> entrys;

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setEntrys(List<Entry> entrys) {
        this.entrys = entrys;
    }

    public String getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public String getLogo() {
        return logo;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdated() {
        return updated;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<Entry> getEntrys() {
        return entrys;
    }

    @NonNull
    @Override
    public String toString() {
        return "Feed: \n [Entrys : " + entrys + "]";
    }
}
