package com.example.admin.testingv1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class groupDetailsLeader extends AppCompatActivity implements View.OnClickListener  {

    private TextView groupName;
    private RecyclerView members;
    private Button leaveGroup;
    private Button backToGroupCalendar;
    private Group group;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private String userID;
    private FirebaseRecyclerAdapter<String, MemberViewHolder> recyclerAdapter;
    private String email;
    private Button editGroup;
    private ArrayList<String> eventIDs;
    private ArrayList<String> dates;
    private DataSnapshot groupDate_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_details_leader);

        groupName = (TextView) findViewById(R.id.Description);
        members = (RecyclerView) findViewById(R.id.members);
        editGroup = (Button) findViewById(R.id.editGroupBtn);
        leaveGroup = (Button) findViewById(R.id.leaveGroup);
        backToGroupCalendar = (Button) findViewById(R.id.backToGroupCalendar);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference();

        Intent incomingIntent = getIntent();
        group = incomingIntent.getParcelableExtra("group");
        groupName.setText(group.getGroupName());
        members.setHasFixedSize(true);
        members.setLayoutManager(new LinearLayoutManager(this));
        FirebaseUser user = firebaseAuth.getCurrentUser();
        email = user.getEmail();

        //Query query = mRef.child("Users").child(encodeUserEmail(email)).child(userID).child("Events").child(Integer.toString(key));

        Query query = mRef.child("Groups").child(group.getGroupID()).child("members");
        FirebaseRecyclerOptions<String> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<String>().setQuery(query, String.class).build();
        recyclerAdapter =new FirebaseRecyclerAdapter<String, MemberViewHolder>
                (firebaseRecyclerOptions) {
            @Override
            public MemberViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_info, parent, false);
                return new MemberViewHolder(view);
            }
            @Override
            protected void onBindViewHolder ( final MemberViewHolder viewHolder, int position,
                                              final String model){
                viewHolder.setMember(model);
                if(group.getLeader().equals(decodeUserEmail(model))){
                    viewHolder.setAdmin();
                }
            }
        };

        members.setAdapter(recyclerAdapter);
        editGroup.setOnClickListener(this);
        backToGroupCalendar.setOnClickListener(this);
        leaveGroup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == leaveGroup) {
            if(group.getSize()>=2) {
                Intent intent = new Intent(this, GroupList.class);
                mRef.child("Groups").child(group.getGroupID()).child("members").child(encodeUserEmail(email)).removeValue();
                mRef.child("Users").child(encodeUserEmail(email)).child("Groups").child(group.getGroupID()).removeValue();
                group.setLeader(decodeUserEmail(group.getMembers().get(0)));
                //need to add deleting of user's events from groups>Events
                deleteEvents(encodeUserEmail(email));
                startActivity(intent);
            } else { //group is deleted
                Intent intent = new Intent(this, GroupList.class);
                mRef.child("Groups").child(group.getGroupID()).removeValue();
                mRef.child("Users").child(encodeUserEmail(email)).child("Groups").child(group.getGroupID()).removeValue();
                group.removeMember(decodeUserEmail(email));
                startActivity(intent);
            }
        }

        if (view == backToGroupCalendar){
            Intent intent = new Intent (groupDetailsLeader.this, groupCalendar.class);
            intent.putExtra("group", group);
            startActivity(intent);
        }

        if (view == editGroup) {
            Intent intent = new Intent (groupDetailsLeader.this, editGroup.class);
            intent.putExtra("group", group);
            startActivity(intent);
        }
    }

    public void deleteEvents (String email) {
        findEventIDs(email);
        mRef.child("Groups").child(group.getGroupID()).child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Iterator<DataSnapshot> groupDate = snapshot.getChildren().iterator();
                while (groupDate.hasNext()) {
                    groupDate_ = groupDate.next();
                    if (dates.contains(groupDate_.getValue(String.class))) {
                        mRef.child("Groups").child(group.getGroupID()).child("Events").child(groupDate_.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Iterator<DataSnapshot> eventID = snapshot.getChildren().iterator();
                                while (eventID.hasNext()) {
                                    DataSnapshot eventID_ = eventID.next();
                                    if (eventIDs.contains(eventID_.getValue(Event.class).getEventId())) {
                                        mRef.child("Groups").child(group.getGroupID()).child("Events").child(groupDate_.getValue(String.class)).child(eventID_.getValue(Event.class).getEventId()).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(groupDetailsLeader.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(groupDetailsLeader.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void findEventIDs (String email) {
        findDates(email);
        for (int i = 0; i < dates.size(); i++) {
            mRef.child("Users").child(email).child("Events").child(dates.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Iterator<DataSnapshot> eventID = snapshot.getChildren().iterator();
                    while (eventID.hasNext()) {
                        DataSnapshot eventID_ = eventID.next();
                        eventIDs.add(eventID_.getValue(Event.class).getEventId());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(groupDetailsLeader.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void findDates (String email) {
        mRef.child("Users").child(email).child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Iterator<DataSnapshot> date = snapshot.getChildren().iterator();
                while (date.hasNext()) {
                    DataSnapshot date_ = date.next();
                    dates.add(date_.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(groupDetailsLeader.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

