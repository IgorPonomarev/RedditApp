package com.example.redditapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * XML Parser
 */
public class ExtractXML {

    private static final String TAG = "ExtractXML";

    private String xml;
    private String tag;
    private String endTag;

    public ExtractXML(String xml, String tag) {
        this.xml = xml;
        this.tag = tag;
        this.endTag = "NONE";
    }

    public ExtractXML(String xml, String tag, String endTag) {
        this.xml = xml;
        this.tag = tag;
        this.endTag = endTag;
    }

    public List<String> start() {
        List<String> result = new ArrayList<>();

        String[] splitXML = null;
        String marker = null;

        if (endTag.equals("NONE")) {
            marker = "\"";
            splitXML = xml.split(tag + marker);
        } else {
            marker = endTag;
            splitXML = xml.split(tag);
        }


        int count = splitXML.length;

        for (int i = 1; i < count; i++) {

            String temp = splitXML[i];

            //find end of link
            int index = temp.indexOf(marker);

            //Log.d(TAG, "start: index: " + index);
            //Log.d(TAG, "start: extracted: " + temp);

            //save link
            temp = temp.substring(0, index);

            //fix screen escaping
            temp = temp.replace("&amp;", "&");

            //Log.d(TAG, "start: snipped: " + temp);

            result.add(temp);
        }

        return result;
    }
}
