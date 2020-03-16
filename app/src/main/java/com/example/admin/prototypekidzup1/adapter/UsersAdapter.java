package com.example.admin.prototypekidzup1.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.prototypekidzup1.R;
import com.example.admin.prototypekidzup1.Users;
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

/**
 * Created by ADMIN on 03.12.2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    private List<Users> mUsersList;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private View v;

    public UsersAdapter(List<Users> mUsersList) {
        this.mUsersList = mUsersList;
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_single_layout, parent, false);
        return new UsersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UsersViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        final Users c = mUsersList.get(position);

        holder.userName.setText(c.getName());
        holder.userStatus.setText(c.getStatus());

        Picasso.with(holder.profileImageView.getContext()).load(c.getImage())
                .placeholder(R.drawable.default_profile).into(holder.profileImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rootRef.addValueEventListener(new ValueEventListener() {
                    int i = 0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (i == 0) {
                            SharedPreferences sharedPreferences = holder.onlineImageView.getContext().getSharedPreferences("Local DB", Context.MODE_PRIVATE);
                            String my_mode = sharedPreferences.getString("my_mode", "unknown");
                            String mode = c.getMode();
                            String user_id = c.getId();
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
                            holder.itemView.getContext().startActivity(anotherProfIntent);
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
        return mUsersList.size();
    }


    public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView profileImageView;
        public ImageView onlineImageView;
        public TextView userName;
        public TextView userStatus;

        public UsersViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.user_single_image);
            onlineImageView = itemView.findViewById(R.id.user_single_online_icon);
            userName = itemView.findViewById(R.id.user_single_name);
            userStatus = itemView.findViewById(R.id.user_single_status);
        }

        @Override
        public void onClick(View view) {
        }

    }


}
