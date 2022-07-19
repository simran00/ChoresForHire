package com.example.choresforhire.chores;

import static com.example.choresforhire.chores.MyChoresFragment.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.choresforhire.R;
import com.example.choresforhire.post.Post;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private Post post;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
        1. Create Bottomsheet Controller -> 2 click handler -> expose 2 method: 1 onDeleteClicked 2. OnClickClicked
        2. Create BottomSheet View -> layout -> inflate, populate buttons
        3. In BottomSheetView, have a new method called initBottomSheet(Controller) -> hook up onCLick to Controller corresponding onCLick(onDeleteClicked/onCancelClick)

         */

        post = getArguments().getParcelable("post");

        BottomSheetController controller = new BottomSheetController(this);
        BottomSheetView btmSheetView = new BottomSheetView(inflater, container, post);
        btmSheetView.initBottomSheetListener(controller);
        return btmSheetView.getView();
    }
}
