package com.example.admin.testingv1;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class setName extends AppCompatActivity {

    private EditText groupName;
    private TextView numParticipants;
    private FloatingActionButton done;
    private RecyclerView groupsRecyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private String userID;
    private String userEmail;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private DatabaseReference groupDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        firebaseAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        groupsRecyclerView = (RecyclerView) findViewById(R.id.groupsRecyclerView);
        numParticipants = (TextView) findViewById(R.id.numParticipants);
        groupsRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        Intent incomingIntent = getIntent();
        final ArrayList<String> participants = incomingIntent.getStringArrayListExtra("checkedMembers");
        numParticipants.setText("Number of Participants: " + participants.size());
        mAdapter = new MainAdapter(participants);
        groupsRecyclerView.setLayoutManager(mLayoutManager);
        groupsRecyclerView.setAdapter(mAdapter);
        done = (FloatingActionButton) findViewById(R.id.done);
        groupName = (EditText) findViewById(R.id.groupName);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setName.this, GroupList.class);
                groupDB = mRef.child("Groups");
                String groupID = groupDB.push().getKey();
                Group group = new Group(participants, groupName.getText().toString().trim(), groupID);
                for (int i = 0; i < participants.size(); i++) {
                    mRef.child("Users").child(participants.get(i)).child("Groups").child(groupID).setValue(groupID);
                }
                groupDB.child(groupID).setValue(group);
                startActivity(intent);
            }
        });
    }
    public String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    public String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
