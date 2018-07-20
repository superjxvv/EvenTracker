package com.example.admin.testingv1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GroupViewHolder extends RecyclerView.ViewHolder  {
    View mView;

    public GroupViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setGroupName (String groupName_) {
        TextView groupName = (TextView) mView.findViewById(R.id.groupName);
        groupName.setText(groupName_);
    }
}
