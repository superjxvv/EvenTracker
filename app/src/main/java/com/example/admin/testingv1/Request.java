package com.example.admin.testingv1;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable{
    String requester;
    String requestType;
    String requestID;
    String groupName;
    String eventName;
    String startTime;
    String endTime;
    String startDate;
    String endDate;
    String remarks;
    String eventId;

    protected Request(Parcel in) {
        requester = in.readString();
        requestType = in.readString();
        requestID = in.readString();
        groupName = in.readString();
        eventName = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        remarks = in.readString();
        eventId = in.readString();
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    public String getRemarks() {
        return remarks;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
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

    public Request(String userId, String requestType, String ID) {
        this.requester = userId;
        this.requestType = requestType;
        requestID = ID;
    }

    public Request (String userID, String groupName, String eventName, String startTime, String endTime,
                    String startDate, String endDate, String ID, String remarks_, String eventId){
        this.requester = userID;
        requestID = ID;
        this.groupName = groupName;
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remarks = remarks_;
        this.eventId = eventId;
    }

    public Request() {
    }

    public String getRequester() {
        return requester;
    }

    public String getRequestType (){
        return requestType;
    }

    public String getRequestID (){return requestID;}

    public String getGroupName () {return groupName;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requester);
        dest.writeString(requestType);
        dest.writeString(requestID);
        dest.writeString(groupName);
        dest.writeString(eventName);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(remarks);
        dest.writeString(eventId);
    }
}
