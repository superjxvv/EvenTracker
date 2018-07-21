package com.example.admin.testingv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class GroupList extends AppCompatActivity implements View.OnClickListener {
    private Button addGroupBtn;
    private RecyclerView myRecyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private String userID;
    private FirebaseRecyclerAdapter <Group, GroupViewHolder> recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        addGroupBtn = (Button) findViewById(R.id.backBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        myRecyclerView = (RecyclerView) findViewById(R.id.groupsRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addGroupBtn.setOnClickListener(this);

        mRef = FirebaseDatabase.getInstance().getReference();
        Query query = mRef.child("Users").child(userID).child("Groups");
        FirebaseRecyclerOptions<Group> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Group>().setQuery(query, Group.class).build();
        recyclerAdapter = new FirebaseRecyclerAdapter<Group, GroupViewHolder>
                (firebaseRecyclerOptions) {
            @Override
            public GroupViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_info, parent, false);
                return new GroupViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(GroupViewHolder viewHolder, int position, final Group model) {
                viewHolder.setGroupName(model.getGroupName());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GroupList.this, groupCalendar.class);
                        intent.putExtra("Group", model);
                        startActivity(intent);
                    }
                });
            }
        };
        myRecyclerView.setAdapter(recyclerAdapter);
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

    public void onClick(View view) {
        if (view == addGroupBtn) {
            Intent intent = new Intent(GroupList.this, addGroup.class);
            startActivity(intent);
        }
    }
}
