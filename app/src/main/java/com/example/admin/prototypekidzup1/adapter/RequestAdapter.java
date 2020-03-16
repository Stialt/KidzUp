package com.example.admin.prototypekidzup1.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.prototypekidzup1.R;
import com.example.admin.prototypekidzup1.Requests;
import com.example.admin.prototypekidzup1.activity.AnotherProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ADMIN on 03.12.2017.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>
{


    private List<Requests> mRequestList;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentKey;
    private String mode;
    View v;

    public RequestAdapter(List<Requests> mRequestList) {
        this.mRequestList = mRequestList;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RequestViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        final Requests c = mRequestList.get(position);

        String current_user_id = mAuth.getCurrentUser().getUid();
        final String from_user_id = c.getFrom();
        final Long time = c.getTime();
        String type = c.getType();

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String from_user_name = dataSnapshot.child("Users").child(from_user_id).child("name").getValue().toString();
                String image = dataSnapshot.child("Users").child(from_user_id).child("thumb_image").getValue().toString();
                mode = dataSnapshot.child("Users").child(from_user_id).child("mode").getValue().toString();
                holder.messageSenderName.setText(from_user_name + " (" + mode + ")");
                holder.messageText.setText("sent you friend request.");

                Picasso.with(holder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.default_profile).into(holder.profileImage);

                holder.messageSentTime.setText(ConvertTime(time));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootRef.addValueEventListener(new ValueEventListener() {
                    int i = 0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (i == 0) {
                            String user_id = c.getFrom();
                            SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("Local DB", Context.MODE_PRIVATE);
                            String my_mode = sharedPreferences.getString("my_mode", "unknown");
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String my_uid = currentUser.getUid();
                            String state = "Undetermined";
                            if (my_mode.equals("unknown")) {
                                state = "Undetermined";
                            } else if (my_mode.equals("Adult") && mode.equals("Child")) {
                                if (dataSnapshot.child("Children").child(my_uid).hasChild(user_id)) {
                                    state = "fam_p";
                                } else if (dataSnapshot.child("Friend_Requests").child(my_uid).hasChild(user_id)) {
                                    state = "fam_sent";
                                } else state = "not_fam_p";
                            } else if (my_mode.equals("Child") && mode.equals("Adult")) {
                                if (dataSnapshot.child("Parents").child(my_uid).hasChild(user_id)) {
                                    state = "fam_ch";
                                } else if (dataSnapshot.child("Friend_Requests").child(my_uid).hasChild(user_id)) {
                                    state = "fam_received";
                                } else state = "not_fam_ch";
                            } else if (my_mode.equals(mode)) {
                                Log.d("hey","Datasnapshot exists? - " + dataSnapshot.exists());
                                Log.d("hey", "Here is Friends Datasnapshoot");
                                Log.d("hey", "Yey" + dataSnapshot.child("Friends").child(my_uid).hasChild(user_id));
                                Log.d("hey", "Friends Adapter");
                                Log.d("hey", "You choosed Profile!");
                                Log.d("hey", "My mode is " + my_mode);
                                Log.d("hey", "His mode is " + mode);
                                Log.d("hey", "My id is " + my_uid);
                                Log.d("hey", "His id is " + user_id);
                                Log.d("hey", "State is " + state);
                                // Log.d("hey", "Yey2" + dataSnapshot.child("Friends").child(my_uid).child(user_id).child("date").getValue().toString());
                                if (dataSnapshot.child("Friends").child(my_uid).hasChild(user_id)) {
                                    state = "friends";
                                } else if (dataSnapshot.child("Friend_Requests").child(my_uid).hasChild(user_id)) {
                                    if (dataSnapshot.child("Friend_Requests").child(my_uid).child(user_id).child("request_type").getValue().toString().equals("received")) {
                                        state = "req_received";
                                    } else state = "req_sent";
                                } else state = "not_friends";
                            }
                            Intent anotherProfIntent = new Intent(holder.itemView.getContext(), AnotherProfileActivity.class);
                            anotherProfIntent.putExtra("user_id", user_id);
                            anotherProfIntent.putExtra("another_user_mode", mode);
                            anotherProfIntent.putExtra("state", state);
                            v.getContext().startActivity(anotherProfIntent);
                            i++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });
    }




    @Override
    public int getItemCount() {
        return mRequestList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView messageSenderName;
        public TextView messageSentTime;

        public RequestViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            profileImage = itemView.findViewById(R.id.message_profile_image);
            messageSenderName = itemView.findViewById(R.id.message_sender_name);
            messageSentTime = itemView.findViewById(R.id.message_time);
        }

        @Override
        public void onClick(View view) {

            Log.d("You clicked on Item!", "hey");

        }
    }

    private static String ConvertTime(long time) {

        final int SECOND = 1000;
        final int MINUTE = 60 * SECOND;
        final int HOUR = MINUTE * 60;
        final int DAY = HOUR * 24;

        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();

        long diff = Math.abs(now - time);

        if (diff < MINUTE) {
            return "Moment ago" + diff;
        } else if (diff < 2 * MINUTE) {
            return "1 minute ago";
        } else if (diff < 60 * MINUTE) {
            return diff / MINUTE + " minutes ago";
        } else if (diff < 2 * HOUR) {
            return "1 hour ago";
        } else if (diff < 24 * HOUR) {
            return diff / HOUR + " hours ago";
        } else if (diff < 48 * HOUR) {
            return "yesterday";
        } else {
            return diff / DAY + " days ago";
        }
    }
}
