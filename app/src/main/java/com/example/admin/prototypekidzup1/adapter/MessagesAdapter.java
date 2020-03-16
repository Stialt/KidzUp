package com.example.admin.prototypekidzup1.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin.prototypekidzup1.Messages;
import com.example.admin.prototypekidzup1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ADMIN on 02.12.2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> mMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    public MessagesAdapter(List<Messages> mMessagesList) {
        this.mMessagesList = mMessagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);
        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public CircleImageView profileImage;
        public TextView messageSenderName;
        public TextView messageSentTime;
        public RelativeLayout relativeLayout;
        public ImageView messageImage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            profileImage = itemView.findViewById(R.id.message_profile_image);
            messageSenderName = itemView.findViewById(R.id.message_sender_name);
            messageSentTime = itemView.findViewById(R.id.message_time);
            relativeLayout = itemView.findViewById(R.id.message_single_layout);
            messageImage = itemView.findViewById(R.id.message_image_layout);
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        Messages c = mMessagesList.get(position);

        String currentUserId = mAuth.getCurrentUser().getUid();
        String from_user_id = c.getFrom();
        String messageType = c.getType();
        final Long time = c.getTime();

        rootRef.child("Users").child(from_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("thumb_image").getValue().toString();

                holder.messageSenderName.setText(name);
                holder.messageSentTime.setText(ConvertTime(time));

                //Picasso download image
                Picasso.with(holder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.default_profile).into(holder.profileImage);
                //end of Picasso
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        if (messageType.equals("text")) {

            holder.messageText.setText(c.getMessage());
            holder.messageImage.setVisibility(View.INVISIBLE);

        } else if (messageType.equals("image")) {

            holder.messageText.setVisibility(View.INVISIBLE);
            Picasso.with(holder.profileImage.getContext())
                    .load(c.getMessage()).into(holder.messageImage);
        }

        //holder.profileImage.setText(c.getTime());
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
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

        if (diff < 15 * SECOND) {
            return "Moment ago" + diff;
        } else if (diff < MINUTE) {
            return diff / SECOND + " seconds ago";
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
