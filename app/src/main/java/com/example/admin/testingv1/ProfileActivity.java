package com.example.admin.testingv1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private CalendarView calendarView;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        Intent incomingIntent = getIntent();
        userEmail = incomingIntent.getStringExtra("userEmail");
        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        if(userEmail!=null){
            textViewUserEmail.setText(userEmail+"'s Calendar.");
        } else {
            textViewUserEmail.setText("Welcome " + user.getEmail());
        }
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                Intent intent = new Intent(ProfileActivity.this, EventsToday.class);
                intent.putExtra("date", date);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }else if(item.getItemId()==R.id.friends) {
            Intent intent = new Intent(this, FriendList.class);
            startActivity(intent);
        }else if(item.getItemId()==R.id.groups) {
            Intent intent = new Intent(this, GroupList.class);
            startActivity(intent);
        }else if(item.getItemId()==R.id.notifications) {
            Intent intent = new Intent(this, RequestList.class);
            startActivity(intent);
        }else if(item.getItemId()==R.id.feedback) {
            startActivity(new Intent(this, EmailFeedBack.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
