package com.example.admin.prototypekidzup1.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.admin.prototypekidzup1.Friends;
import com.example.admin.prototypekidzup1.adapter.FriendsAdapter;
import com.example.admin.prototypekidzup1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FamilyFragment extends Fragment {

    private RecyclerView mFriendsList;
    private List<Friends> friendsListArray;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private FriendsAdapter mAdapter;
    private LinearLayoutManager mLinearLayout;
    private String mCurrentUserId;
    private EditText searchET;
    private Button searchButton;
    private ValueEventListener valueEventListener;
    private TextView nothingtoShow;

    DatabaseReference friendRef;

    private String my_mode;

    private View mMainView;
    private static int ONGOING = 1;
    private static int STOPPED = 2;
    private int status = ONGOING;

    private static int count = 0;

    public FamilyFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        status = ONGOING;
    }

    @Override
    public void onStop() {
        super.onStop();
        status = STOPPED;
        searchET.setText("");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mMainView == null)
            mMainView = inflater.inflate(R.layout.fragment_family, container, false);


        return mMainView;
    }

    @Override
    public void onStart() {
        Log.d("hey", "Started");
        super.onStart();
        friendsListArray = new ArrayList<>();
        mFriendsList = mMainView.findViewById(R.id.frag_family_recycle_list);
        searchET = mMainView.findViewById(R.id.frag_family_search_text);
        searchButton = mMainView.findViewById(R.id.frag_family_search_button);
        nothingtoShow = mMainView.findViewById(R.id.frag_family_nothing_to_show);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Local DB", Context.MODE_PRIVATE);
        my_mode = sharedPreferences.getString("my_mode", "unkown");


        if (my_mode.equals("Adult"))
            mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Children").child(mCurrentUserId);
        else if (my_mode.equals("Child"))
            mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Parents").child(mCurrentUserId);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendsDatabase.keepSynced(true);
        mAdapter = new FriendsAdapter(friendsListArray);
        mUsersDatabase.keepSynced(true);
        mLinearLayout = new LinearLayoutManager(getContext());

        mFriendsList.setLayoutManager(mLinearLayout);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setAdapter(mAdapter);

        nothingtoShow.setVisibility(View.VISIBLE);
        loadFriends(searchET.getText().toString().toLowerCase());

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = ONGOING;

                nothingtoShow.setVisibility(View.VISIBLE);
                friendsListArray.clear();
                loadFriends(searchET.getText().toString().toLowerCase());
                mAdapter.notifyDataSetChanged();
                //status = STOPPED;
            }
        });

    }

    private void loadFriends(final String searchText) {
        if (my_mode.equals("Adult"))
            friendRef = FirebaseDatabase.getInstance().getReference()
                    .child("Children").child(mCurrentUserId);
        else if (my_mode.equals("Child"))
            friendRef = FirebaseDatabase.getInstance().getReference()
                    .child("Parents").child(mCurrentUserId);

        Query friendRefQuery;
            friendRefQuery = friendRef;

        friendsListArray.clear();
        //friendRef.addChildEventListener(new ChildEventListener() {
        friendRefQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Friends friends = dataSnapshot.getValue(Friends.class);

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("Users").child(friends.getId()).child("name").getValue().toString();
                        String mode = dataSnapshot.child("Users").child(friends.getId()).child("mode").getValue().toString();

                        name = name.toLowerCase();
                        mode = mode.toLowerCase();

                        if (searchText.isEmpty() || name.contains(searchText) || mode.contains(searchText)) {
                            if (status == ONGOING) {
                                friendsListArray.add(friends);
                                Log.d("hey", searchText);
                                Log.d("hey", "I am stupid program!");
                                mAdapter.notifyDataSetChanged();
                                nothingtoShow.setVisibility(View.INVISIBLE);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                rootRef.addValueEventListener(valueEventListener);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

   /* @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int position) {
                final String list_user_id = getRef(position).getKey();
                friendsViewHolder.setDate(friends.getDate());

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        String userOnline = dataSnapshot.child("online").getValue().toString();
                        String mode = dataSnapshot.child("mode").getValue().toString();
                        friendsViewHolder.setName(userName, mode);
                        friendsViewHolder.setImage(userThumb);
                        friendsViewHolder.setUserOnline(userOnline);

                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Choose Option");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Click event for each item
                                        switch (i) {
                                            case 0:
                                                Intent anotherProfIntent = new Intent(getContext(), AnotherProfileActivity.class);
                                                anotherProfIntent.putExtra("user_id", list_user_id);
                                                startActivity(anotherProfIntent);
                                                break;
                                            case 1:
                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                chatIntent.putExtra("user_id", list_user_id);
                                                chatIntent.putExtra("userName", userName);
                                                startActivity(chatIntent);
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
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        };
        mFriendsList.setAdapter(friendsRecyclerViewAdapter);
    }*/
/*
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FriendsViewHolder(View viewItem) {
            super(viewItem);
            mView = viewItem;
        }

        public void setDate(String date) {
            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText("Friends since " + date);
        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setName(String name, String mode) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name + " (" + mode + ")");
        }

        public void setImage(final String thumbImage) {
            final ImageView userImageView = mView.findViewById(R.id.user_single_image);
            if (!thumbImage.equals("default")) {
                Picasso.with(mView.getContext()).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile).into(userImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(mView.getContext()).load(thumbImage).placeholder(R.drawable.default_profile).into(userImageView);
                    }
                });
            } else {
                Picasso.with(mView.getContext()).load(R.drawable.default_profile).into(userImageView);
            }
        }

        public void setUserOnline(String online) {

            ImageView userOnlineImage = mView.findViewById(R.id.user_single_online_icon);
            if (online.equals("true")) {
                userOnlineImage.setVisibility(View.VISIBLE);
            }
        }
    }*/
}
