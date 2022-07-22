package com.example.choresforhire.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.choresforhire.R;
import com.parse.ParseFile;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private String mUserId;
    private Context mContext;
    private List<Message> mMessages;
    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;

    public ChatAdapter(Context context, String userId, List<Message> messages) {
        mMessages = messages;
        this.mUserId = userId;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING) {
            View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemViewType(int position) {
        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position) {
        Message message = mMessages.get(position);
        return message.getUser() != null && message.getUser().getObjectId().equals(mUserId);
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        abstract void bindMessage(Message message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {
        TextView mBody;
        TextView mName;
        ImageView mImageOther;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            mImageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            mBody = (TextView)itemView.findViewById(R.id.tvBody);
            mName = (TextView)itemView.findViewById(R.id.tvName);
        }

        @Override
        public void bindMessage(Message message) {
            ParseFile profilePic = (ParseFile) message.getUser().get("profilePic");
            if (profilePic != null) {
                Glide.with(mContext).load(profilePic.getUrl()).circleCrop().into(mImageOther);
            } else {
                int drawableIdentifier = mContext.getResources().getIdentifier("blank_profile", "drawable", mContext.getPackageName());
                Glide.with(mContext).load(drawableIdentifier).circleCrop().into(mImageOther);
            }
            mBody.setText(message.getBody());
            mName.setText(message.getUser().getUsername());
        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {
        TextView mBody;
        ImageView mImageMe;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            mImageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            mBody = (TextView)itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bindMessage(Message message) {
            ParseFile profilePic = (ParseFile) message.getUser().get("profilePic");
            if (profilePic != null) {
                Glide.with(mContext).load(profilePic.getUrl()).circleCrop().into(mImageMe);
            } else {
                int drawableIdentifier = mContext.getResources().getIdentifier("blank_profile", "drawable", mContext.getPackageName());
                Glide.with(mContext).load(drawableIdentifier).circleCrop().into(mImageMe);
            }
            mBody.setText(message.getBody());
        }
    }
}
