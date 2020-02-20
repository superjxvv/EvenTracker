package com.example.admin.eventracker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class MainAdapter2 extends RecyclerView.Adapter<MainAdapter2.ViewHolder> {

    ArrayList<String> participants;
    Group group;

    public MainAdapter2(Group group) {
        this.participants = group.getMembers();
        this.group = group;
    }

    @NonNull
    @Override
    public MainAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter2.ViewHolder holder, int position) {
        holder.setMember(decodeUserEmail(participants.get(position)));
        if(group.getLeader().equals(decodeUserEmail((participants.get(position))))){
            holder.setAdmin();
        }
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        View mView;
        TextView isAdmin;

        public ViewHolder(View itemView) {
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
    public String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    public String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
