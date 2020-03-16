package com.example.admin.prototypekidzup1.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.admin.prototypekidzup1.fragment.ChatsFragment;
import com.example.admin.prototypekidzup1.fragment.FamilyFragment;
import com.example.admin.prototypekidzup1.fragment.FriendsFragment;
import com.example.admin.prototypekidzup1.fragment.HomePageFragment;
import com.example.admin.prototypekidzup1.ProfileMain;
import com.example.admin.prototypekidzup1.R;
import com.example.admin.prototypekidzup1.fragment.RequestFragment;
import com.example.admin.prototypekidzup1.SchoolMain;
import com.example.admin.prototypekidzup1.fragment.TasksFragment;
import com.example.admin.prototypekidzup1.fragment.UsersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        /*if (notLogged()) {

            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
        } */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            launchStartActivity();
        }



        DisplayFragments(R.id.nav_home);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            // logout();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();
            launchStartActivity();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchStartActivity() {

        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DisplayFragments(id);
        return true;
    }

    private void DisplayFragments(int id) {
        Fragment fragment = null;

        switch (id) {

            case R.id.nav_home:
                fragment = new HomePageFragment();
                break;
            case R.id.nav_tasks:
                fragment = new TasksFragment();
                break;
            case R.id.nav_school:
                fragment = new SchoolMain();
                break;
            case R.id.nav_chats:
                fragment = new ChatsFragment();
                break;
            case R.id.nav_friends:
                fragment = new FriendsFragment();
                break;
            case R.id.nav_family:
                fragment = new FamilyFragment();
                break;
            case R.id.nav_friend_requests:
                fragment = new RequestFragment();
                break;
            case R.id.nav_profile:
                fragment = new ProfileMain();
                break;
            case R.id.nav_logout:
                mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
                FirebaseAuth.getInstance().signOut();
                launchStartActivity();
                break;
            case R.id.nav_search:
                fragment = new UsersFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.MyFrameLayout, fragment);

            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            launchStartActivity();
        }
        else {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mUserRef.child("online").setValue(0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser() != null) {
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);}
    }
}
