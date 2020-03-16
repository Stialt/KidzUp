package com.example.admin.prototypekidzup1.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.admin.prototypekidzup1.Messages;
import com.example.admin.prototypekidzup1.adapter.MessagesAdapter;
import com.example.admin.prototypekidzup1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private String mChatUserName;
    private DatabaseReference rootRef;
    private Toolbar mToolBar;
    private TextView mTitleView, mLastSeenView;
    private CircleImageView mProfileImage;
    private String mCurrentUserId;

    private FirebaseAuth mAuth;

    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;
    private RecyclerView mMessagesList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private StorageReference imageStorage;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessagesAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private boolean refreshed = false;
    private int itemPos = 0;
    private String lastKey = "";
    private String prevKey = "";

    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatUser = getIntent().getStringExtra("user_id");
        mChatUserName = getIntent().getStringExtra("userName");

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        imageStorage = FirebaseStorage.getInstance().getReference();

        mToolBar = findViewById(R.id.chat_page_app_bar);
        setSupportActionBar(mToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(actionBarView);

        // -----------Custom Action Bar Items
        mTitleView = findViewById(R.id.chat_custom_bar_display_name);
        mLastSeenView = findViewById(R.id.chat_custom_bar_last_seen);
        mProfileImage = findViewById(R.id.chat_custom_bar_image);

        mChatAddBtn = findViewById(R.id.chat_page_add_button);
        mChatSendBtn = findViewById(R.id.chat_page_send_button);
        mChatMessageView = findViewById(R.id.chat_page_message_text);

        mAdapter = new MessagesAdapter(messagesList);

        mMessagesList = findViewById(R.id.chat_page_messages_list);
        swipeRefreshLayout = findViewById(R.id.chat_page_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        loadMessages();

        mTitleView.setText(mChatUserName);

        rootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Long online = (Long) dataSnapshot.child("online").getValue();
                final String image = dataSnapshot.child("image").getValue().toString();

                if (online == 0) {
                    mLastSeenView.setText("Online");
                } else {
                        mLastSeenView.setText(ConvertTime(online));
                }
                //Picasso loading Image
                if (!image.equals("default")) {
                    Picasso.with(getApplicationContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile).into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.default_profile).into(mProfileImage);
                        }
                    });
                } else {
                    Picasso.with(getApplicationContext()).load(R.drawable.default_profile).into(mProfileImage);
                } //end of Picasso Loading Image
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        rootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mChatUser)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId, chatAddMap);

                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {

                            } else {

                            }
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //-------------------- Send Button ------------------------------
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                mChatMessageView.getText().clear();
                mMessagesList.scrollToPosition(messagesList.size() - 1);
                DatabaseReference chatUserRef = FirebaseDatabase.getInstance().getReference()
                        .child("Chat").child(mChatUser).child(mCurrentUserId);
                chatUserRef.child("seen").setValue(false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages();
            }
        });

        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
            }
        });

    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            final String currentUserRef = "messages/" + mCurrentUserId + "/" + mChatUser;
            final String chatUserRef = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = rootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            final String push_id = user_message_push.getKey();

            StorageReference filepath = imageStorage.child("message_images").child(push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        String downloadUri = task.getResult().getDownloadUrl().toString();
                        Map messageMap = new HashMap();
                        messageMap.put("message", downloadUri);
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("seen", false);
                        messageMap.put("from", mCurrentUserId);
                        messageMap.put("type", "image");

                        Map bothMessageMap = new HashMap();
                        bothMessageMap.put(currentUserRef + "/" + push_id, messageMap);
                        bothMessageMap.put(chatUserRef + "/" + push_id, messageMap);

                        Log.d("Maps Data: ", messageMap.toString());
                        Log.d("Maps Data: ", bothMessageMap.toString());


                        rootRef.updateChildren(bothMessageMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.d("Chat_Log", databaseError.getMessage());
                                } else {
                                }
                            }
                        });
                    }
                }
            });
        }

    } // end of on Activity Result

    private void loadMoreMessages() {

        DatabaseReference messageRef = rootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery = messageRef.orderByKey().endAt(lastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();


                DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Chat")
                        .child(mCurrentUserId).child(mChatUser);

                messagesRef.child("seen").setValue(true);

                if (!prevKey.equals(messageKey)) {
                    messagesList.add(itemPos++, message);
                } else {
                    prevKey = lastKey;
                }

                if (itemPos == 1) {
                    lastKey = dataSnapshot.getKey();
                }


                mAdapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10, 0);

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


    } // end of Load More Messages

    private void loadMessages() {

        DatabaseReference messageRef = rootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery = messageRef.limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;
                DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Chat")
                        .child(mCurrentUserId).child(mChatUser);

                messagesRef.child("seen").setValue(true);

                if (itemPos == 1) {
                    lastKey = dataSnapshot.getKey();
                    prevKey = dataSnapshot.getKey();
                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();


                mMessagesList.scrollToPosition(messagesList.size() - 1);

                swipeRefreshLayout.setRefreshing(false);

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

    } //end of load messages

    private void sendMessage() {
        String message = mChatMessageView.getText().toString();
        String currentUserRef = "messages/" + mCurrentUserId + "/" + mChatUser;
        String chatUserRef = "messages/" + mChatUser + "/" + mCurrentUserId;

        DatabaseReference user_message_push = rootRef.child("messages")
                .child(mCurrentUserId).child(mChatUser).push();
        String push_id = user_message_push.getKey();

        if (!TextUtils.isEmpty(message)) {
            Map msgMap = new HashMap();
            msgMap.put("message", message);
            msgMap.put("seen", false);
            msgMap.put("type", "text");
            msgMap.put("time", ServerValue.TIMESTAMP);
            msgMap.put("from", mCurrentUserId);

            Map bothUserMessage = new HashMap();
            bothUserMessage.put(currentUserRef + "/" + push_id, msgMap);
            bothUserMessage.put(chatUserRef + "/" + push_id, msgMap);

            rootRef.updateChildren(bothUserMessage, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("Chat_Log", databaseError.getMessage().toString());
                    }
                }
            });

        }

    }

    private static String ConvertTime(long time) {

        final int SECOND = 1000;
        final int MINUTE = 60 * SECOND;
        final int HOUR = MINUTE * 60;
        final int DAY = HOUR * 24;

        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();

        long diff = Math.abs(now - time);

        if (diff < MINUTE) {
            return "Moment ago" + diff;
        } else if (diff < 2 * MINUTE) {
            return "1 minute ago";
        } else if (diff < 60 * MINUTE) {
            return diff / MINUTE + " minutes ago";
        } else if (diff < 2 * HOUR) {
            return "1 hour ago";
        } else if (diff < 24 * HOUR) {
            return diff / HOUR + " hours ago";
        } else if (diff < 48 * HOUR) {
            return "yesterday";
        } else {
            return diff / DAY + " days ago";
        }
    }

}
