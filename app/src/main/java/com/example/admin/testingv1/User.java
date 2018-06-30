package com.example.admin.testingv1;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable{
    private ArrayList<Event> events = new ArrayList <Event>();
    private String userId;

    public User(String userId) {
        this.userId = userId;
    }

    public User() {
    }

    protected User(Parcel in) {
        events = in.createTypedArrayList(Event.CREATOR);
        userId = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public ArrayList<Event> getEvents() {
        return events;
    }

    public String getUserId() {
        return userId;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }

    public void editEvent (Event event, Event editedEvent) {
        events.remove(event);
        events.add(editedEvent);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(events);
        parcel.writeString(userId);
    }
}
