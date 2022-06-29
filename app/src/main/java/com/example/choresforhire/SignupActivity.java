package com.example.choresforhire;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etUsername;
    private Button btnSignup;
    private ParseUser currUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currUser = new ParseUser();

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String email = etEmail.getText().toString();

                currUser.setEmail(email);
                currUser.setUsername(username);
                currUser.setPassword(password);

                currUser.signUpInBackground(new SignUpCallback() {
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
