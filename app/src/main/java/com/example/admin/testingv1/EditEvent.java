package com.example.admin.testingv1;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class EditEvent extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = addEvent.class.getSimpleName();
    private TextView theDate;
    private Button backToEventsToday;
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
    private Button save;
    private Button delete;
    private EditText eventName;
    private EditText remarks;
    private String userId;
    private Event event;
    private DatabaseReference mRef;
    private ArrayList<String> groups_ = new ArrayList<String>();
    private int start_Date;
    private int end_Date;
    private int start_Time;
    private int end_Time;
    private String email;
    private String eventId;
    private int numGroups;
    private int key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        theDate = (TextView) findViewById(R.id.theDate);
        backToEventsToday = (Button) findViewById(R.id.backToEventsToday);
        save = (Button) findViewById(R.id.save);
        delete = (Button) findViewById(R.id.delete);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        Intent incomingIntent = getIntent();
        date = incomingIntent.getStringExtra("date");
        event = incomingIntent.getParcelableExtra("Event");

        start_Date = dateConvert(event.getStartDate());
        end_Date = dateConvert(event.getEndDate());
        start_Time = timeConvert(event.getStartTime());
        end_Time = timeConvert(event.getEndTime());

        remarks = (EditText) findViewById(R.id.remarks);
        eventName = (EditText) findViewById(R.id.eventName);
        startTime = (TextView) findViewById(R.id.startTime);
        endTime = (TextView) findViewById(R.id.endTime);
        startTime.setText(event.getStartTime());
        endTime.setText(event.getEndTime());
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
                    Toast.makeText(EditEvent.this, "End time should be after start time.", Toast.LENGTH_LONG).show();
                }
            }
        };

        startDate = (TextView) findViewById(R.id.startDate);
        endDate = (TextView) findViewById(R.id.endDate);
        startDate.setText(event.getStartDate());
        endDate.setText(event.getEndDate());
        theDate.setText(date);

        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        backToEventsToday.setOnClickListener(this);
        save.setOnClickListener(this);
        delete.setOnClickListener(this);

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
                    Toast.makeText(EditEvent.this, "End date must be before start date.", Toast.LENGTH_LONG).show();
                }
                if(newEndDate == start_Date && start_Time > end_Time) {
                    end_Time = start_Time;
                    endTime.setText(getTime(end_Time/100, end_Time%100));
                }
            }
        };

    }

    public void onClick(View view) {
        if (view == backToEventsToday) {
            //will open login
            Intent intent = new Intent(EditEvent.this, EventsToday.class);
            intent.putExtra("date", date);
            startActivity(intent);
        }

        if (view == startDate) {
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
        if (view == endDate) {
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
        if (view == startTime) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    startTimeSetListener,
                    hour, minute, true);
            dialog.show();
        }
        if (view == endTime) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    endTimeSetListener,
                    hour, minute, true);
            dialog.show();
        }
        if (view == save) {
            Toast.makeText(EditEvent.this, "Saved", Toast.LENGTH_SHORT).show();
            removeEvent();
            mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child("Users").child(encodeUserEmail(email)).child("Groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> groups = dataSnapshot.getChildren().iterator();
                    while (groups.hasNext()) {
                        DataSnapshot group = groups.next();
                        groups_.add(group.getValue().toString());
                    }
                    addEventToDatabase();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditEvent.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }

            /*@Override
            public void onCancelled(FirebaseError arg0) {
                Toast.makeText(UserInfo.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
            }
            */
            });
        }
        if (view == delete) {
            removeEvent();
            Intent intent = new Intent(EditEvent.this, EventsToday.class);
            intent.putExtra("date", date);
            startActivity(intent);
        }
    }

    public void addEventToDatabase(){
        int numDays = getNumDays(start_Date, end_Date);
        numGroups = groups_.size();
        while(numGroups>0) {
            for (int i = 0; i < numDays; i++) {
                int key = getKey(start_Date, i);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String email = user.getEmail();
                eventDB = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(encodeUserEmail(email)).child("Events").child(Integer.toString(key));
                String eventId = event.getEventId();
                String event_name = eventName.getText().toString().trim();
                String start_time = startTime.getText().toString().trim();
                String end_time = endTime.getText().toString().trim();
                String start_date = startDate.getText().toString().trim();
                String end_date = endDate.getText().toString().trim();
                String remarks_ = remarks.getText().toString().trim();
                Event event2 = new Event(event_name, start_time, end_time, start_date, end_date, remarks_, eventId);
                eventDB.child(eventId).setValue(event2);
                mRef.child("Groups").child(groups_.get(numGroups - 1)).child("Events").child(Integer.toString(key)).child(eventId).setValue(event2);
                mRef.child("Groups").child(groups_.get(numGroups-1)).child("Events").child(Integer.toString(key)).child(eventId).child("Emails").child(encodeUserEmail(email));
            }
            numGroups--;
        }
        Intent intent = new Intent(EditEvent.this, EventsToday.class);
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

    private void removeEvent () {
        String [] info = event.getStartDate().split("/");
        int day = Integer.parseInt(info[0]);
        int month = Integer.parseInt(info [1]);
        int year = Integer.parseInt(info [2]);
        start_Date = year *10000 + month *100 + day;

        String [] info2 = event.getEndDate().split("/");
        day = Integer.parseInt(info2[0]);
        month = Integer.parseInt(info2 [1]);
        year = Integer.parseInt(info2 [2]);
        end_Date = year *10000 + month *100 + day;

        eventId = event.getEventId();
        email = firebaseAuth.getCurrentUser().getEmail();

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("Users").child(encodeUserEmail(email)).child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> groups = dataSnapshot.getChildren().iterator();
                while (groups.hasNext()) {
                    DataSnapshot group = groups.next();
                    groups_.add(group.getValue().toString());
                }
                removeEventFromDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditEvent.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

            /*@Override
            public void onCancelled(FirebaseError arg0) {
                Toast.makeText(UserInfo.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
            }
            */
        });
    }

    public void removeEventFromDatabase(){
        numGroups = groups_.size();
        final int numDays = getNumDays(start_Date, end_Date);
        mRef.child("Groups").child(groups_.get(numGroups - 1)).child("Events").child(Integer.toString(key))
                .child(eventId).child("Emails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                while(numGroups>0) {
                    for (int i = 0; i < numDays; i++) {
                        key = start_Date + i;
                        FirebaseDatabase.getInstance().getReference().child("Users").child(encodeUserEmail(email))
                                .child("Events").child(Integer.toString(key)).child(eventId).removeValue();
                        mRef.child("Groups").child(groups_.get(numGroups - 1)).child("Events").child(Integer.toString(key))
                                .child(eventId).child("Emails").child(encodeUserEmail(email)).removeValue();
                        if(snapshot.getValue()==null){
                            mRef.child("Groups").child(groups_.get(numGroups - 1)).child("Events").child(Integer.toString(key))
                                    .child(eventId).removeValue();
                        }
                    }
                    numGroups--;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditEvent.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private int getNumDays(int start, int end) {
        int day = start%100;
        int month = start/100%100;
        int year = start/10000;
        int day2 = end%100;
        int month2 = end/100%100;
        int year2 = end/10000;
        int days = 1;

        while(day != day2 || month!=month2 || year != year2) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, day);
            gregorianCalendar.add(GregorianCalendar.DAY_OF_MONTH, days);
            day = gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH);
            month = gregorianCalendar.get(GregorianCalendar.MONTH);
            year = gregorianCalendar.get(GregorianCalendar.YEAR);
            days++;
        }
        return days;
    }

    private int dateConvert(String date2) {
        String [] info = date2.split("/");
        int day = Integer.parseInt(info[0]);
        int month = Integer.parseInt(info [1]);
        int year = Integer.parseInt(info [2]);
        return year *10000 + month *100 + day;
    }

    private int timeConvert(String time) {
        String [] info = time.split(":");
        int hour = Integer.parseInt(info[0]);
        int minute = Integer.parseInt(info [1]);
        return hour*100 + minute;
    }

    public String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    public String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
}
