package com.example.admin.testingv1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class GroupList extends AppCompatActivity implements View.OnClickListener {
    private Button addGroupBtn;
    private RecyclerView myRecyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private FirebaseRecyclerAdapter <String, GroupViewHolder> recyclerAdapter;
    private String userEmail;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addGroupBtn = (Button) findViewById(R.id.backBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        myRecyclerView = (RecyclerView) findViewById(R.id.groupsRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addGroupBtn.setOnClickListener(this);

        mRef = FirebaseDatabase.getInstance().getReference();
        Query query = mRef.child("Users").child(encodeUserEmail(userEmail)).child("Groups");
        FirebaseRecyclerOptions<String> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<String>().setQuery(query, String.class).build();
        recyclerAdapter = new FirebaseRecyclerAdapter<String, GroupViewHolder>
                (firebaseRecyclerOptions) {
            @Override
            public GroupViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_info, parent, false);
                return new GroupViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(final GroupViewHolder viewHolder, int position, final String model) {
                FirebaseDatabase.getInstance().getReference().child("Groups").child(model).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        group = dataSnapshot.getValue(Group.class);
                        viewHolder.setGroupName(group.getGroupName().toString());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(GroupList.this, groupCalendar.class);
                                intent.putExtra("group", group);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(GroupList.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    public String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    public String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
