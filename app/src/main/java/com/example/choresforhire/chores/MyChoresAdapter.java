package com.example.choresforhire.chores;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choresforhire.R;
import com.example.choresforhire.post.Post;
import com.example.choresforhire.chores.BottomSheetDialog;
import com.example.choresforhire.post.PostDetails;

import java.io.Serializable;
import java.util.List;

public class MyChoresAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    private Context context;
    private List<Post> posts;

    public MyChoresAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public int getItemViewType(int position) {
        if (posts.get(position).isShowMenu()) {
            return SHOW_MENU;
        } else {
            return HIDE_MENU;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == SHOW_MENU) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_chores_swipe_menu, parent, false);
            return new MenuViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.my_chores_item, parent, false);
            return new MyChoresViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(holder.getAbsoluteAdapterPosition());
        if (holder instanceof MyChoresViewHolder) {
            ((MyChoresViewHolder)holder).mTitle.setText(post.getTitle());
            ((MyChoresViewHolder) holder).mPay.setText("$" + String.valueOf(post.getPay()));
            ((MyChoresViewHolder) holder).mDescription.setText(post.getDescription());

            ((MyChoresViewHolder)holder).container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu(holder.getAbsoluteAdapterPosition());
                    return true;
                }
            });
        }

        if (holder instanceof MenuViewHolder) {
            ((MenuViewHolder) holder).mTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog bottomSheet = new BottomSheetDialog();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("post", post);
                    bottomSheet.setArguments(bundle);

                    bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(),
                            "ModalBottomSheet");
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void showMenu(int position) {
        for(int i=0; i < posts.size(); i++){
            posts.get(i).setShowMenu(false);
        }
        posts.get(position).setShowMenu(true);
        notifyDataSetChanged();
    }


    public boolean isMenuShown() {
        for(int i=0; i<posts.size(); i++){
            if(posts.get(i).isShowMenu()){
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for(int i = 0; i < posts.size(); i++){
            posts.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }


    public class MyChoresViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mPay;
        private TextView mDescription;
        private ConstraintLayout container;


        public MyChoresViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.myChoreTitle);
            mPay = itemView.findViewById(R.id.myChorePay);
            mDescription = itemView.findViewById(R.id.myChoreDescription);
            container = itemView.findViewById(R.id.container);
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mTrash;

        public MenuViewHolder(View view) {
            super(view);
            mTrash = itemView.findViewById(R.id.swipeTrash);
        }
    }
}
