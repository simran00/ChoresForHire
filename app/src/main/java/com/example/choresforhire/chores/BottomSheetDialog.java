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

import com.example.choresforhire.R;
import com.example.choresforhire.post.Post;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

//beter to rename it more descriptive
public class BottomSheetDialog extends BottomSheetDialogFragment {
    private Post post;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_layout,
                container, false);

        post = getArguments().getParcelable("post");

        Button mDelete = v.findViewById(R.id.btnDelete);
        Button mCancel = v.findViewById(R.id.btnBottomSheetCancel);

        // Notify parent fragment
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (post != null) {
                    Bundle result = new Bundle();
                    result.putParcelable("post", post);
                    getParentFragmentManager().setFragmentResult("postDelete", result);
                }
                dismiss();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post != null) {
                    Bundle result = new Bundle();
                    result.putParcelable("post", post);
                    getParentFragmentManager().setFragmentResult("closeMenu", result);
                }
                dismiss();
            }
        });
        return v;
    }
}
