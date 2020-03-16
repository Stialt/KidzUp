package com.example.admin.prototypekidzup1.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.prototypekidzup1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText inputTitle;
    private EditText inputTaskDescription;
    private EditText inputPrizeText;
    private EditText inputPunishText;

    private ImageButton imageTaskButton;
    private ImageButton imagePrizeButton;
    private ImageButton imagePunishButton;

    private Button buttonSubmitTask;
    private TextView textViewTitle;

    private DatePicker datePicker;

    private Toolbar mToolBar;

    private String user_id;
    private String userName;
    private String myUid;

    private static final int TASKIMAGE = 0;
    private static final int PRIZEIMAGE = 1;
    private static final int PUNISHIMAGE = 2;
    private int currentImage = TASKIMAGE;
    private Uri taskImageUri;
    private Uri prizeImageUri;
    private Uri punishImageUri;
    private Drawable taskImageDrawable;
    private Drawable prizeImageDrawable;
    private Drawable punishImageDrawable;
    private String taskImageLink;
    private String prizeImageLink;
    private String punishImageLink;
    private Uri defaultUri;

    private StorageReference firebaseStorage;
    private DatabaseReference rootRef;

    private ProgressDialog mprogressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        inputTitle = findViewById(R.id.activity_create_task_title);
        inputTaskDescription = findViewById(R.id.activity_create_task_description);
        inputPrizeText = findViewById(R.id.activity_create_task_prize_text);
        inputPunishText = findViewById(R.id.activity_create_task_punish_text);

        imageTaskButton = findViewById(R.id.activity_create_task_attach_main_image);
        imagePrizeButton = findViewById(R.id.activity_create_task_attch_prize_icon);
        imagePunishButton = findViewById(R.id.activity_create_task_attach_punish_icon);

        datePicker = findViewById(R.id.activity_create_task_input_date);

        buttonSubmitTask = findViewById(R.id.activity_create_task_submit_button);

        rootRef = FirebaseDatabase.getInstance().getReference();

        textViewTitle = findViewById(R.id.activity_create_task_text_title_to_user);

        mToolBar = findViewById(R.id.activity_create_task_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user_id = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("userName");

        Uri uri = Uri.parse("android.resource://com.example.admin.prototypekidzup1/drawable/icon_attach_image");
        defaultUri = uri;
        taskImageUri = punishImageUri = prizeImageUri = uri;

        textViewTitle.setText("Task for " + userName);

        //Progress Dialog
        mprogressDialog = new ProgressDialog(this);
        mprogressDialog.setTitle("Uploading Task");
        mprogressDialog.setMessage("Please, wait while we upload and process your task");
        mprogressDialog.setCanceledOnTouchOutside(false);

        imageTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentImage = TASKIMAGE;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(CreateTaskActivity.this);
            }
        });

        imagePrizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentImage = PRIZEIMAGE;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(CreateTaskActivity.this);
            }
        });
        imagePunishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentImage = PUNISHIMAGE;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(CreateTaskActivity.this);
            }
        });

        firebaseStorage = FirebaseStorage.getInstance().getReference();
        firebaseStorage = firebaseStorage.child("tasks_images").child(myUid);
        buttonSubmitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(inputTitle.getText().toString())) {
                    Toast.makeText(CreateTaskActivity.this, "Title is neccessary", Toast.LENGTH_LONG).show();

                } else {
                    mprogressDialog.show();
                    final String a = Long.toString(System.currentTimeMillis());
                    firebaseStorage.child(a + "image1").putFile(taskImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                taskImageLink = task.getResult().getDownloadUrl().toString();
                                firebaseStorage.child(a + "image2").putFile(prizeImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            prizeImageLink = task.getResult().getDownloadUrl().toString();
                                            firebaseStorage.child(a + "image3").putFile(punishImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        punishImageLink = task.getResult().getDownloadUrl().toString();
                                                        Map taskMap = new HashMap();
                                                        taskMap.put("title", inputTitle.getText().toString());
                                                        taskMap.put("icon_path", taskImageLink);
                                                        taskMap.put("prize_text", inputPrizeText.getText().toString());
                                                        taskMap.put("prize_icon_path", prizeImageLink);
                                                        taskMap.put("punish_text", inputPunishText.getText().toString());
                                                        taskMap.put("punish_icon_path", punishImageLink);
                                                        taskMap.put("text", inputTaskDescription.getText().toString());
                                                        //taskMap.put("create_date", ServerValue.TIMESTAMP);
                                                        //expire date
                                                        taskMap.put("from", myUid);
                                                        taskMap.put("to", user_id);
                                                        taskMap.put("seen", "false");

                                                        String push_id = rootRef.child("Tasks").child(myUid).child("incomplete").push().getKey();
                                                        Map allMapsMap = new HashMap();
                                                        allMapsMap.put("Tasks/" + myUid + "/incomplete/" + push_id, taskMap);
                                                        allMapsMap.put("Tasks/" + myUid + "/all/" + push_id, taskMap);
                                                        allMapsMap.put("Tasks/" + user_id + "/incomplete/" + push_id, taskMap);
                                                        allMapsMap.put("Tasks/" + user_id + "/all/" + push_id, taskMap);

                                                        rootRef.updateChildren(allMapsMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                mprogressDialog.dismiss();
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(CreateTaskActivity.this, "Task sent succesfully", Toast.LENGTH_LONG).show();
                                                                    Intent intent = new Intent(CreateTaskActivity.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else
                                                                    Toast.makeText(CreateTaskActivity.this, "Error. Task not sent", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            /*
                                            |----Title
                                            |----Icon
                                            |----prize_text
                                            |----prize_icon
                                            |----punishment_text
                                            |----punishment_icon
                                            |----Text
                                            |----creation_date
                                            |----expire_date
                                            |----from
                                            |----to
                                            |----seen: true / false*/

                                                    } else {
                                                        mprogressDialog.dismiss();
                                                        Toast.makeText(CreateTaskActivity.this, "Undefined Error occured, task not sent", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            mprogressDialog.dismiss();
                                            Toast.makeText(CreateTaskActivity.this, "Undefined Error occured, task not sent", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {

                                mprogressDialog.dismiss();
                                Toast.makeText(CreateTaskActivity.this, "Undefined Error occured, task not sent", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            } //end of onclick
        }); //end of listener


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //---------------Task Image-------------------
                if (currentImage == TASKIMAGE) {
                    try {
                        taskImageUri = resultUri;
                        InputStream inputStream = getContentResolver().openInputStream(resultUri);
                        taskImageDrawable = Drawable.createFromStream(inputStream, resultUri.toString());
                        imageTaskButton.setImageDrawable(taskImageDrawable);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(CreateTaskActivity.this, "File cannot be opened or doesn't exist", Toast.LENGTH_LONG).show();
                    }

                }
                //--------------------------Prize Image-------------------------
                else if (currentImage == PRIZEIMAGE) {
                    try {
                        prizeImageUri = resultUri;
                        InputStream inputStream = getContentResolver().openInputStream(resultUri);
                        prizeImageDrawable = Drawable.createFromStream(inputStream, resultUri.toString());
                        imagePrizeButton.setImageDrawable(prizeImageDrawable);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(CreateTaskActivity.this, "File cannot be opened or doesn't exist", Toast.LENGTH_LONG).show();
                    }


                }
                //-----------------------------Punish Image -------------------------------
                else if (currentImage == PUNISHIMAGE) {
                    try {
                        punishImageUri = resultUri;
                        InputStream inputStream = getContentResolver().openInputStream(resultUri);
                        punishImageDrawable = Drawable.createFromStream(inputStream, resultUri.toString());
                        imagePunishButton.setImageDrawable(punishImageDrawable);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(CreateTaskActivity.this, "File cannot be opened or doesn't exist", Toast.LENGTH_LONG).show();
                    }

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
