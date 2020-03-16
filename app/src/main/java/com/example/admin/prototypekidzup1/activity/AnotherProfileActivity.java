package com.example.admin.prototypekidzup1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.prototypekidzup1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AnotherProfileActivity extends AppCompatActivity {

    private TextView userNameDisplay;
    private ImageView imageDisplay;
    private TextView userStatusDisplay, userModeDisplay;
    private EditText familyKeyText;
    private Button sendRequestButton;
    private Button deckineRequestButton;
    private DatabaseReference userDatabase;
    private DatabaseReference friendRequestDatabase;
    private DatabaseReference friendDatabase;
    private DatabaseReference notificationDatabase;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;
    private Toolbar mToolBar;
    String user_id;
    String name;
    private String currentState;
    private String user_mode;
    private String current_user_mode;

    private static final int RETRIEVED = 0;
    private static final int NOTRETRIEVED = 1;
    private int mode_retrieve_status = NOTRETRIEVED;

    private String userName;

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AnotherProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_profile);

        user_mode = "user_mode";
        current_user_mode = "current_user_mode";
        user_id = getIntent().getStringExtra("user_id");

        SharedPreferences sharedPreferences = getSharedPreferences("Local DB", Context.MODE_PRIVATE);
        user_mode = sharedPreferences.getString("my_mode", "user_mode");
        current_user_mode = getIntent().getStringExtra("another_user_mode");
        currentState = getIntent().getStringExtra("state");

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        rootRef = FirebaseDatabase.getInstance().getReference();
        userDatabase.keepSynced(true);
        friendDatabase.keepSynced(true);
        friendRequestDatabase.keepSynced(true);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();



        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        //Toolbar
        mToolBar = findViewById(R.id.another_profile_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userNameDisplay = findViewById(R.id.another_profile_display_name);
        imageDisplay = findViewById(R.id.another_profile_display_image);
        userStatusDisplay = findViewById(R.id.another_profile_display_status);
        userModeDisplay = findViewById(R.id.another_profile_display_mode);
        sendRequestButton = findViewById(R.id.another_profile_send_request_button);
        deckineRequestButton = findViewById(R.id.another_profile_decline_request_button);
        familyKeyText = findViewById(R.id.another_profile_key);
        deckineRequestButton.setVisibility(View.INVISIBLE);
        deckineRequestButton.setEnabled(false);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("User Information Loading");
        progressDialog.setMessage("Please, wait while we load user's data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();



        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String mode = dataSnapshot.child("mode").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                userNameDisplay.setText(name);
                userStatusDisplay.setText(status);
                userModeDisplay.setText(mode);
                Picasso.with(AnotherProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile).into(imageDisplay, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(AnotherProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(imageDisplay);
                    }
                });

                getSupportActionBar().setTitle(name);
                setInterface();
                // - ---------------------------Friend List / Reuest Feature ---------------------------
                //determineState();

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendRequestButton.setEnabled(false);

                final String input_key = familyKeyText.getText().toString();

                if (currentUser.getUid().equals(user_id)) {
                    Toast.makeText(AnotherProfileActivity.this, "Cannot Send Request Yourself", Toast.LENGTH_LONG).show();
                }

                // - --------------------------NOT FRIENDS STATE ----------------------------------
                else if (currentState.equals("not_friends")) {

                    String notId = rootRef.child("notifications").child(user_id).push().getKey();
                    Map requestMap = new HashMap();
                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", currentUser.getUid());
                    notificationData.put("type", "friend_request");

                    requestMap.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id + "/from", currentUser.getUid());
                    requestMap.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id + "/time", ServerValue.TIMESTAMP);
                    requestMap.put("Friend_Requests/" + user_id + "/" + currentUser.getUid() + "/request_type", "received");
                    requestMap.put("Friend_Requests/" + user_id + "/" + currentUser.getUid() + "/from", currentUser.getUid());
                    requestMap.put("Friend_Requests/" + user_id + "/" + currentUser.getUid() + "/time", ServerValue.TIMESTAMP);

                    requestMap.put("notifications/" + user_id + "/" + notId, notificationData);

                    rootRef.updateChildren(requestMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                sendRequestButton.setEnabled(true);
                                currentState = "req_sent";
                                setInterface();
                                Toast.makeText(AnotherProfileActivity.this, "Request Sent Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AnotherProfileActivity.this, "Request Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } //if not friends


                // - --------------------------CANCEL REQUEST----------------------------------
                else if (currentState.equals("req_sent")) {

                    Map cancelReq = new HashMap();
                    cancelReq.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id, null);
                    cancelReq.put("Friend_Requests/" + user_id + "/" + currentUser.getUid(), null);

                    rootRef.updateChildren(cancelReq, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(AnotherProfileActivity.this, "Request Canceled", Toast.LENGTH_LONG).show();
                                currentState = "not_friends";
                                setInterface();
                            } else {
                                Toast.makeText(AnotherProfileActivity.this, "Request Not Canceled on second stage", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                // - --------------------------Request Received State ----------------------------------
                else if (currentState.equals("req_received")) {

                    SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy hh:mm:ss a", Locale.ENGLISH);
                    final String current_date = sdf.format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + currentUser.getUid() + "/" + user_id + "/date", current_date);
                    friendsMap.put("Friends/" + currentUser.getUid() + "/" + user_id + "/id", user_id);
                    friendsMap.put("Friends/" + user_id + "/" + currentUser.getUid() + "/date", current_date);
                    friendsMap.put("Friends/" + user_id + "/" + currentUser.getUid() + "/id", currentUser.getUid());

                    friendsMap.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id, null);
                    friendsMap.put("Friend_Requests/" + user_id + "/" + currentUser.getUid(), null);

                    rootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(AnotherProfileActivity.this, "Request Accepted", Toast.LENGTH_LONG).show();

                                currentState = "friends";
                                setInterface();
                            } else {
                                Toast.makeText(AnotherProfileActivity.this, "Request not Accepted", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } //end of Received State


                // - --------------------------Friends State ----------------------------------
                else if (currentState.equals("friends")) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + currentUser.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + currentUser.getUid(), null);
                    rootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                currentState = "not_friends";
                                setInterface();
                                Toast.makeText(AnotherProfileActivity.this, "Unfriended", Toast.LENGTH_LONG).show();
                            } //Friend state removed from SECOND user
                            else {
                                Toast.makeText(AnotherProfileActivity.this, "Unfriending Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } //end of Friends State

                // -----------------------Family Child State ---------------------------------------
                else if (currentState.equals("fam_ch")) {
                    open_chat(user_id);
                }
                // ------------------------Family Parent State -------------------------------------
                else if (currentState.equals("fam_p")) {
                    open_chat(user_id);
                }
                // -------------------------Family Received (Child) --------------------------------
                else if (currentState.equals("fam_received")) {
                    final String[] key = new String[1];
                    key[0] = "";
                    DatabaseReference friendReqUser = FirebaseDatabase.getInstance().getReference().child("Friend_Requests")
                            .child(currentUser.getUid()).child(user_id);
                    friendReqUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("key").getValue() != null)
                            key[0] = dataSnapshot.child("key").getValue().toString();
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy hh:mm:ss a", Locale.ENGLISH);
                            final String current_date = sdf.format(new Date());

                            Log.d("hey", "input key is " + familyKeyText.getText().toString());
                            if (TextUtils.isEmpty(input_key)) {
                                Toast.makeText(AnotherProfileActivity.this, "Key is necessary", Toast.LENGTH_LONG).show();
                            } else if (!input_key.equals(key[0])) {
                                Toast.makeText(AnotherProfileActivity.this, "Keys are not same", Toast.LENGTH_LONG).show();
                            } else {
                                Map request = new HashMap();
                                request.put("Parents/" + currentUser.getUid() + "/" + user_id + "/id", user_id);
                                request.put("Parents/" + currentUser.getUid() + "/" + user_id + "/date", current_date);
                                request.put("Children/" + user_id + "/" + currentUser.getUid() + "/id", currentUser.getUid());
                                request.put("Children/" + user_id + "/" + currentUser.getUid() + "/date", current_date);
                                request.put("Friend_Requests/" + user_id + "/" + currentUser.getUid(), null);
                                request.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id, null);
                                rootRef.updateChildren(request).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        Toast.makeText(AnotherProfileActivity.this, "Family Request Accepted", Toast.LENGTH_LONG).show();
                                        currentState = "fam_ch";
                                        setInterface();
                                    }
                                });
                            }
                            setInterface();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


                }
                //-------------------------Family Sent (Adult) State -------------------------------
                else if (currentState.equals("fam_sent")) {
                    Map request = new HashMap();
                    request.put("Friend_Requests/" + user_id + "/" + currentUser.getUid(), null);
                    request.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id, null);
                    rootRef.updateChildren(request).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(AnotherProfileActivity.this, "Family Request Canceled", Toast.LENGTH_LONG).show();
                            currentState = "not_fam_p";
                            setInterface();
                        }
                    });
                }
                //--------------------------Not Family Child State ---------------------------------
                else if (currentState.equals("not_fam_ch")) {
                    //Nothing
                }
                //--------------------------Not Family Parent State --------------------------------
                else if (currentState.equals("not_fam_p")) {
                    String key = familyKeyText.getText().toString();
                    if (TextUtils.isEmpty(key)) {
                        Toast.makeText(AnotherProfileActivity.this, "Family Key is Necessary", Toast.LENGTH_LONG).show();
                    } else {
                        Map request = new HashMap();
                        request.put("Friend_Requests/" + user_id + "/" + currentUser.getUid() + "/from", currentUser.getUid());
                        request.put("Friend_Requests/" + user_id + "/" + currentUser.getUid() + "/request_type", "received");
                        request.put("Friend_Requests/" + user_id + "/" + currentUser.getUid() + "/time", ServerValue.TIMESTAMP);
                        request.put("Friend_Requests/" + user_id + "/" + currentUser.getUid() + "/key", key);
                        request.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id + "/from", currentUser.getUid());
                        request.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id + "/request_type", "sent");
                        request.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id + "/time", ServerValue.TIMESTAMP);
                        request.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id + "/key", key);

                        rootRef.updateChildren(request).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Toast.makeText(AnotherProfileActivity.this, "Family Request Sent Succesfully", Toast.LENGTH_LONG).show();
                                currentState = "fam_sent";
                                setInterface();
                            }
                        });

                    }

                } //end of Not Fam P state


                sendRequestButton.setEnabled(true);

                familyKeyText.setText("");
                //determineState();
                //setInterface();
            } //OnClick end
        });  //send request button end

        deckineRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestButton.setEnabled(false);

                //-----------------------------------Received State (Decline) ---------------------------------
                if (currentState.equals("req_received")) {

                    Map declineReqMap = new HashMap();
                    declineReqMap.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id, null);
                    declineReqMap.put("Friend_Requests/" + user_id + "/" + currentUser.getUid(), null);
                    rootRef.updateChildren(declineReqMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                currentState = "not_friends";
                                setInterface();
                                Toast.makeText(AnotherProfileActivity.this, "Requset Declined", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AnotherProfileActivity.this, "Decline Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                //-----------------------------------Fam Received State (Decline button)------------
                else if (currentState.equals("fam_received")) {
                    Map request = new HashMap();
                    request.put("Friend_Requests/" + user_id + "/" + currentUser.getUid(), null);
                    request.put("Friend_Requests/" + currentUser.getUid() + "/" + user_id, null);
                    rootRef.updateChildren(request).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(AnotherProfileActivity.this, "Request Declined", Toast.LENGTH_LONG).show();
                            currentState = "not_fam_ch";
                            setInterface();
                        }
                    });
                } //end of state fam received

                //-----------------------------Fam Parent (Decline) --------------------------------
                else if (currentState.equals("fam_p")) {
                    //give a task activity
                    Intent createtaskIntent = new Intent(AnotherProfileActivity.this, CreateTaskActivity.class);
                    //put user name
                    createtaskIntent.putExtra("user_id", user_id);
                    createtaskIntent.putExtra("userName", name);
                    //put user id
                    startActivity(createtaskIntent);
                }
                //determineState();

                sendRequestButton.setEnabled(true);
            }
        });



    }

    private void setInterface() {
        Log.d("hey", "Setting up interface");
        Log.d("hey", "Current state is " + currentState);
        // userModeDisplay
        // sendRequestButton
        // deckineRequestButton
        // familyKeyText
        /*fam_ch - 		open chat b
        fam_p - 		open chat b	give task b
        fam_received		decline b	accept b	key input
        fam_sent		cancel b
        not_fam_ch
        not_fam_p		send fam b			key input
        req_received		decline b	accept b
        req_sent		cancel b
        friends			open chat b
        not_friends		send req b*/
        sendRequestButton.setVisibility(View.VISIBLE);
        sendRequestButton.setEnabled(true);
        deckineRequestButton.setVisibility(View.INVISIBLE);
        deckineRequestButton.setEnabled(false);
        familyKeyText.setVisibility(View.INVISIBLE);
        familyKeyText.setEnabled(false);
        switch (currentState) {
            case "fam_ch":
                userModeDisplay.setText("Your Parent");
                sendRequestButton.setText("Open Chat");
                sendRequestButton.setVisibility(View.VISIBLE);
                break;
            case "fam_p":
                userModeDisplay.setText("Your Child");
                sendRequestButton.setText("Open Chat");
                deckineRequestButton.setVisibility(View.VISIBLE);
                deckineRequestButton.setEnabled(true);
                deckineRequestButton.setText("Give a task");
                break;
            case "fam_received":
                deckineRequestButton.setVisibility(View.VISIBLE);
                deckineRequestButton.setEnabled(true);
                sendRequestButton.setText("Accept Family Request");
                deckineRequestButton.setText("Decline Family Request");
                familyKeyText.setVisibility(View.VISIBLE);
                familyKeyText.setEnabled(true);
                break;
            case "fam_sent":
                sendRequestButton.setText("Cancel Family Request");
                break;
            case "not_fam_ch":
                sendRequestButton.setVisibility(View.INVISIBLE);
                sendRequestButton.setEnabled(false);
                break;
            case "not_fam_p":
                sendRequestButton.setText("Send Family Request");
                familyKeyText.setVisibility(View.VISIBLE);
                familyKeyText.setEnabled(true);
                break;
            case "req_received":
                deckineRequestButton.setVisibility(View.VISIBLE);
                deckineRequestButton.setEnabled(true);
                deckineRequestButton.setText("Decline Friend Request");
                sendRequestButton.setText("Accept Friend Request");
                break;
            case "req_sent":
                sendRequestButton.setText("Cancel Friend Request");
                break;
            case "friends":
                sendRequestButton.setText("Unfriend");
                break;
            case "not_friends":
                sendRequestButton.setText("Send Friend Request");
                break;
            case "Undetermined":
                sendRequestButton.setEnabled(false);
                sendRequestButton.setVisibility(View.INVISIBLE);
            default:
                break;
        }
        Log.d("hey", "Interface setted up");
    }
/*
    private void determineState() {

        Log.d("hey", "Trying to determine state");
        final String current_user_id = currentUser.getUid();

        if (current_user_mode.equals("Child") && user_mode.equals("Adult")) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

            final Semaphore semaphore = new Semaphore(0);
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("hey", "Stupid program that not deserves to implemented by Kim Denis");
                    if (dataSnapshot.child("Parents").child(current_user_id).hasChild(user_id))
                        currentState = "fam_ch";
                    else {
                        if (dataSnapshot.child("Friend_Requests").child(current_user_id).child(user_id).hasChild("from")) {
                            currentState = "fam_received";
                        } else {
                            currentState = "not_fam_ch";
                        }
                    }

                    setInterface();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });//end of Listener
        } else if (current_user_mode.equals("Adult") && user_mode.equals("Child")) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Children").child(current_user_id).hasChild(user_id))
                        currentState = "fam_p";
                    else {
                        if (dataSnapshot.child("Friend_Requests").child(current_user_id).child(user_id).hasChild("from")) {
                            currentState = "fam_sent";
                        } else {
                            currentState = "not_fam_p";
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            }); //end of Listener

        } else if (user_mode.equals(current_user_mode)) {
            friendRequestDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {

                        String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                        if (req_type.equals("received")) {
                            currentState = "req_received";
                        } else if (req_type.equals("sent")) {
                            currentState = "req_sent";
                        }
                    } else {
                        friendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(user_id)) {
                                    currentState = "friends";
                                } else currentState = "not_friends";
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } //on Data Change Listener

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }//--- end of friend feature

        Log.d("hey", "Determined current state is " + currentState);

    } //end of determine state
*/

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String User = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(User).child("online").setValue(0);
        //determineState();
        //setInterface();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void open_chat(final String user_id) {

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue().toString();
                Intent chatIntent = new Intent(AnotherProfileActivity.this, ChatActivity.class);
                chatIntent.putExtra("user_id", user_id);
                Log.d("hey", "I put name " + userName + "as extra to call chat");
                chatIntent.putExtra("userName", userName);
                startActivity(chatIntent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            // logout();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();
            launchStartActivity();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchStartActivity() {

        Intent intent = new Intent(AnotherProfileActivity.this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}
