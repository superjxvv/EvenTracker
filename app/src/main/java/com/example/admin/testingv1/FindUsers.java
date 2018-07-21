package com.example.admin.testingv1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindUsers extends AppCompatActivity implements View.OnClickListener {
    private EditText searchTab;
    private TextView userFound;
    private Button enterBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);
        firebaseAuth = FirebaseAuth.getInstance();

        enterBtn = (Button) findViewById(R.id.enterBtn);
        searchTab = (EditText) findViewById(R.id.searchTab);
        userFound = (TextView) findViewById(R.id.userFound);

        enterBtn.setOnClickListener(this);
        userFound.setOnClickListener(this);
        searchTab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == enterBtn) {
            email = searchTab.getText().toString().trim();
            myRef = FirebaseDatabase.getInstance().getReference().child("Users");
            myRef.child(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        //user exists
                        userFound.setText(email);
                        Toast.makeText(FindUsers.this, "User found!", Toast.LENGTH_SHORT).show();
                    } else {
                        //user does not exist
                        Toast.makeText(FindUsers.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(FirebaseError arg0) {
                    Toast.makeText(FindUsers.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (view == userFound) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (userFound.getText() != null) {
                if (userFound.getText().equals(user.getEmail())) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else {
                    Intent intent = new Intent(this, UserInfo.class);
                    intent.putExtra("Email", email);
                    startActivity(intent);
                }
            }
        }
    }
}
