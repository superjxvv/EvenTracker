package com.example.admin.testingv1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RequestList extends AppCompatActivity implements View.OnClickListener {
    private Button backBtn;
    private RecyclerView myRecyclerView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private String userID;
    private String userEmail;
    private FirebaseRecyclerAdapter<Request, RequestViewHolder> recyclerAdapter;
    private String requestID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        backBtn = (Button) findViewById(R.id.backBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        myRecyclerView = (RecyclerView) findViewById(R.id.requestRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        backBtn.setOnClickListener(this);

        mRef = FirebaseDatabase.getInstance().getReference();
        final Query query = mRef.child("Users").child(userEmail).child("Request");
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
                viewHolder.setDescription(model.getRequester(), model.getRequestType());
                viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestID = model.getRequestID();
                        String requestType = model.getRequestType();
                        String requester = model.getRequester();
                        if(requestType.equals("friendRequest")){
                            mRef.child("Users").child(userEmail).child("Friends").child(requester);
                            mRef.child("Users").child(requester).child("Friends").child(userEmail);
                        } else {

                            mRef.child("Users").child(requester).child("AcceptedRequests").setValue(userEmail);
                            mRef.child("Users").child(userEmail).child("Request").child(requestID).removeValue();
                        }
                    }
                });
                viewHolder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestID = model.getRequestID();
                        mRef.child("Users").child(userEmail).child("Request").child(requestID).removeValue();
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
        if (view == backBtn) {
            Intent intent = new Intent(RequestList.this, ProfileActivity.class);
            startActivity(intent);
        }
    }
}
