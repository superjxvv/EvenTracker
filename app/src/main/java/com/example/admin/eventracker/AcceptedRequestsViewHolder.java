package com.example.admin.eventracker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AcceptedRequestsViewHolder extends RecyclerView.ViewHolder  {
        View mView;
        Button viewCalendar;

    public AcceptedRequestsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        viewCalendar = (Button) mView.findViewById(R.id.viewBtn);
    }

    public void setDescription (String user) {
        TextView description = mView.findViewById(R.id.Description);
        description.setText(user +" has accepted your request to view his calendar.");
    }
}
