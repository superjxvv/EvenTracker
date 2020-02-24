package com.example.admin.eventracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class setName extends AppCompatActivity {

    private EditText groupName;
    private DatabaseReference mRef;
    private String userEmail;
    private DatabaseReference groupDB;
    private ArrayList<String> dates = new ArrayList<>();
    private DataSnapshot event;
    private String groupID;
    private ArrayList<String> participants;
    private Group group;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        userEmail = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
        RecyclerView groupsRecyclerView = findViewById(R.id.groupsRecyclerView);
        TextView numParticipants = findViewById(R.id.numParticipants);
        groupsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        Intent incomingIntent = getIntent();
        participants = incomingIntent.getStringArrayListExtra("checkedMembers");
        numParticipants.setText(getString(R.string.number_of_participants, participants.size()));
        ArrayList<String> decodeparticipants = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            decodeparticipants.add(decodeUserEmail(participants.get(i)));
        }
        RecyclerView.Adapter mAdapter = new MainAdapter(decodeparticipants);
        groupsRecyclerView.setLayoutManager(mLayoutManager);
        groupsRecyclerView.setAdapter(mAdapter);
        FloatingActionButton done = findViewById(R.id.done);
        groupName = findViewById(R.id.groupName);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(setName.this, GroupList.class);
                groupDB = mRef.child("Groups");
                groupID = groupDB.push().getKey();
                group = new Group(participants, groupName.getText().toString().trim(), groupID, userEmail);

                run(participants.size());


//                for(i=0; i<dates.size(); i++){
//                    for(j=0; j<participants.size();j++){
//                        mRef.child("Users").child(participants.get(j)).child("Events").child(dates.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot snapshot) {
//                                if (snapshot.getValue(Event.class) != null) { //event exist in this date
//                                    Iterator<DataSnapshot> events = snapshot.getChildren().iterator();
//                                    while (events.hasNext()) {
//                                        event = events.next();
//                                        mRef.child("Users").child(participants.get(j)).child("Events").child(dates.get(i)).child(event.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot snapshot) {
//                                                Event event_ = snapshot.getValue(Event.class);
//                                                groupDB.child(groupID).child("Events").child(dates.get(i)).child(event_.getEventId()).setValue(event_);
//                                                groupDB.child(groupID).child("Events").child(dates.get(i)).child(event_.getEventId()).child("Emails").child(encodeUserEmail(userEmail)).setValue(encodeUserEmail(userEmail));
//                                            }
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                                Toast.makeText(setName.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                Toast.makeText(setName.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//                groupDB.child(groupID).setValue(group);
//                startActivity(intent);
            }
        });
    }

    public void run(int num) {
        for (int i = 0; i < num; i++) {
            mRef.child("Users").child(participants.get(i)).child("Groups").child(groupID).setValue(groupID);
            mRef.child("Users").child(participants.get(i)).child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot value : snapshot.getChildren()) {
                        dates.add(value.getKey());
                    }
                    dates = makeUnique(dates);

                    for (int k = 0; k < dates.size(); k++) {
                        for (int j = 0; j < participants.size(); j++) {
                            final int finalJ = j;
                            final int finalK = k;
                            mRef.child("Users").child(participants.get(j)).child("Events").child(dates.get(k)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue(Event.class) != null) { //event exist in this date
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            event = dataSnapshot;
                                            mRef.child("Users").child(participants.get(finalJ)).child("Events").child(dates.get(finalK)).child(Objects.requireNonNull(event.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Event event_ = snapshot.getValue(Event.class);
                                                    if (event_ != null) {
                                                        groupDB.child(groupID).child("Events").child(dates.get(finalK)).child(event_.getEventId()).setValue(event_);
                                                        groupDB.child(groupID).child("Events").child(dates.get(finalK)).child(event_.getEventId()).child("Emails").child(encodeUserEmail(userEmail)).setValue(encodeUserEmail(userEmail));
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Toast.makeText(setName.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(setName.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    groupDB.child(groupID).setValue(group);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(setName.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public ArrayList<String> makeUnique(ArrayList<String> list) {
        ArrayList<String> uniqueList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!uniqueList.contains(list.get(i))) {
                uniqueList.add(list.get(i));
            }
        }
        return uniqueList;
    }

    public String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}