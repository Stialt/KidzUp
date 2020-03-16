package com.example.admin.prototypekidzup1.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.prototypekidzup1.R;
import com.example.admin.prototypekidzup1.Tasks;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ADMIN on 05.12.2017.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {


    private List<Tasks> mTasksList;
    private View v;

    public TasksAdapter(List<Tasks> mTasksList) {
        this.mTasksList = mTasksList;
    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_single_layout, parent, false);
        return new TasksViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TasksViewHolder holder, int position) {
        final Tasks task = mTasksList.get(position);

        holder.Title.setText(task.getTitle());

        if (TextUtils.isEmpty(task.getPrize_text()))
            holder.prizeText.setText("No prize");
        else
            holder.prizeText.setText(task.getPrize_text());

        if (TextUtils.isEmpty(task.getPunish_text()))
            holder.punishText.setText("No Punishment");
            else
        holder.punishText.setText(task.getPunish_text());

        Picasso.with(holder.taskImage.getContext()).load(task.getIcon_path())
                .placeholder(R.drawable.icon_attach_image).into(holder.taskImage);
        Picasso.with(holder.prizeImage.getContext()).load(task.getPrize_icon_path())
                .placeholder(R.drawable.icon_attach_image).into(holder.prizeImage);
        Picasso.with(holder.punishImage.getContext()).load(task.getPunish_icon_path())
                .placeholder(R.drawable.icon_attach_image).into(holder.punishImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "You clicked on " + task.getTitle() + " task", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTasksList.size();
    }

    public class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView taskImage;
        public ImageView prizeImage;
        public ImageView punishImage;
        public TextView Title;
        public TextView prizeText;
        public TextView punishText;

        public TasksViewHolder(View itemView) {
            super(itemView);
            taskImage = itemView.findViewById(R.id.task_single_icon);
            prizeImage = itemView.findViewById(R.id.task_single_prize_icon);
            punishImage = itemView.findViewById(R.id.task_single_punish_icon);
            Title = itemView.findViewById(R.id.task_single_title);
            prizeText = itemView.findViewById(R.id.task_single_prize_text);
            punishText = itemView.findViewById(R.id.task_single_punish_text);

        }

        @Override
        public void onClick(View view) {
        }

    }
}
