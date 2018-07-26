package com.example.admin.testingv1;

import android.content.Intent;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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
    private ArrayList <String> dates = new ArrayList<String>();
    private int i, j;
    private DataSnapshot event;
    private String groupID;
    private ArrayList<String> participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        groupsRecyclerView = (RecyclerView) findViewById(R.id.groupsRecyclerView);
        numParticipants = (TextView) findViewById(R.id.numParticipants);
        groupsRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        Intent incomingIntent = getIntent();
        participants = incomingIntent.getStringArrayListExtra("checkedMembers");
        numParticipants.setText("Number of Participants: " + participants.size());
        ArrayList <String> decodeparticipants = new ArrayList<String>();
        for(int i=0; i<participants.size(); i++){
            decodeparticipants.add(decodeUserEmail(participants.get(i)));
        }
        mAdapter = new MainAdapter(decodeparticipants);
        groupsRecyclerView.setLayoutManager(mLayoutManager);
        groupsRecyclerView.setAdapter(mAdapter);
        done = (FloatingActionButton) findViewById(R.id.done);
        groupName = (EditText) findViewById(R.id.groupName);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setName.this, GroupList.class);
                groupDB = mRef.child("Groups");
                groupID = groupDB.push().getKey();
                Group group = new Group(participants, groupName.getText().toString().trim(), groupID, userEmail);
                if(getAllDates(participants.size())){
                    dates = makeUnique(dates);
                }

                for(i=0; i<dates.size(); i++){
                    for(j=0; j<participants.size();j++){
                        mRef.child("Users").child(participants.get(j)).child("Events").child(dates.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.getValue(Event.class) != null) { //event exist in this date
                                    Iterator<DataSnapshot> events = snapshot.getChildren().iterator();
                                    while (events.hasNext()) {
                                        event = events.next();
                                        mRef.child("Users").child(participants.get(j)).child("Events").child(dates.get(i)).child(event.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                Event event_ = snapshot.getValue(Event.class);
                                                groupDB.child(groupID).child("Events").child(dates.get(i)).child(event_.getEventId()).setValue(event_);
                                                groupDB.child(groupID).child("Events").child(dates.get(i)).child(event_.getEventId()).child("Emails").child(encodeUserEmail(userEmail)).setValue(encodeUserEmail(userEmail));
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
        });
    }

    public boolean getAllDates (int num) {
        for (int i = 0; i < num; i++) {
            mRef.child("Users").child(participants.get(i)).child("Groups").child(groupID).setValue(groupID);
            mRef.child("Users").child(participants.get(i)).child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Iterator<DataSnapshot> values = snapshot.getChildren().iterator();
                    while (values.hasNext()) {
                        DataSnapshot value = values.next();
                        dates.add(value.getKey());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(setName.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return true;
    }

    public ArrayList makeUnique(ArrayList<String> list) {
        ArrayList<String> uniqueList = new ArrayList<String>();
        for(int i=0; i<list.size(); i++) {
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
