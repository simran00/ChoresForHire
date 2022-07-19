package com.example.choresforhire.chores;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.choresforhire.post.Post;

public class BottomSheetController {
    DialogFragment fragment;

    public BottomSheetController(DialogFragment fragment) {
        this.fragment = fragment;
    }

    public void onDeleteClicked(Post post) {
        if (post != null) {

            Bundle result = new Bundle();
            result.putParcelable("post", post);
            fragment.getParentFragmentManager().setFragmentResult("postDelete", result);
            fragment.dismiss();
        }
    }

    public void onCancelClicked(Post post) {
        if (post != null) {
            Bundle result = new Bundle();
            result.putParcelable("post", post);
            fragment.getParentFragmentManager().setFragmentResult("closeMenu", result);
            fragment.dismiss();
        }
    }
}
