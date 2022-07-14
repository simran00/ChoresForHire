package com.example.choresforhire.group;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;

@ParseClassName("Group")
public class Group extends ParseObject {
    public static final String KEY_NAME = "groupName";
    public static final String KEY_MEMBERS = "members";
    public static final String KEY_DESCRIPTION = "description";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public JSONArray getMembers() {
        return getJSONArray("members");
    }

    public void setMembers(JSONArray members) {
        put(KEY_MEMBERS, members);
    }
}
