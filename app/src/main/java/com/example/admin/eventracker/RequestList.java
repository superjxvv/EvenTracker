package com.example.admin.eventracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.GregorianCalendar;
import java.util.Iterator;

public class RequestList extends AppCompatActivity {

    private RecyclerView myRecyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private String userID;
    private String userEmail;
    private FirebaseRecyclerAdapter<Request, RequestViewHolder> recyclerAdapter;
    private String requestID;
    private ArrayList<String> groups_ = new ArrayList<String>();
    private DatabaseReference eventDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        myRecyclerView = (RecyclerView) findViewById(R.id.requestRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRef = FirebaseDatabase.getInstance().getReference();
        final Query query = mRef.child("Users").child(encodeUserEmail(userEmail)).child("Request");
        FirebaseRecyclerOptions<Request> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Request>().setQuery(query, Request.class).build();
        recyclerAdapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>
                (firebaseRecyclerOptions) {
            @Override
            public RequestViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_info, parent, false);
                return new RequestViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(RequestViewHolder viewHolder, int position, final Request model) {
                if(model.getEventName()!=null) {
                    viewHolder.setDescription(model.getGroupName(), model.getRequester(), model.getEventName(),
                            model.getStartTime(), model.getEndTime(), model.getStartDate(), model.getEndDate());
                } else {
                    viewHolder.setDescription(model.getRequestType(), model.getRequester());
                }
                viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestID = model.getRequestID();
                        String requestType = model.getRequestType();
                        String requester = model.getRequester();
                        if(requestType.equals("friendRequest")){
                            mRef.child("Users").child(encodeUserEmail(userEmail)).child("Friends").child(encodeUserEmail(requester)).setValue(encodeUserEmail(requester));
                            mRef.child("Users").child(encodeUserEmail(requester)).child("Friends").child(encodeUserEmail(userEmail)).setValue(encodeUserEmail(userEmail));
                            mRef.child("Users").child(encodeUserEmail(userEmail)).child("Request").child(requestID).removeValue();
                        } else if (requestType == null) {
                            mRef.child("Users").child(encodeUserEmail(userEmail)).child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Iterator<DataSnapshot> groups = dataSnapshot.getChildren().iterator();
                                    while (groups.hasNext()) {
                                        DataSnapshot group = groups.next();
                                        groups_.add(group.getValue().toString());
                                    }
                                    String[] info = model.getStartDate().split("/");
                                    int day = Integer.parseInt(info[0]);
                                    int month = Integer.parseInt(info[1]);
                                    int year = Integer.parseInt(info[2]);

                                    int start_Date = year * 10000 + month * 100 + day;

                                    String[] info2 = model.getEndDate().split("/");
                                    day = Integer.parseInt(info2[0]);
                                    month = Integer.parseInt(info2[1]);
                                    year = Integer.parseInt(info2[2]);

                                    int end_Date = year * 10000 + month * 100 + day;

                                    int numDays = (end_Date - start_Date) + 1;

                                    int numGroups = groups_.size();
                                    while (numGroups > 0) {
                                        for (int i = 0; i < numDays; i++) {
                                            int key = getKey(start_Date, i);

                                            eventDB = FirebaseDatabase.getInstance().getReference().child("Users").
                                                    child(encodeUserEmail(userEmail)).child("Events").child(Integer.toString(key));
                                            String eventId = eventDB.push().getKey();
                                            String event_name = model.getEventName();
                                            String start_time = model.getStartTime();
                                            String end_time = model.getEndTime();
                                            String start_date = model.getStartDate();
                                            String end_date = model.getEndDate();
                                            String remarks_ = model.getRemarks();
                                            Event event = new Event(event_name, start_time, end_time, start_date, end_date, remarks_, eventId);
                                            eventDB.child(eventId).setValue(event);
                                            mRef.child("Groups").child(groups_.get(numGroups - 1)).child("Events")
                                                    .child(Integer.toString(key)).child(eventId).setValue(event);
                                            mRef.child("Groups").child(groups_.get(numGroups - 1)).child("Events").child(Integer.toString(key))
                                                    .child(eventId).child("Emails").child(encodeUserEmail(userEmail)).setValue(encodeUserEmail(userEmail));
                                        }
                                        numGroups--;
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(RequestList.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            mRef.child("Users").child(encodeUserEmail(userEmail)).child("Request").child(requestID).removeValue();
                        } else {
                            mRef.child("Users").child(encodeUserEmail(requester)).child("AcceptedRequests").child(encodeUserEmail(userEmail)).setValue(encodeUserEmail(userEmail));
                            mRef.child("Users").child(encodeUserEmail(userEmail)).child("Request").child(requestID).removeValue();
                        }
                    }
                });
                viewHolder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestID = model.getRequestID();
                        mRef.child("Users").child(encodeUserEmail(userEmail)).child("Request").child(requestID).removeValue();
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
    private int getKey(int start, int i) {
        int day = start%100;
        int month = start/100%100;
        int year = start/10000;
        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, day);
        gregorianCalendar.add(GregorianCalendar.DAY_OF_MONTH, i);
        day = gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH);
        month = gregorianCalendar.get(GregorianCalendar.MONTH);
        year = gregorianCalendar.get(GregorianCalendar.YEAR);
        return year*10000 + month*100 + day;
    }

    public String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    public String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
