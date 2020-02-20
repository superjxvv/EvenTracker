package com.example.admin.eventracker;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String title = "My Event";
    private String startTime;
    private String endTime;
    private String startDate;
    private String endDate;
    private String remarks = "";
    private String eventId;
    private String user = "";
    private Boolean isPrivate = false;

    public Event(String title, String startTime, String endTime, String startDate, String endDate, String remarks, String eventId, String user, Boolean isPrivate) {
        if(!title.equals("")){
            this.title = title;
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        if(!remarks.equals("")){
            this.remarks = remarks;
        }
        this.eventId = eventId;
        this.user = user;
        this.isPrivate = isPrivate;
    }

    public Event(String title, String startTime, String endTime, String startDate, String endDate, String remarks, String eventId) {
        if(!title.equals("")){
            this.title = title;
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        if(!remarks.equals("")){
            this.remarks = remarks;
        }
        this.eventId = eventId;
    }

    public Event() {

    }


    protected Event(Parcel in) {
        title = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        remarks = in.readString();
        eventId = in.readString();
        user = in.readString();
        byte tmpIsPrivate = in.readByte();
        isPrivate = tmpIsPrivate == 0 ? null : tmpIsPrivate == 1;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getEventId() {
        return eventId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(remarks);
        dest.writeString(eventId);
        dest.writeString(user);
        dest.writeByte((byte) (isPrivate == null ? 0 : isPrivate ? 1 : 2));
    }
}
