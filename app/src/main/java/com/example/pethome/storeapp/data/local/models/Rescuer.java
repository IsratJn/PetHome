

package com.example.pethome.storeapp.data.local.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Rescuer implements Parcelable {

    public static final Creator<Rescuer> CREATOR = new Creator<Rescuer>() {
        @Override
        public Rescuer createFromParcel(Parcel in) {
            return new Rescuer(in);
        }

        @Override
        public Rescuer[] newArray(int size) {
            return new Rescuer[size];
        }
    };

    private final int mId;

    private String mName;

    private String mCode;

    private ArrayList<RescuerContact> mContacts;

    private ArrayList<AnimalRescuerInfo> mAnimalRescuerInfoList;

    private Rescuer(int id,
                     String name,
                     String code,
                     ArrayList<RescuerContact> contacts,
                     ArrayList<AnimalRescuerInfo> animalRescuerInfoList) {
        mId = id;
        mName = name;
        mCode = code;
        mContacts = contacts;
        mAnimalRescuerInfoList = animalRescuerInfoList;
    }

    protected Rescuer(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mCode = in.readString();
        mContacts = in.createTypedArrayList(RescuerContact.CREATOR);
        mAnimalRescuerInfoList = in.createTypedArrayList(AnimalRescuerInfo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mCode);
        dest.writeTypedList(mContacts);
        dest.writeTypedList(mAnimalRescuerInfoList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public ArrayList<RescuerContact> getContacts() {
        return mContacts;
    }

    public void setContacts(ArrayList<RescuerContact> contacts) {
        mContacts = contacts;
    }

    public ArrayList<AnimalRescuerInfo> getAnimalRescuerInfoList() {
        return mAnimalRescuerInfoList;
    }

    public void setAnimalRescuerInfoList(ArrayList<AnimalRescuerInfo> animalRescuerInfoList) {
        mAnimalRescuerInfoList = animalRescuerInfoList;
    }

    public static class Builder {

        private int mId;
        private String mName;
        private String mCode;
        private ArrayList<RescuerContact> mContacts;
        private ArrayList<AnimalRescuerInfo> mAnimalRescuerInfoList;

        public Builder setId(int id) {
            mId = id;
            return this;
        }

        public Builder setName(String name) {
            mName = name;
            return this;
        }

        public Builder setCode(String code) {
            mCode = code;
            return this;
        }

        public Builder setContacts(ArrayList<RescuerContact> contacts) {
            mContacts = contacts;
            return this;
        }

        public Builder setAnimalRescuerInfoList(ArrayList<AnimalRescuerInfo> animalRescuerInfoList) {
            mAnimalRescuerInfoList = animalRescuerInfoList;
            return this;
        }

        public Rescuer createRescuer() {

            return new Rescuer(mId, mName, mCode, mContacts, mAnimalRescuerInfoList);
        }
    }
}
