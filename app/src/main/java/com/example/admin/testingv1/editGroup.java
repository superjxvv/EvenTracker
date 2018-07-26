package com.example.admin.testingv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class editGroup extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private RecyclerView members;
    private DatabaseReference mRef;
    private String userEmail;
    private ArrayList<String> members;
    private int numMembers;
    private Group group;
    private Query query;
    private FirebaseRecyclerAdapter<Event, GroupEventViewHolder> recyclerAdapter;
    private TextView groupName;
    private Button addMembers;
    private Button leaveGroup;
    private Button backBtn;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        groupName = (TextView) findViewById(R.id.groupName);
        members = (RecyclerView) findViewById(R.id.recyclerView);
        leaveGroup = (Button) findViewById(R.id.leaveBtn);
        backBtn = (Button) findViewById(R.id.BackBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        Intent incomingIntent = getIntent();
        group = incomingIntent.getParcelableExtra("group");
        groupName.setText(group.getGroupName());
        members.setHasFixedSize(true);
        members.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUser user = firebaseAuth.getCurrentUser();
        email = firebaseAuth.getCurrentUser().getEmail();
    }
}
