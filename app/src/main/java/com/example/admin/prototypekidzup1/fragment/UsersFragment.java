package com.example.admin.prototypekidzup1.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.admin.prototypekidzup1.R;
import com.example.admin.prototypekidzup1.Users;
import com.example.admin.prototypekidzup1.adapter.UsersAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {


    private List<Users> usersArrayList;
    private RecyclerView mUsersList;
    private View mMainView;
    private UsersAdapter mAdapter;

    private EditText searchText;
    private Button searchButton;

    private static int ONGOING = 1;
    private static int STOPPED = 2;
    private int status = ONGOING;

    private TextView nothingtoShow;
    private DatabaseReference mUsersDatabase;

    public UsersFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        status = STOPPED;
        searchText.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        status = ONGOING;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mMainView == null)
            mMainView = inflater.inflate(R.layout.fragment_users, container, false);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mUsersList = mMainView.findViewById(R.id.users_list);
        searchText = mMainView.findViewById(R.id.frag_users_search_text);
        searchButton = mMainView.findViewById(R.id.frag_users_search_button);
        nothingtoShow = mMainView.findViewById(R.id.frag_users_nothing_to_show);

        usersArrayList = new ArrayList<>();
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new UsersAdapter(usersArrayList);
        mUsersList.setAdapter(mAdapter);

        //Firebase
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        nothingtoShow.setVisibility(View.VISIBLE);
        loadUsers(searchText.getText().toString().toLowerCase());
        //status = STOPPED;

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nothingtoShow.setVisibility(View.VISIBLE);
                status = ONGOING;
                Log.d("hey", "users_search_button_clicked");
                loadUsers(searchText.getText().toString().toLowerCase());
                mAdapter.notifyDataSetChanged();
               // status = STOPPED;
            }
        });

    }


    private void loadUsers(final String searchString) {
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference()
                .child("Users");
        Query usersRefQuery;

        usersRefQuery = usersRef.limitToLast(100);

        usersArrayList.clear();

        usersRefQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Users user = dataSnapshot.getValue(Users.class);
                String name = user.getName().toLowerCase();
                String mode = user.getMode().toLowerCase();

                Log.d("hey", "search string is " + searchString);
                if (searchString.isEmpty() || name.contains(searchString) || mode.contains(searchString)) {

                    if (status == ONGOING) {
                        Log.d("hey", "one more item added!");
                        usersArrayList.add(user);
                        mAdapter.notifyDataSetChanged();
                        nothingtoShow.setVisibility(View.INVISIBLE);
                    }
                }

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


    /*
    @Override
    public void onStart() {
        super.onStart();

        String User = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(User).child("online").setValue("true");

        Log.d("instanciated2", "hey");

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder usersViewHolder, Users users, int position) {

                Log.d("populate invoked", "hey");

                String list_user_id = getRef(position).getKey();
                //Toast.makeText(UsersFragment.this, "Position is " + list_user_id, Toast.LENGTH_LONG).show();
                usersViewHolder.setName(users.getName(), users.getMode());
                usersViewHolder.setStatus(users.getStatus());
                usersViewHolder.setThumbImage(users.getThumb_image());

                final String user_id = getRef(position).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent anotherProfIntent = new Intent(getActivity(), AnotherProfileActivity.class);
                        anotherProfIntent.putExtra("user_id", user_id);
                        startActivity(anotherProfIntent);
                    }
                });

            }
        }; //firebase recycler adapter created

        mUsersList.setAdapter(firebaseRecyclerAdapter);

    }

    */




/*
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setName(String name, String mode) {
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name + " (" + mode + ")");
        }

        public void setStatus(String status) {
            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void setThumbImage(final String image) {
            final ImageView userImageView = mView.findViewById(R.id.user_single_image);
            if (!image.equals("default")) {
                Picasso.with(mView.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile).into(userImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(mView.getContext()).load(image).placeholder(R.drawable.default_profile).into(userImageView);
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
    }

    */
}
