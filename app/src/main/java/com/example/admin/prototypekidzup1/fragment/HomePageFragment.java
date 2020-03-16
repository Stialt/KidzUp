package com.example.admin.prototypekidzup1.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.admin.prototypekidzup1.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {


    private static final int TASKS = 0;
    private static final int MESSAGES = 1;
    private static final int FRIENDS = 2;
    private static final int REQUESTS = 3;
    private static final int SEARCH = 4;
    private static final int MYPROFILE = 5;
    private static final int FAMILY = 6;


    private View mMainView;

    private ImageButton tasksButton;
    private ImageButton chatsButton;
    private ImageButton friendsButton;
    private ImageButton familyButton;
    private TextView welcomeText;


    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mMainView == null)
            mMainView = inflater.inflate(R.layout.fragment_homepage, container, false);
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        tasksButton = mMainView.findViewById(R.id.frag_home_tasks_button);
        chatsButton = mMainView.findViewById(R.id.frag_home_chats_button);
        friendsButton = mMainView.findViewById(R.id.frag_home_friends_button);
        familyButton = mMainView.findViewById(R.id.frag_home_family_button);
        welcomeText = mMainView.findViewById(R.id.frag_home_welcome_text);



        chatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayFragment(MESSAGES);
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayFragment(FRIENDS);
            }
        });
        tasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayFragment(TASKS);
            }
        });
        familyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayFragment(FAMILY);
            }
        });
    }

    void DisplayFragment(int item) {

        Fragment fragment = null;

        switch (item) {
            case TASKS:
                fragment = new TasksFragment();
                break;
            case MESSAGES:
                fragment = new ChatsFragment();
                break;
            case FRIENDS:
                fragment = new FriendsFragment();
                break;
            case FAMILY:
                fragment = new FamilyFragment();
                break;
            default:
                break;

        }

        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.MyFrameLayout, fragment);

            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
