package com.example.admin.prototypekidzup1.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.admin.prototypekidzup1.R;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class StartActivity extends AppCompatActivity {

    private Button registerButton;
    private Button loginButton;
private SignInButton googleButton;
private static final int RC_SIGN_IN = 1;
private GoogleApiClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        registerButton = findViewById(R.id.start_register_button);
        loginButton = findViewById(R.id.start_signin_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regIntent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent (StartActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });


    }





}
