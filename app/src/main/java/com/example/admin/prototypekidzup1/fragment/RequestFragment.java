package com.example.admin.prototypekidzup1.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.prototypekidzup1.R;
import com.example.admin.prototypekidzup1.adapter.RequestAdapter;
import com.example.admin.prototypekidzup1.Requests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private RecyclerView requestsList;
    private LinearLayoutManager mLinearLayout;
    private RequestAdapter mAdapter;
    private final List<Requests> requestsArrayList = new ArrayList<>();
    private String currentUserId;
    private TextView nothingtoShow;
    View mMainView;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_request, container, false);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        requestsList = mMainView.findViewById(R.id.frag_requests_recycler_view);
        nothingtoShow = mMainView.findViewById(R.id.frag_requests_nothing_to_show);
        mLinearLayout = new LinearLayoutManager(getContext());
        mAdapter = new RequestAdapter(requestsArrayList);

        requestsList.setLayoutManager(mLinearLayout);
        requestsList.setHasFixedSize(true);
        requestsList.setAdapter(mAdapter);

        nothingtoShow.setVisibility(View.VISIBLE);
        loadRequests();

        return mMainView;
    }

    private void loadRequests() {
        final DatabaseReference reqRef = FirebaseDatabase.getInstance().getReference()
                .child("Friend_Requests").child(currentUserId);
        reqRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Requests request = dataSnapshot.getValue(Requests.class);

                if (dataSnapshot.child("request_type").getValue().toString().equals("received")) {
                    requestsArrayList.add(request);
                    mAdapter.notifyDataSetChanged();
                    nothingtoShow.setVisibility(View.INVISIBLE);
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

}
