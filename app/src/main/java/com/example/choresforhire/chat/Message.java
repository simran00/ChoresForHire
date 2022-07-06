package com.example.choresforhire.chat;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_BODY = "body";
    public static final String KEY_RECEIVER = "receiver";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseUser getReceiver() {
        return getParseUser(KEY_RECEIVER);
    }

    public void setReceiver(ParseUser receiver) {
        put(KEY_RECEIVER, receiver);
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }



}
