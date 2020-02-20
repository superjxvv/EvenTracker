package com.example.admin.eventracker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RequestViewHolder extends RecyclerView.ViewHolder {
    View mView;
    Button acceptBtn;
    Button rejectBtn;

    public RequestViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        acceptBtn = (Button) mView.findViewById(R.id.acceptBtn);
        rejectBtn = (Button) mView.findViewById(R.id.rejectBtn);
    }

    public void setDescription (String requestType, String requester) {
        TextView description = (TextView) mView.findViewById(R.id.description);
        if(requestType.equals("ViewCalendarRequest")) {
            description.setText(requester + " has sent a request to view your Calendar.");
        } else {
            description.setText(requester + " has sent you a friend request.");
        }
    }

    public void setDescription (String groupName, String requester, String eventName, String startTime, String endTime, String startDate, String endDate) {
        TextView description = (TextView) mView.findViewById(R.id.description);
        description.setText(requester +" from " +groupName +" has sent you an invitation to "+eventName +" from "+ startDate +" "+ startTime +" to " + endDate +" "+ endTime+" .");
    }
}
