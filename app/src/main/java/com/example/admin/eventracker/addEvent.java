package com.example.admin.eventracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class addEvent extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = addEvent.class.getSimpleName();
    private TextView theDate;
    private FirebaseAuth firebaseAuth;
    private TextView startDate;
    private TextView endDate;
    private DatePickerDialog.OnDateSetListener startDateSetListener;
    private DatePickerDialog.OnDateSetListener endDateSetListener;
    private String date;
    private TextView startTime;
    private TextView endTime;
    private TimePickerDialog.OnTimeSetListener startTimeSetListener;
    private TimePickerDialog.OnTimeSetListener endTimeSetListener;
    private DatabaseReference eventDB;
    private DatabaseReference mRef;
    private ArrayList<String> groups_ = new ArrayList<String>();
    private Button addEventBtn;
    private EditText eventName;
    private EditText remarks;
    private int start_Date =0;
    private int end_Date = 0;
    private int start_Time = 800;
    private int end_Time = 900;
    private String eventId;
    private String event_name;
    private String start_time;
    private String end_time;
    private String start_date;
    private String end_date ;
    private String remarks_;
    private Event event;
    private CheckBox mCheckBox;
    private Boolean isPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        theDate = (TextView) findViewById(R.id.theDate);
        addEventBtn = (Button) findViewById(R.id.addEvent);
        firebaseAuth = FirebaseAuth.getInstance();

        mCheckBox = (CheckBox) findViewById(R.id.checkBoxPrivacy);

        Intent incomingIntent = getIntent();
        date = incomingIntent.getStringExtra("date");
        String [] info = date.split("/");
        int day = Integer.parseInt(info[0]);
        int month = Integer.parseInt(info [1]);
        int year = Integer.parseInt(info [2]);

        start_Date = year *10000 + month *100 + day;
        end_Date = start_Date;

        remarks = (EditText) findViewById(R.id.remarks);
        eventName = (EditText) findViewById(R.id.eventName);
        startTime = (TextView) findViewById(R.id.startTime);
        endTime = (TextView) findViewById(R.id.endTime);
        startTime.setText("08:00");
        endTime.setText("09:00");
        startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, "onTimeSet: hh:mm " + hourOfDay + ":" + minute);
                start_Time = hourOfDay *100 + minute;
                startTime.setText(getTime(hourOfDay, minute));
                if(start_Date == end_Date && end_Time<start_Time){
                    endTime.setText(getTime(hourOfDay, minute));
                    end_Time = start_Time;
                }
            }
        };
        endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, "onTimeSet: hh:mm " + hourOfDay + ":" + minute);
                int newEndTime = hourOfDay *100 + minute;
                if (end_Date >start_Date ) {
                    endTime.setText(getTime(hourOfDay, minute));
                    end_Time = newEndTime;
                } else if(newEndTime >= start_Time) {
                    endTime.setText(getTime(hourOfDay, minute));
                    end_Time = newEndTime;
                } else {
                    Toast.makeText(addEvent.this, "End time should be after start time.", Toast.LENGTH_LONG).show();
                }
            }
        };

        startDate = (TextView) findViewById(R.id.startDate);
        endDate = (TextView) findViewById(R.id.endDate);
        startDate.setText(date);
        endDate.setText(date);
        theDate.setText(date);

        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        addEventBtn.setOnClickListener(this);
        mCheckBox.setOnClickListener(this);

        startDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG,"onDateSet: dd/mm/yyyy: " + month + "/" + dayOfMonth + "/" + year);

                String date = dayOfMonth + "/" + month + "/" + year;
                start_Date = year *10000 + month *100 + dayOfMonth;
                startDate.setText(date);
                if(end_Date<start_Date) {
                    endDate.setText(date);
                    end_Date = start_Date;
                }
            }
        };
        endDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG,"onDateSet: dd/mm/yyyy: " + month + "/" + dayOfMonth + "/" + year);

                String date = dayOfMonth + "/" + month + "/" + year;
                int newEndDate = year *10000 + month *100 + dayOfMonth;
                if(newEndDate >= start_Date) {
                    end_Date = newEndDate;
                    endDate.setText(date);
                } else {
                    Toast.makeText(addEvent.this, "End date must be before start date.", Toast.LENGTH_LONG).show();
                }
                if(newEndDate == start_Date && start_Time > end_Time) {
                    end_Time = start_Time;
                    endTime.setText(getTime(end_Time/100, end_Time%100));
                }
            }
        };

    }

    public void onClick(View view) {
        if(view == startDate) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    android.R.style.Theme_Holo_Dialog,
                    startDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
        if(view == endDate) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    android.R.style.Theme_Holo_Dialog,
                    endDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
        if(view == startTime) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    startTimeSetListener,
                    hour, minute, true);
            dialog.show();
        }
        if(view == endTime) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    endTimeSetListener,
                    hour, minute, true);
            dialog.show();
        }

        if(view == mCheckBox) {
            boolean checked = ((CheckBox) view).isChecked();
            if (checked) {
                isPrivate = true;
            } else {
                isPrivate = false;
            }
        }

        if(view == addEventBtn) {
            Toast.makeText(addEvent.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
            final String email = firebaseAuth.getCurrentUser().getEmail();
            mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child("Users").child(encodeUserEmail(email)).child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> groups = dataSnapshot.getChildren().iterator();
                    while (groups.hasNext()) {
                        DataSnapshot group = groups.next();
                        groups_.add(group.getValue().toString());
                    }
                    addEventToDatabase(email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(addEvent.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }

            /*@Override
            public void onCancelled(FirebaseError arg0) {
                Toast.makeText(UserInfo.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
            }
            */
            });
        }
    }

    public void addEventToDatabase(String email){
        int numGroup = groups_.size();
        int numDays = (end_Date - start_Date)+1;
        int j = -1;
        eventDB = FirebaseDatabase.getInstance().getReference().child("Users").
                child(encodeUserEmail(email)).child("Events");
        eventId = eventDB.push().getKey();
        event_name = eventName.getText().toString().trim();
        start_time = startTime.getText().toString().trim();
        end_time = endTime.getText().toString().trim();
        start_date = startDate.getText().toString().trim();
        end_date = endDate.getText().toString().trim();
        remarks_ = remarks.getText().toString().trim();
        event = new Event(event_name, start_time, end_time, start_date, end_date, remarks_, eventId, email, isPrivate);
        while (numGroup>j){
            for (int i = 0; i < numDays; i++) {
                int key = getKey(start_Date, i);
                if (j == -1) {
                    eventDB.child(Integer.toString(key)).child(eventId).setValue(event);
                }
                if (j >= 0) {
                    mRef.child("Groups").child(groups_.get(j)).child("Events").child(Integer.toString(key)).child(eventId).setValue(event);
                mRef.child("Groups").child(groups_.get(j)).child("Events").child(Integer.toString(key))
                        .child(eventId).child("Emails").child(encodeUserEmail(email)).setValue(encodeUserEmail(email));
                }
            }
            j++;
        }
        Intent intent = new Intent(addEvent.this, EventsToday.class);
        intent.putExtra("date", date);
        startActivity(intent);
    }

    private String getTime (int hourOfDay, int minute){
        String time;
        if(hourOfDay <10 && minute <10){
            time = "0" + hourOfDay + ":0" + minute;
        } else if(hourOfDay<10) {
            time = "0" +hourOfDay + ":" + minute;
        } else if (minute<10) {
            time = hourOfDay + ":0" + minute;
        } else {
            time = hourOfDay + ":" + minute;
        }
        return time;
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
    /*private String getEndTime (int hourOfDay, int minute){
        String time;
        if(hourOfDay <9 && minute <10){
            time = "0" + hourOfDay + ":0" + minute;
        } else if(hourOfDay<9) {
            time = "0" +hourOfDay + ":" + minute;
        } else if (minute<10) {
            time = hourOfDay+1 + ":0" + minute;
        } else {
            time = hourOfDay+1 + ":" + minute;
        }

        if(hourOfDay >=23){
            time = "00:" + minute;
            int day = end_Date%100;
            int month = end_Date/100%100;
            int year = end_Date/1000;
            endDate.setText(day+"/"+month+"/"+ year);
            end_Date =
        }
        return time;
    }
    */
}

