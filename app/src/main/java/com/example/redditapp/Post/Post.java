package com.example.redditapp.Post;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Post {

    private String title;
    private String author;
    private String date_updated;
    private String postURL;
    private String thumbnailURL;
    private String id;

    public Post(String title, String author, String date_updated, String postURL, String thumbnailURL, String id) {
        this.title = title;
        this.author = author;
        this.date_updated = date_updated;
        this.postURL = postURL;
        this.thumbnailURL = thumbnailURL;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public String getDate_updatedFormatted() {
        String result = date_updated;
        //On API >26 show pretty Date and Time
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //convert string to ZonedDateTime
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime date = ZonedDateTime.parse(result).withZoneSameInstant(zoneId);
            //convert to Local Date Time
            LocalDateTime localDateTime = date.toLocalDateTime();
            //create a formatter for pretty Date Time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
            //format
            result = localDateTime.format(formatter);
        }
        return result;
    }

    public void setDate_updated(String date_updated) {
        this.date_updated = date_updated;
    }

    public String getPostURL() {
        return postURL;
    }

    public void setPostURL(String postURL) {
        this.postURL = postURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
