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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
    private int numMembers;
    private Group group;
    private Query query;
    private FirebaseRecyclerAdapter<String, EditMemberViewHolder> recyclerAdapter;
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
        addMembers = (Button) findViewById(R.id.AddBtn);
        mRef = FirebaseDatabase.getInstance().getReference();

        Intent incomingIntent = getIntent();
        group = incomingIntent.getParcelableExtra("group");
        groupName.setText(group.getGroupName());
        members.setHasFixedSize(true);
        members.setLayoutManager(new LinearLayoutManager(this));
        email = firebaseAuth.getCurrentUser().getEmail();

        Query query = mRef.child("Groups").child(group.getGroupID()).child("members");
        FirebaseRecyclerOptions<String> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<String>().setQuery(query, String.class).build();
        recyclerAdapter = new FirebaseRecyclerAdapter<String, EditMemberViewHolder>(firebaseRecyclerOptions) {
            @Override
            public EditMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_member_info, parent, false);
                return new EditMemberViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final EditMemberViewHolder viewHolder, int position,
                                            final String model) {
                viewHolder.setMember(decodeUserEmail(model));
                if (group.getLeader().equals(decodeUserEmail(model))) {
                    viewHolder.kickBtn.setText("Me");
                } else {
                    viewHolder.kickBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewHolder.kickMember(group, decodeUserEmail(model));
                            mRef.child("users").child(model).child("Groups").child(group.getGroupID()).removeValue();
                            //need add removing user's events in group > events
                            Intent intent = new Intent(editGroup.this, editGroup.class);
                            intent.putExtra("Group", group);
                            startActivity(intent);
                        }
                    });
                }
            }
        };

        members.setAdapter(recyclerAdapter);
        backBtn.setOnClickListener(this);
        leaveGroup.setOnClickListener(this);
        addMembers.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == leaveGroup) {
            if (group.getSize() > 2) {
                Intent intent = new Intent(this, chooseNewLeader.class);
                mRef.child("Groups").child(group.getGroupID()).child("members").child(encodeUserEmail(email)).removeValue();
                mRef.child("Users").child(encodeUserEmail(email)).child("Groups").child(group.getGroupID()).removeValue();
                group.removeMember(decodeUserEmail(email));
                intent.putExtra("group", group);
                //need to add deleting of user's events from groups>Events
                startActivity(intent);
            } else if (group.getSize() == 1) {
                Intent intent = new Intent(this, GroupList.class);
                mRef.child("Groups").child(group.getGroupID()).removeValue();
                mRef.child("Users").child(encodeUserEmail(email)).child("Groups").child(group.getGroupID()).removeValue();
                group.removeMember(decodeUserEmail(email));
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, GroupList.class);
                mRef.child("Groups").child(group.getGroupID()).child("members").child(encodeUserEmail(email)).removeValue();
                mRef.child("Users").child(encodeUserEmail(email)).child("Groups").child(group.getGroupID()).removeValue();
                group.removeMember(decodeUserEmail(email));
                group.setLeader(decodeUserEmail(group.getMembers().get(0)));
                //need to add deleting of user's events from groups>Events
                startActivity(intent);
            }
        }

        if (view == backBtn) {
            Intent intent = new Intent(editGroup.this, groupCalendar.class);
            intent.putExtra("group", group);
            startActivity(intent);
        }

        if (view == addMembers) {
            Intent intent = new Intent(editGroup.this, addGroupMembers.class);
            intent.putExtra("group", group);
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

