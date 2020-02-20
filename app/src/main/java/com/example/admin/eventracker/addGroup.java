package com.example.admin.eventracker;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class addGroup extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView members;
    private DatabaseReference mRef;
    private String userID;
    private String userEmail;
    private FirebaseRecyclerAdapter<String, checkMemberViewHolder> recyclerAdapter;
    private FloatingActionButton next;
    private ArrayList<String> checkedMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkedMembers = new ArrayList<String>();
        next = (FloatingActionButton) findViewById(R.id.next);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        checkedMembers.add(encodeUserEmail(userEmail));
        mRef = FirebaseDatabase.getInstance().getReference();
        members = (RecyclerView) findViewById(R.id.members);
        members.setHasFixedSize(true);
        members.setLayoutManager(new LinearLayoutManager(this));
        Query query = mRef.child("Users").child(encodeUserEmail(userEmail)).child("Friends");
        FirebaseRecyclerOptions<String> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<String>().setQuery(query, String.class).build();
        recyclerAdapter = new FirebaseRecyclerAdapter<String, checkMemberViewHolder>
                (firebaseRecyclerOptions) {
            @Override
            public checkMemberViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_member_info, parent, false);
                return new checkMemberViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(checkMemberViewHolder viewHolder, int position, final String model) {
                viewHolder.setMember(model.replace(",", "."));
                viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean checked = ((CheckBox)view).isChecked();
                        if(checked) {
                            checkedMembers.add(model);
                        }else {
                            checkedMembers.remove(model);
                        }
                    }
                });
            }
        };
        members.setAdapter(recyclerAdapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedMembers.size() < 2) {
                    Toast.makeText(addGroup.this, "Select at least 1 participant", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(addGroup.this, setName.class);
                    intent.putExtra("checkedMembers", checkedMembers);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        recyclerAdapter.startListening();
    }
    @Override
    protected void onStop(){
        super.onStop();
        if(recyclerAdapter!= null){
            recyclerAdapter.stopListening();
        }
    }

    public String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    public String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
