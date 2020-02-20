package com.example.admin.eventracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class GroupDetailsLeader_ extends AppCompatActivity  implements View.OnClickListener  {

    private TextView groupName;
    private RecyclerView members;
    private Button leaveGroup;
    private Button backToGroupCalendar;
    private Group group;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private String email;
    private Button editGroup;
    private ArrayList<String> eventIDs = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();
    private DataSnapshot groupDate_;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details_leader_);

        groupName = (TextView) findViewById(R.id.Description);
        members = (RecyclerView) findViewById(R.id.members);
        editGroup = (Button) findViewById(R.id.editGroupBtn);
        leaveGroup = (Button) findViewById(R.id.leaveGroup);
        backToGroupCalendar = (Button) findViewById(R.id.backToGroupCalendar);
        firebaseAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        Intent incomingIntent = getIntent();
        group = incomingIntent.getParcelableExtra("group");
        groupName.setText(group.getGroupName());
        members.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        email = user.getEmail();

        mAdapter = new MainAdapter2(group);
        members.setLayoutManager(mLayoutManager);
        members.setAdapter(mAdapter);

        editGroup.setOnClickListener(this);
        backToGroupCalendar.setOnClickListener(this);
        leaveGroup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == leaveGroup) {
            if(group.getSize()>=2) {
                Intent intent = new Intent(this, GroupList.class);
                group.removeMember(encodeUserEmail(group.getLeader()));
                group.setLeader(decodeUserEmail(group.getMembers().get(0)));

                mRef.child("Groups").child(group.getGroupID()).child("members").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> emails = dataSnapshot.getChildren().iterator();
                        while (emails.hasNext()) {
                            DataSnapshot email_ = emails.next();
                            if(email_.getValue(String.class).equals(encodeUserEmail(email))){
                                mRef.child("Groups").child(group.getGroupID()).child("members").child(email_.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(GroupDetailsLeader_.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                mRef.child("Groups").child(group.getGroupID()).child("leader").setValue(decodeUserEmail(group.getMembers().get(0)));

                mRef.child("Groups").child(group.getGroupID()).child("size").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int size = dataSnapshot.getValue(Integer.class);
                        size--;
                        mRef.child("Groups").child(group.getGroupID()).child("size").setValue(size);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(GroupDetailsLeader_.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                mRef.child("Users").child(encodeUserEmail(email)).child("Groups").child(group.getGroupID()).removeValue();
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
            Intent intent = new Intent (GroupDetailsLeader_.this, groupCalendar.class);
            intent.putExtra("group", group);
            startActivity(intent);
        }

        if (view == editGroup) {
            Intent intent = new Intent (GroupDetailsLeader_.this, editGroup.class);
            intent.putExtra("group", group);
            startActivity(intent);
        }
    }

    public void deleteEvents (final String email) {
        findEventIDs(email);
        mRef.child("Groups").child(group.getGroupID()).child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Iterator<DataSnapshot> groupDate = snapshot.getChildren().iterator();
                while (groupDate.hasNext()) {
                    groupDate_ = groupDate.next();
                    if (dates.contains(groupDate_.getKey())) {
                        mRef.child("Groups").child(group.getGroupID()).child("Events").child(groupDate_.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Iterator<DataSnapshot> eventID = snapshot.getChildren().iterator();
                                while (eventID.hasNext()) {
                                    DataSnapshot eventID_ = eventID.next();
                                    if (eventIDs.contains(eventID_.getValue(Event.class).getEventId())) {
                                        mRef.child("Groups").child(group.getGroupID()).child("Events").child(groupDate_.getKey())
                                                .child(eventID_.getValue(Event.class).getEventId()).child("Emails").child(email).removeValue();
                                        checkMembers(group.getGroupID(), Integer.parseInt(groupDate_.getKey()), eventID_.getValue(Event.class).getEventId());
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(GroupDetailsLeader_.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GroupDetailsLeader_.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void findEventIDs (final String email) {
        mRef.child("Users").child(email).child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Iterator<DataSnapshot> date = snapshot.getChildren().iterator();
                while (date.hasNext()) {
                    DataSnapshot date_ = date.next();
                    dates.add(date_.getKey());
                }
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
                            Toast.makeText(GroupDetailsLeader_.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GroupDetailsLeader_.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkMembers(final String groupID, final int code, final String eventId){
        mRef.child("Groups").child(groupID).child("Events").child(Integer.toString(code))
                .child(eventId).child("Emails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue()==null){
                    mRef.child("Groups").child(groupID).child("Events").child(Integer.toString(code))
                            .child(eventId).removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GroupDetailsLeader_.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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