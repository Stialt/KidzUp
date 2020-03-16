package com.example.admin.prototypekidzup1.fragment;

//import android.app.Fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.prototypekidzup1.R;
import com.example.admin.prototypekidzup1.adapter.SectionsPagerAdapter;

public class FriendsMainFragment extends Fragment {

    private ViewPager mviewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.friends_main_fragment, container, false);

        mviewPager = mView.findViewById(R.id.friend_tab_pager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());//getSupportFragmentManager());
        //getSupportFragmentManager());

        mviewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = mView.findViewById(R.id.friends_tabs);
        tabLayout.setupWithViewPager(mviewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}
