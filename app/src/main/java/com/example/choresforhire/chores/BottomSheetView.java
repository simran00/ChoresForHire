package com.example.choresforhire.chores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.choresforhire.R;
import com.example.choresforhire.post.Post;

public class BottomSheetView {
    Button mDelete;
    Button mCancel;
    Post post;
    View bottomSheetView;

    public BottomSheetView(LayoutInflater inflater, @Nullable
            ViewGroup container, Post post) {
        bottomSheetView = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        mDelete = bottomSheetView.findViewById(R.id.btnDelete);
        mCancel = bottomSheetView.findViewById(R.id.btnBottomSheetCancel);
        this.post = post;

    }

    public View getView() {
        return bottomSheetView;
    }

    public void initBottomSheetListener(BottomSheetController controller) {
        mDelete.setOnClickListener(v -> controller.onDeleteClicked(post));

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onCancelClicked(post);
            }
        });
    }
}
