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
import android.widget.CheckBox;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Notifications extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private RecyclerView members;
    private DatabaseReference mRef;
    private String requester;
    private String userEmail;
    private FirebaseRecyclerAdapter<String, AcceptedRequestsViewHolder> recyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            firebaseAuth = FirebaseAuth.getInstance();
            userEmail = firebaseAuth.getCurrentUser().getEmail();
            mRef = FirebaseDatabase.getInstance().getReference();
            members = (RecyclerView) findViewById(R.id.notifications);
            members.setHasFixedSize(true);
            members.setLayoutManager(new LinearLayoutManager(this));
            Query query = mRef.child("Users").child(encodeUserEmail(userEmail)).child("AcceptedRequests");
            FirebaseRecyclerOptions<String> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<String>().setQuery(query, String.class).build();
            recyclerAdapter = new FirebaseRecyclerAdapter<String, AcceptedRequestsViewHolder>
                    (firebaseRecyclerOptions) {
                @Override
                public AcceptedRequestsViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accepted_requests_info, parent, false);
                    return new AcceptedRequestsViewHolder(view);
                }
                @Override
                protected void onBindViewHolder(AcceptedRequestsViewHolder viewHolder, int position, final String model) {
                    requester = decodeUserEmail(model);
                    viewHolder.setDescription(requester);
                    viewHolder.viewCalendar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Notifications.this, ProfileActivity.class);
                            intent.putExtra("userEmail", requester);
                            mRef.child("Users").child(encodeUserEmail(userEmail)).child("AcceptedRequests").child(model).removeValue();
                            startActivity(intent);
                            }
                        });
                }
            };
            members.setAdapter(recyclerAdapter);
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

        public String encodeUserEmail(String userEmail) {
            return userEmail.replace(".", ",");
        }
        public String decodeUserEmail(String userEmail) {
            return userEmail.replace(",", ".");
        }
    }
