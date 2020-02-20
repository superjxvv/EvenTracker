package com.example.admin.eventracker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class GroupEventViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public GroupEventViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setTitle(String title_, Boolean privacy) {
        TextView title = (TextView) mView.findViewById(R.id.title);
        if(!privacy){
            title.setText(title_);
        } else {
            title.setText("Private Event.");
        }
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

    public void setName(String email) {
        TextView user = (TextView)mView.findViewById(R.id.userEmail);
        user.setText(email+"'s Event.");
    }
}
