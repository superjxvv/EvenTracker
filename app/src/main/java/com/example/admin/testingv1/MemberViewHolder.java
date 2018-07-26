package com.example.admin.testingv1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MemberViewHolder extends RecyclerView.ViewHolder  {
    View mView;
    TextView isAdmin;

    public MemberViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setMember (String member_) {
        TextView member = (TextView) mView.findViewById(R.id.member);
        member.setText(member_);
    }

    public void setAdmin (){
        isAdmin = (TextView) mView.findViewById(R.id.isLeader);
        isAdmin.setText("Admin");
    }
}
