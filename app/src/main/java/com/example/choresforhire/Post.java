package com.example.choresforhire;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_PAY = "pay";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "user";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_ACCEPTED = "accepted";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public Integer getPay() {
        return getInt(KEY_PAY);
    }

    public void setPay(Integer title) {
        put(KEY_PAY, title);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseGeoPoint getLocation() {return getParseGeoPoint(KEY_LOCATION); }

    public void setLocation(ParseGeoPoint location) { put(KEY_LOCATION, location); }

    public ParseUser getAccepted() { return getParseUser(KEY_ACCEPTED); }

    public void setAccepted(ParseUser acceptedUser) { put(KEY_ACCEPTED, acceptedUser); }
}
