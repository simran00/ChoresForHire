package com.example.choresforhire.post;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_PAY = "pay";
    public static final String KEY_USER = "user";
    public static final String KEY_TITLE = "title";
    public static final String KEY_AGE = "overEighteen";
    public static final String KEY_ONE_TIME = "oneTime";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_ACCEPTED = "accepted";
    public static final String KEY_RECURRING = "recurring";
    public static final String KEY_COMPLETED = "completedTask";
    public static final String KEY_DESCRIPTION = "description";
    public boolean showMenu = false;

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public int getPay() {
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

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint location) {
        put(KEY_LOCATION, location);
    }

    public ParseUser getAccepted() {
        return getParseUser(KEY_ACCEPTED);
    }

    public void setAccepted(ParseUser acceptedUser) {
        put(KEY_ACCEPTED, acceptedUser);
    }

    public boolean isCompleted() {
        return getBoolean(KEY_COMPLETED);
    }

    public void setCompleted(boolean completedTask) {
        put(KEY_COMPLETED, completedTask);
    }

    public boolean isAgeRestricted() {
        return getBoolean(KEY_AGE);
    }

    public void setAgeRestriction(boolean completedTask) {
        put(KEY_AGE, completedTask);
    }

    public boolean isOneTime() {
        return getBoolean(KEY_ONE_TIME);
    }

    public void setOneTime(boolean completedTask) {
        put(KEY_ONE_TIME, completedTask);
    }

    public boolean isRecurring() {
        return getBoolean(KEY_RECURRING);
    }

    public void setRecurring(boolean completedTask) {
        put(KEY_RECURRING, completedTask);
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }
}
