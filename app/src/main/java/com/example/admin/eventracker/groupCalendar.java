package com.example.admin.eventracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class groupCalendar extends AppCompatActivity implements View.OnClickListener {
    private CalendarView calendarView;
    private TextView groupName;
    private FirebaseAuth firebaseAuth;
    private Group group;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_calendar);

        calendarView = (CalendarView) findViewById(R.id.groupCalenderView);
        groupName = (TextView) findViewById(R.id.Description);
        firebaseAuth = FirebaseAuth.getInstance();

        userEmail = firebaseAuth.getCurrentUser().getEmail();
        Intent incomingIntent = getIntent();
        group = incomingIntent.getParcelableExtra("group");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                Intent intent = new Intent(groupCalendar.this, EventsTodayGroup.class);
                intent.putExtra("date", date);
                intent.putExtra("group", group);
                startActivity(intent);
            }
        });
        groupName.setOnClickListener(this);
        groupName.setText(group.getGroupName());
    }

    @Override
    public void onClick(View view) {
        if (view == groupName) {
            if (!(group.getLeader().equals(userEmail))) {
                Intent intent = new Intent(this, groupDetails.class);
                intent.putExtra("group", group);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, GroupDetailsLeader_.class);
                intent.putExtra("group", group);
                startActivity(intent);
            }
        }
    }
}
