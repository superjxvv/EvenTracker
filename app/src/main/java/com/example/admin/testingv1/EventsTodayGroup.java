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

public class EventsTodayGroup extends AppCompatActivity implements View.OnClickListener{
    private TextView theDate;
    private Button addEvent;
    private FirebaseAuth firebaseAuth;
    private String date;
    private RecyclerView myRecyclerView;
    private DatabaseReference mRef;
    private String userEmail;
    private ArrayList<String> members;
    private int numMembers;
    private Group group;
    private String groupName;
    private Query query;
    private FirebaseRecyclerAdapter<Event, GroupEventViewHolder> recyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_today_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        theDate = (TextView) findViewById(R.id.theDate);
        addEvent = (Button) findViewById(R.id.addEvent);
        firebaseAuth = FirebaseAuth.getInstance();
        myRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent incomingIntent = getIntent();
        date = incomingIntent.getStringExtra("date");
        group = incomingIntent.getParcelableExtra("group");
        members = group.getMembers();
        groupName = group.getGroupID();
        numMembers = members.size();

        String [] info = date.split("/");
        int day = Integer.parseInt(info[0]);
        int month = Integer.parseInt(info [1]);
        int year = Integer.parseInt(info [2]);

        theDate.setText(date);

        mRef = FirebaseDatabase.getInstance().getReference();
        int key = year*10000 +month *100 +day;

        addEvent.setOnClickListener(this);
        query = mRef.child("Groups").child(groupName).child("Events").child(Integer.toString(key));
        FirebaseRecyclerOptions<Event> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        recyclerAdapter = new FirebaseRecyclerAdapter<Event, GroupEventViewHolder>(firebaseRecyclerOptions) {
                @Override
                public GroupEventViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_info_group, parent, false);
                    return new GroupEventViewHolder(view);
                }
                @Override
                protected void onBindViewHolder(GroupEventViewHolder viewHolder, int position, final Event model) {
                    viewHolder.setTitle(model.getTitle());
                    viewHolder.setStartTime(model.getStartTime() + " - ");
                    viewHolder.setEndTime(model.getEndTime());
                    viewHolder.setRemarks(model.getRemarks());
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
        if (view == addEvent) {
            Intent intent = new Intent(EventsTodayGroup.this, addEventGroup.class);
            intent.putExtra("date", date);
            intent.putExtra("group" , group);
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
