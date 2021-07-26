
package com.example.pethome.storeapp.data.local.models;

import android.os.Parcel;
import android.os.Parcelable;


public class AnimalAttribute implements Parcelable {

    public static final Creator<AnimalAttribute> CREATOR = new Creator<AnimalAttribute>() {
        @Override
        public AnimalAttribute createFromParcel(Parcel in) {
            return new AnimalAttribute(in);
        }

        @Override
        public AnimalAttribute[] newArray(int size) {
            return new AnimalAttribute[size];
        }
    };

    private String mAttributeName;

    private String mAttributeValue;

    private AnimalAttribute(String attributeName, String attributeValue) {
        mAttributeName = attributeName;
        mAttributeValue = attributeValue;
    }

    protected AnimalAttribute(Parcel in) {
        mAttributeName = in.readString();
        mAttributeValue = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAttributeName);
        dest.writeString(mAttributeValue);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAttributeName() {
        return mAttributeName;
    }

    public void setAttributeName(String attributeName) {
        mAttributeName = attributeName;
    }

    public String getAttributeValue() {
        return mAttributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        mAttributeValue = attributeValue;
    }

    public static class Builder {

        private String mAttributeName;
        private String mAttributeValue;

        public Builder setAttributeName(String attributeName) {
            mAttributeName = attributeName;
            return this;
        }

        public Builder setAttributeValue(String attributeValue) {
            mAttributeValue = attributeValue;
            return this;
        }

        public AnimalAttribute createAnimalAttribute() {
            return new AnimalAttribute(mAttributeName, mAttributeValue);
        }
    }
}
