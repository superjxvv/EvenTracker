package com.example.admin.testingv1;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable{
    private ArrayList<Event> events = new ArrayList <Event>();
    private ArrayList<Group> groups = new ArrayList<Group>();
    private String userId;

    public User(String userId) {
        this.userId = userId;
    }

    public User() {
    }

    protected User(Parcel in) {
        events = in.createTypedArrayList(Event.CREATOR);
        groups = in.createTypedArrayList(Group.CREATOR);
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
        parcel.writeTypedList(groups);
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void addGroup (Group group) {
        groups.add(group);
    }

    public void removeGroup (Group group) {
        groups.remove(group);
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }
}
