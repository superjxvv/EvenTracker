package com.example.admin.testingv1;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Group implements Parcelable{
    private ArrayList<String> members = new ArrayList<String>();
    private String groupName;
    private int size = 0;
    private String groupID;
    private String leader;

    public Group(ArrayList<String> members, String groupName, String groupID, String leader) {
        this.members = members;
        this.groupName = groupName;
        this.groupID = groupID;
        this.size = members.size();
        this.leader = leader;
    }

    public Group() {
    }

    protected Group(Parcel in) {
        members = in.createStringArrayList();
        groupName = in.readString();
        size = in.readInt();
        groupID = in.readString();
        leader = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public ArrayList<String> getMembers() {
        return members;
    }

    public void addMember (String member) {
        members.add(member);
        size++;
    }

    public void removeMember (String member) {
        members.remove(member);
        if(size >0){
            size--;
        }
    }

    public int getSize (){
        return size;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getLeader (){
        return leader;
    }

    public void setLeader(String leader2) {
        leader = leader2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(members);
        parcel.writeString(groupName);
        parcel.writeInt(size);
        parcel.writeString(groupID);
        parcel.writeString(leader);
    }
}
