package com.example.admin.eventracker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventViewHolder extends RecyclerView.ViewHolder {
    View mView;
    Button editBtn;
    public EventViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        editBtn = (Button) mView.findViewById(R.id.editBtn);
    }

    public void setTitle(String title_) {
        TextView title = (TextView) mView.findViewById(R.id.title);
        title.setText(title_);
    }

    public void setStartTime(String startTime_) {
        TextView startTime = (TextView) mView.findViewById(R.id.startTime);
        startTime.setText(startTime_);
    }

    public void setEndTime(String endTime_) {
        TextView endTime = (TextView) mView.findViewById(R.id.endTime);
        endTime.setText(endTime_);
    }

    public void setRemarks(String remarks_) {
        TextView remarks = (TextView) mView.findViewById(R.id.remarks);
        remarks.setText(remarks_);
    }
}