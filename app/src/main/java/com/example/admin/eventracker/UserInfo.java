package com.example.admin.eventracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.Iterator;

public class UserInfo extends AppCompatActivity {
    private String email;
    private Button requestBtn;
    private TextView unfriendBtn;
    private FirebaseAuth firebaseAuth;
    private String userEmail_;
    private DatabaseReference myRef;
    private TextView email_;
    private boolean calenderRequested = false;
    private Boolean requested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent incomingIntent = getIntent();
        email = incomingIntent.getStringExtra("Email");

        email_ = (TextView)findViewById(R.id.userEmail);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail_ = user.getEmail();
        requestBtn = (Button) findViewById(R.id.requestBtn);
        unfriendBtn = (TextView) findViewById(R.id.unFriendBtn);
        unfriendBtn.setText("");

        firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        email_.setText(decodeUserEmail(email));
        isFriend ();

    }

    private void isFriend (){
        myRef.child(encodeUserEmail(userEmail_)).child("Friends").child(encodeUserEmail(email)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    alreadyRequest();
                    requestBtn.setText("Send request to view calendar");
                    unfriendBtn.setText("Unfriend");
                    unfriendBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myRef.child(encodeUserEmail(userEmail_)).child("Friends").child(encodeUserEmail(email)).removeValue();
                            myRef.child(encodeUserEmail(email)).child("Friends").child(encodeUserEmail(userEmail_)).removeValue();
                            Toast.makeText(UserInfo.this, "Unfriended.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //user does not exist
                    requestBtn.setText("Send friend request");
                }
                requestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (requestBtn.getText().equals("Send friend request")){
                            alreadyRequest ();
                            if(!requested) {
                                String requestId = myRef.child(encodeUserEmail(email)).child("Request").push().getKey();
                                Request request = new Request(userEmail_, "friendRequest", requestId);
                                myRef.child(encodeUserEmail(email)).child("Request").child(requestId).setValue(request);
                                Toast.makeText(UserInfo.this, "Request sent!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(UserInfo.this, "Already sent request.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            alreadyRequest();
                            if (!requested) {
                                String requestId = myRef.child(encodeUserEmail(email)).child("Request").push().getKey();
                                Request request = new Request(userEmail_, "ViewCalendarRequest", requestId);
                                myRef.child(encodeUserEmail(email)).child("Request").child(requestId).setValue(request);
                                Toast.makeText(UserInfo.this, "Request sent!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(UserInfo.this, "Already sent request.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserInfo.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void alreadyRequest (){
        myRef.child(encodeUserEmail(email)).child("Request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Iterator<DataSnapshot> requests = snapshot.getChildren().iterator();
                while (requests.hasNext()) {
                    DataSnapshot request = requests.next();
                    if((request.getValue(Request.class)).getRequester().equals(userEmail_)) {
                        requested = true;
                        return;
                    }
                }
                requested = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserInfo.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
