package com.example.admin.prototypekidzup1.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.prototypekidzup1.Friends;
import com.example.admin.prototypekidzup1.R;
import com.example.admin.prototypekidzup1.activity.AnotherProfileActivity;
import com.example.admin.prototypekidzup1.activity.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ADMIN on 03.12.2017.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    private List<Friends> mFriendList;

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    private FirebaseAuth mAuth;
    private String mode;
    private DatabaseReference rootRef;
    private View v;

    int specialI = 0;

    public FriendsAdapter(List<Friends> mFriendList) {
        this.mFriendList = mFriendList;
    }


    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_single_layout, parent, false);
        return new FriendsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FriendsViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        final Friends c = mFriendList.get(position);

        final String user_id = c.getId();
        final String date = c.getDate();

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Users").child(user_id).child("name").getValue().toString();
                String image = dataSnapshot.child("Users").child(user_id).child("thumb_image").getValue().toString();
                mode = dataSnapshot.child("Users").child(user_id).child("mode").getValue().toString();

                SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("Local DB", Context.MODE_PRIVATE);
                String my_mode = sharedPreferences.getString("my_mode", "unknown");
                String nameString;
                String statusString = "Family confirmed since " + date;

                if (my_mode.equals("Child") && mode.equals("Adult")) {
                    nameString = name + " (Your Parent)";
                }
                else if (my_mode.equals("Adult")&& mode.equals("Child")) {
                    nameString = name + " (Your Child)";
                }
                else nameString = name + " (" + mode + ")";

                holder.nameView.setText(nameString);
                holder.dateView.setText(statusString);
                Picasso.with(holder.imageView.getContext()).load(image)
                        .placeholder(R.drawable.default_profile).into(holder.imageView);

               // holder.setName(name, mode);
                //holder.setDate(date);
                //holder.setImage(image);
            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());

                builder.setTitle("Choose Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Click event for each item
                        switch (i) {
                            case 0:
                                rootRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (specialI == 0) {
                                                SharedPreferences sharedPreferences = holder.imageView.getContext().getSharedPreferences("Local DB", Context.MODE_PRIVATE);
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
                                                    Log.d("hey", "Here is Friends Datasnapshoot");
                                                    Log.d("hey", "Yey" + dataSnapshot.child("Friends").child(my_uid).child(user_id).hasChild(date));
                                                    //Log.d("hey", "Yey2" + dataSnapshot.child("Friends").child(my_uid).child(user_id).child("date").getValue().toString());
                                                    if (dataSnapshot.child("Friends").child(my_uid).hasChild(user_id)) {
                                                        state = "friends";
                                                    } else if (dataSnapshot.child("Friend_Requests").child(my_uid).hasChild(user_id)) {
                                                        if (dataSnapshot.child("Friend_Requests").child(my_uid).child(user_id).child("type").getValue().toString().equals("received")) {
                                                            state = "req_received";
                                                        } else state = "req_sent";
                                                    } else state = "not_friends";
                                                }
                                                Intent anotherProfIntent = new Intent(holder.itemView.getContext(), AnotherProfileActivity.class);
                                                Log.d("hey", "Friends Adapter");
                                                Log.d("hey", "You choosed Profile!");
                                                Log.d("hey", "My mode is " + my_mode);
                                                Log.d("hey", "His mode is " + mode);
                                                Log.d("hey", "My id is " + my_uid);
                                                Log.d("hey", "His id is " + user_id);
                                                Log.d("hey", "State is " + state);
                                                anotherProfIntent.putExtra("user_id", user_id);
                                                anotherProfIntent.putExtra("another_user_mode", mode);
                                                anotherProfIntent.putExtra("state", state);
                                                holder.itemView.getContext().startActivity(anotherProfIntent);
                                                specialI++;
                                            }
                                        }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                break;
                            case 1:
                                Intent chatIntent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                                chatIntent.putExtra("user_id", user_id);
                                chatIntent.putExtra("userName", holder.nameView.getText().toString());
                                holder.itemView.getContext().startActivity(chatIntent);
                                break;
                            default:
                                break;
                        }
                    }
                });

                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View mView;
        public TextView nameView;
        public TextView dateView;
        public ImageView imageView;

        public FriendsViewHolder(View viewItem) {
            super(viewItem);
            nameView = viewItem.findViewById(R.id.user_single_name);
            dateView = viewItem.findViewById(R.id.user_single_status);
            imageView = viewItem.findViewById(R.id.user_single_image);
            mView = viewItem;
        }

        public void setDate(String date) {
            dateView.setText("Friends since " + date);
        }

        public void setName(String name) {

            nameView.setText(name);
        }

        public void setName(String name, String mode) {

            nameView.setText(name + " (" + mode + ")");
        }

        public void setImage(final String thumbImage) {
            if (!thumbImage.equals("default")) {
                Picasso.with(imageView.getContext()).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(imageView.getContext()).load(thumbImage).placeholder(R.drawable.default_profile).into(imageView);
                    }
                });
            } else {
                Picasso.with(imageView.getContext()).load(R.drawable.default_profile).into(imageView);
            }
        }

        public void setUserOnline(String online) {

            ImageView userOnlineImage = mView.findViewById(R.id.user_single_online_icon);
            if (online.equals("true")) {
                userOnlineImage.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {

        }
    }
}
