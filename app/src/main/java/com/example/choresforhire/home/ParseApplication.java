package com.example.choresforhire.home;

import android.app.Application;

import com.example.choresforhire.chat.Message;
import com.example.choresforhire.group.Group;
import com.example.choresforhire.group.GroupPost;
import com.example.choresforhire.post.Post;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.ArrayList;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register parse models
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(GroupPost.class);

        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("YnFKMkzAJ0sfFewGMmlswvWUhf7Y4SRoTTSTXFlN") // should correspond to Application Id env variable
                .clientKey("t7xfZc1mRGYgkACQ0O6sM8Xljmq45M0hi3cQAUee")  // should correspond to Client key env variable
                .server("https://parseapi.back4app.com")
                .enableLocalDataStore()
                .build());

        ArrayList<String> channels = new ArrayList<>();
        channels.add("News");
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.put("GCMSenderId", "830679742091");
        installation.put("channels", channels);
        installation.saveInBackground();
    }
}
