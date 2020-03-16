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
import com.example.admin.prototypekidzup1.Tasks;
import com.example.admin.prototypekidzup1.adapter.TasksAdapter;
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
public class TasksFragment extends Fragment {

    private RecyclerView tasksList;
    private LinearLayoutManager mLinearLayout;
    private TasksAdapter mAdapter;
    private final List<Tasks> tasksArrayList = new ArrayList<>();
    private String currentUserId;
    private TextView nothingtoShow;
    private DatabaseReference rootRef;
    View mMainView;

    public TasksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_tasks, container, false);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        tasksList = mMainView.findViewById(R.id.frag_tasks_recycler_view);
        nothingtoShow = mMainView.findViewById(R.id.frag_tasks_nothing_to_show);
        mLinearLayout = new LinearLayoutManager(getContext());
        mAdapter = new TasksAdapter(tasksArrayList);

        tasksList.setLayoutManager(mLinearLayout);
        tasksList.setHasFixedSize(true);
        tasksList.setAdapter(mAdapter);

        nothingtoShow.setVisibility(View.VISIBLE);
        loadTasks();

        return mMainView;
    }

    private void loadTasks() {
        rootRef.child("Tasks")
                .child(currentUserId)
                .child("all").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Tasks task = dataSnapshot.getValue(Tasks.class);
                    tasksArrayList.add(task);
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
