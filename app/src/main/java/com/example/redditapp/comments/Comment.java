package com.example.redditapp.comments;

import android.util.Log;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Comment {

    private static final String TAG = "Comment";

    private String comment;
    private String author;
    private String updated;
    private String id;

    public Comment(String comment, String author, String updated, String id) {
        this.comment = comment;
        this.author = author;
        this.updated = updated;
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUpdated() {
        return updated;
    }

    public String getUpdatedFormatted() {
        String result = updated;
        //On API >26 show pretty Date and Time
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                //convert string to ZonedDateTime
                ZoneId zoneId = ZoneId.systemDefault();
                ZonedDateTime date = ZonedDateTime.parse(result).withZoneSameInstant(zoneId);
                //convert to Local Date Time
                LocalDateTime localDateTime = date.toLocalDateTime();
                //create a formatter for pretty Date Time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
                //format
                result = localDateTime.format(formatter);
            } catch (DateTimeParseException e) {
                Log.e(TAG, "getUpdatedFormatted: " + e + "\nUpdated: " + updated);
            }
        }
        return result;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", author='" + author + '\'' +
                ", updated='" + updated + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
