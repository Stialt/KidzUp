package com.example.admin.prototypekidzup1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.prototypekidzup1.activity.StartActivity;
import com.example.admin.prototypekidzup1.activity.StatusActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

/**
 * Created by ADMIN on 28.11.2017.
 */

public class ProfileMain extends Fragment {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mUser;

    //Layout

    private ImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private TextView mEmail;
    private TextView mMode;
    private Button mChangeStatusButton;
    private Button mChangeImageButton;

    public static final int GALLERY_PICK = 1;
    public static final int RESULT_OK = -1;

    //Storage Firebase
    private StorageReference imageStorage;

    //Progress Dialog
    private ProgressDialog mprogressDialog;

    //Strings
    private String uid;
    private String email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_main, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDisplayImage = getActivity().findViewById(R.id.profile_image);
        mName = getActivity().findViewById(R.id.profile_display_name);
        mStatus = getActivity().findViewById(R.id.profile_display_status);
        mEmail = getActivity().findViewById(R.id.profile_email);
        mMode = getActivity().findViewById(R.id.profile_display_mode);
        mChangeStatusButton = getActivity().findViewById(R.id.profile_change_status_button);
        mChangeImageButton = getActivity().findViewById(R.id.profile_change_image_button);

        mUser = FirebaseAuth.getInstance().getCurrentUser();


        if (mUser == null) {
            Intent intent = new Intent(getActivity(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().startActivity(intent);
            getActivity().finish();

        } else {
            uid = mUser.getUid();
            email = mUser.getEmail();
        }
        imageStorage = FirebaseStorage.getInstance().getReference();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild("name")) {
                    Intent intent = new Intent(getActivity(), StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                String mode = dataSnapshot.child("mode").getValue().toString();


                mName.setText(name);
                mStatus.setText(status);
                mEmail.setText(email);
                mMode.setText(mode);
                if (!image.equals("default")) {
                    //Picasso.with(getActivity()).load(image).placeholder(R.drawable.default_profile).into(mDisplayImage);
                    Picasso.with(getActivity()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_profile).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getActivity()).load(image).placeholder(R.drawable.default_profile).into(mDisplayImage);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChangeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusSend = mStatus.getText().toString();
                Intent intent = new Intent(getActivity(), StatusActivity.class);
                intent.putExtra("status_value", statusSend);
                startActivity(intent);

            }
        });

        mChangeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

                // start picker to get image for cropping and then use the image in cropping activity
               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity());*/
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            // CropImage.activity(imageUri)
            //       .start(this);

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);


        } //if Gallery Picked

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mprogressDialog = new ProgressDialog(getActivity());
                mprogressDialog.setTitle("Uploading Image");
                mprogressDialog.setMessage("Please, wait while we upload and process your image");
                mprogressDialog.setCanceledOnTouchOutside(false);
                mprogressDialog.show();

                Uri resultUri = result.getUri();

                File thumbFilePath = new File(resultUri.getPath());

                String current_user_id = mUser.getUid();

                //Creating compressed Bitmap Image
                Bitmap compressedImageBitmap = null;
                try {
                    compressedImageBitmap = new Compressor(getActivity())
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumbFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Converting Bitmap image to byte form (stream)
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = imageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = imageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            //if Task is succesfull
                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumbDownloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {

                                        Map updateHashMap = new HashMap<>();
                                        updateHashMap.put("image",download_url);
                                        updateHashMap.put("thumb_image",thumbDownloadUrl);

                                        mUserDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mprogressDialog.dismiss();
                                                    Toast.makeText(getActivity(), "Succesfully Uploaded", Toast.LENGTH_LONG).show();

                                                } else {
                                                    Toast.makeText(getActivity(), "Upload of Image Failed", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        }); //downloading Url to database
                                    } else {
                                        Toast.makeText(getActivity(), "Upload of thumb Image failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }); //uploading thumb finished


                        } else {
                            //if Not
                            mprogressDialog.dismiss();
                            Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                }); //end of OnCompleteListener of adding file


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    } // end of onActivityResult

}
