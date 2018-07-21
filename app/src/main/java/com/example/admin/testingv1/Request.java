package com.example.admin.testingv1;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable{
    String requester;
    String requestType;
    String requestID;

    public Request(String userId, String requestType, String ID) {
        this.requester = userId;
        this.requestType = requestType;
        requestID = ID;
    }

    public Request() {
    }

    protected Request(Parcel in) {
        requester = in.readString();
        requestType = in.readString();
        requestID = in.readString();
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

    public String getRequester() {
        return requester;
    }

    public String getRequestType (){
        return requestType;
    }

    public String getRequestID (){return requestID;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(requester);
        dest.writeString(requestType);
        dest.writeString(requestID);
    }
}
