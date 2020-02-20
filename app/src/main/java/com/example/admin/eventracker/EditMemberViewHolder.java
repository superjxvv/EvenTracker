package com.example.admin.eventracker;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EditMemberViewHolder extends RecyclerView.ViewHolder  {
    View mView;
    Button kickBtn;

    public EditMemberViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        kickBtn = (Button) mView.findViewById(R.id.kickBtn);
    }

    public void setMember (String member_) {
        TextView member = (TextView) mView.findViewById(R.id.member);
        member.setText(member_);
    }

    public void kickMember (Group group, String member) {
        group.removeMember(member);
    }
}

