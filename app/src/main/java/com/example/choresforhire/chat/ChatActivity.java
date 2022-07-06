package com.example.choresforhire.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choresforhire.login.LoginActivity;
import com.example.choresforhire.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {
    public static final String TAG = "ChatActivity";
    public static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    public static final long POLL_INTERVAL = TimeUnit.SECONDS.toMillis(3);

    boolean mFirstLoad;
    private EditText mMessage;
    private ImageButton ibSend;
    private ParseUser chatUser;
    private RecyclerView rvChat;
    private ChatAdapter mChatAdapter;
    private ArrayList<Message> mMessages;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                chatUser = (ParseUser) extras.get("user");
            }
        } else {
            chatUser = (ParseUser) savedInstanceState.getSerializable("user");
        }

        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new user
            goToLogin();
        }

        // Load existing messages to begin with
        refreshMessages();

        String websocketUrl = "wss://https://parseapi.back4app.com";

        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI(websocketUrl));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);

        // Connect to Parse server
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        // Listen for CREATE events on the Message class
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {
            mMessages.add(0, object);

            // RecyclerView updates need to be run on the UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mChatAdapter.notifyDataSetChanged();
                    rvChat.scrollToPosition(0);
                }
            });
        });
    }

    private void goToLogin() {
        Intent i = new Intent(ChatActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void startWithCurrentUser() {
        setupMessagePosting();
    }

    private void setupMessagePosting() {
        mMessage = (EditText) findViewById(R.id.etMessage);
        ibSend = (ImageButton) findViewById(R.id.ibSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mChatAdapter = new ChatAdapter(ChatActivity.this, userId, mMessages);
        rvChat.setAdapter(mChatAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = mMessage.getText().toString();
                Message message = new Message();
                message.setUser(ParseUser.getCurrentUser());
                message.setReceiver(chatUser);
                message.setBody(data);

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                Toast.LENGTH_SHORT).show();
                        refreshMessages();
                    }
                });
                mMessage.setText(null);
            }
        });
    }

    private void refreshMessages() {
        ParseQuery<Message> query1 = ParseQuery.getQuery(Message.class);
        ParseQuery<Message> query2 = ParseQuery.getQuery(Message.class);

        // query where user is chatUser and receiver is current user
        query1.whereEqualTo("user", chatUser);
        query1.whereEqualTo("receiver", ParseUser.getCurrentUser());

        // OR query where user is current user and receiver is chatUser
        query2.whereEqualTo("user", ParseUser.getCurrentUser());
        query2.whereEqualTo("receiver", chatUser);

        List<ParseQuery<Message>> queries = Arrays.asList(query1, query2);
        ParseQuery<Message> combinedQuery = ParseQuery.or(queries);

        combinedQuery.include(Message.KEY_USER);
        combinedQuery.include(Message.KEY_RECEIVER);
        combinedQuery.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        combinedQuery.orderByDescending("createdAt");

        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        combinedQuery.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mChatAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }
}
