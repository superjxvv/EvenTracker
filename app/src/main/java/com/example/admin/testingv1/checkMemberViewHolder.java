package com.example.admin.testingv1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class checkMemberViewHolder extends RecyclerView.ViewHolder  {
    View mView;
    CheckBox mCheckBox;
    public checkMemberViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mCheckBox = (CheckBox) mView.findViewById(R.id.checkmember);
    }

    public void setMember (String member_) {
        TextView member = (TextView) mView.findViewById(R.id.member);
        member.setText(member_);
    }
}
