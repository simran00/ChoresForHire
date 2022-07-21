package com.example.choresforhire.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.choresforhire.R;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {
    public static final String TAG = "SignupActivity";

    private Button mBtnSignup;
    private ParseUser mCurrUser;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private TextInputEditText mUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmail = findViewById(R.id.etEmail);
        mUsername = findViewById(R.id.etUsername);
        mPassword = findViewById(R.id.etPassword);
        mBtnSignup = findViewById(R.id.btnSignup);

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrUser = new ParseUser();

                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                mCurrUser.setEmail(email);
                mCurrUser.setUsername(username);
                mCurrUser.setPassword(password);

                mCurrUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with signup", e);
                            Toast.makeText(SignupActivity.this, "Issue with signup!", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
            }
        });
    }
}
