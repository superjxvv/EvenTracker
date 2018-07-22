package com.example.admin.testingv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class groupDetails extends AppCompatActivity {

    private TextView groupName;
    private RecyclerView members;
    private Button leaveGroup;
    private Button backToGroupCalendar;
    private Group group;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;
    private String userID;
    private FirebaseRecyclerAdapter<Event, EventViewHolder> recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        groupName = (TextView) findViewById(R.id.name);
        members = (RecyclerView) findViewById(R.id.members);
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
        String email = user.getEmail();

        //Query query = mRef.child("Users").child(encodeUserEmail(email)).child(userID).child("Events").child(Integer.toString(key));
    }

    public String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    public String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
