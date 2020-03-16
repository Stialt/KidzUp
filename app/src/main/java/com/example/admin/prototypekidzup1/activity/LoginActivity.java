package com.example.admin.prototypekidzup1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.prototypekidzup1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPass;

    private Button mLoginButton;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private Toolbar mToolBar;

    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mToolBar = findViewById(R.id.login_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);
        mLoginEmail = findViewById(R.id.log_email_layout);
        mLoginPass = findViewById(R.id.log_pass_layout);
        mLoginButton = findViewById(R.id.log_login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmail.getEditText().getText().toString();
                String pass = mLoginPass.getEditText().getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {

                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("Please, wait while we check your information.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    loginUser(email, pass);

                }

            }
        });
    }

    private void loginUser(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    progressDialog.dismiss();

                    final String currenUid = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    userDatabase.child(currenUid).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref = ref.child("Users").child(currenUid);
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String mode = dataSnapshot.child("mode").getValue().toString();
                                        SharedPreferences sharedPreferences = getSharedPreferences("Local DB", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("my_mode", mode);
                                        editor.apply();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });


                            } else {
                                Toast.makeText(LoginActivity.this, "Unable to Login. Database Error, pleae try again or later", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } else {
                    progressDialog.hide();
                    Toast.makeText(LoginActivity.this, "Unable to Log in. Check your information and try again.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


}
