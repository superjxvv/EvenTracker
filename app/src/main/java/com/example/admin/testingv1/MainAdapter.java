package com.example.admin.testingv1;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    ArrayList<String> participants;

    public MainAdapter(ArrayList<String> participants) {
        this.participants = participants;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        holder.member.setText(participants.get(position));
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView member;

        public ViewHolder(View itemView) {
            super(itemView);

            member = (TextView) itemView.findViewById(R.id.member);
        }
    }
}
