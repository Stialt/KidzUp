package com.example.admin.prototypekidzup1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admin.prototypekidzup1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusActivity extends AppCompatActivity {

    // private Toolbar mToolBar;
    private TextInputLayout mStatus;
    private Button mSaveButton;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mUser;

    private String status;

    private ProgressDialog mProgress;
    private Toolbar mToolBar;

    private Spinner spinnerMode;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //mToolBar = findViewById(R.id.status_appBar);
        //setSupportActionBar(mToolBar);
        mToolBar = findViewById(R.id.status_app_bar);
        spinnerMode = findViewById(R.id.status_spinner_mode);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String status_value = getIntent().getStringExtra("status_value");

        //Firebase initilization
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        //elements initialization
        mStatus = findViewById(R.id.status_input_layout);
        mSaveButton = findViewById(R.id.status_save_changes_button);


        spinnerMode.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        List<String> list = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list);
        list.add("Adult");
        list.add("Child");
        spinnerMode.setAdapter(adapter);


        mode = "Adult";

        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) mode = "Child";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //Setting ol value
        //Note that status and status_value both can be used,
        //status - uploaded from server
        //status_value sent from previous activity - preferred
        mStatus.getEditText().setText(status_value);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving status");
                mProgress.setMessage("Please, wait while we saving your status");
                mProgress.show();

                status = mStatus.getEditText().getText().toString();

                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                Map userMap = new HashMap();
                userMap.put("status",status);
                userMap.put("mode", mode);

                mUserDatabase.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //if Task is sucesfull
                            SharedPreferences sharedPreferences = getSharedPreferences("Local DB", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("my_mode", mode);
                            editor.apply();
                            mProgress.dismiss();
                        } else {
                            //if task in NOT succesfull
                            Toast.makeText(getApplicationContext(), "Error in updating changes, please, try again or try later", Toast.LENGTH_LONG).show();
                        }
                    } //end of OnComplete
                }); //end of OnCompleteListener

            } //end of OnClick
        });  //en of Save Button


    }

    @Override
    protected void onStart() {
        super.onStart();
        String User = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(User).child("online").setValue("true");
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        String User = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(User).child("online").setValue(false);
    }*/
}
