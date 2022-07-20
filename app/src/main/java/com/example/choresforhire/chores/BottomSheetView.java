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
    Post mPost;
    Button mDelete;
    Button mCancel;
    View mBottomSheetView;

    public BottomSheetView(LayoutInflater inflater, @Nullable
            ViewGroup container, Post post) {
        mBottomSheetView = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        mDelete = mBottomSheetView.findViewById(R.id.btnDelete);
        mCancel = mBottomSheetView.findViewById(R.id.btnBottomSheetCancel);
        this.mPost = post;

    }

    public View getView() {
        return mBottomSheetView;
    }

    public void initBottomSheetListener(BottomSheetController controller) {
        mDelete.setOnClickListener(v -> controller.onDeleteClicked(mPost));

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onCancelClicked(mPost);
            }
        });
    }
}
