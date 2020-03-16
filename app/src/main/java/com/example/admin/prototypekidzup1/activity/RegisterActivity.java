package com.example.admin.prototypekidzup1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admin.prototypekidzup1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout mPasswordRepeat;
    private Button createAccButton;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgDialog;
    private DatabaseReference mfirebaseDatabase;
    private Toolbar mToolBar;
    private String mode;
    private Spinner spinnerMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDisplayName = findViewById(R.id.reg_display_layout);
        mEmail = findViewById(R.id.reg_email_layout);
        mPassword = findViewById(R.id.reg_pass_layout);
        mPasswordRepeat = findViewById(R.id.reg_pass_layout_repeat);
        createAccButton = findViewById(R.id.reg_create_button);
        mAuth = FirebaseAuth.getInstance();
        mProgDialog = new ProgressDialog(this);
        spinnerMode = findViewById(R.id.register_spinner_mode);

        mToolBar = findViewById(R.id.register_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerMode.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        List<String> list = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,list);
        list.add("Adult");
        list.add("Child");
        spinnerMode.setAdapter(adapter);

        mode = "Adult";

        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) mode = "Child";
                else mode = "Adult";
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        createAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayName = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String pass = mPassword.getEditText().getText().toString();
                String pass_rep = mPasswordRepeat.getEditText().getText().toString();

                if (!pass.equals(pass_rep)) {
                    Toast.makeText(RegisterActivity.this, "Psswords are not same", Toast.LENGTH_LONG).show();
                }
                else if (!TextUtils.isEmpty(displayName) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {
                    mProgDialog.setTitle("Registering User");
                    mProgDialog.setMessage("Whait a moment while we creating your account");
                    mProgDialog.setCanceledOnTouchOutside(false);
                    mProgDialog.show();

                    register_user(displayName, email, pass, mode);
                }


            }
        });




    }

    private void register_user(final String displayName, String email, String pass, final String mode) {

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mfirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name",displayName);
                    userMap.put("status", "Hi there! I am using KidzUp Application!");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");
                    userMap.put("device_token", deviceToken);
                    userMap.put("mode", mode);

                    mfirebaseDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                SharedPreferences sharedPreferences = getSharedPreferences("Local DB", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("my_mode", mode);
                                editor.apply();

                                mProgDialog.dismiss();

                                Intent intentMain = new Intent(RegisterActivity.this, MainActivity.class);
                                intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intentMain);
                                finish();
                            }

                        }
                    });


                } else {

                    String error = "";
                    mProgDialog.hide();
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        error = "Weak Password";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error = "Invalid Email";
                    } catch (FirebaseAuthUserCollisionException e) {
                        error = "Email already in Use";
                    } catch (Exception e) {
                        error = "Unknown Exception";
                        e.printStackTrace();
                    }

                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();

                }

            }
        });

    }

}
