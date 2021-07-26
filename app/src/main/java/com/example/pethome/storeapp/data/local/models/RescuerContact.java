

package com.example.pethome.storeapp.data.local.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringDef;

import com.example.pethome.storeapp.data.local.contracts.RescuerContract;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RescuerContact implements Parcelable, Cloneable {

    public static final Creator<RescuerContact> CREATOR = new Creator<RescuerContact>() {
        @Override
        public RescuerContact createFromParcel(Parcel in) {
            return new RescuerContact(in);
        }

        @Override
        public RescuerContact[] newArray(int size) {
            return new RescuerContact[size];
        }
    };

    private final String mType;

    private String mValue;

    private boolean mIsDefault;

    private RescuerContact(@RescuerContactTypeDef String type, String value, boolean isDefault) {
        mType = type;
        mValue = value;
        mIsDefault = isDefault;
    }

    protected RescuerContact(Parcel in) {
        mType = in.readString();
        mValue = in.readString();
        mIsDefault = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
        dest.writeString(mValue);
        dest.writeByte((byte) (mIsDefault ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getType() {
        return mType;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public boolean isDefault() {
        return mIsDefault;
    }

    public void setDefault(boolean isDefault) {
        mIsDefault = isDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RescuerContact that = (RescuerContact) o;

        if (mIsDefault != that.mIsDefault) return false;
        if (!mType.equals(that.mType)) return false;
        return mValue != null ? mValue.equals(that.mValue) : that.mValue == null;
    }

    @Override
    public int hashCode() {
        int result = mType.hashCode();
        result = 31 * result + (mValue != null ? mValue.hashCode() : 0);
        result = 31 * result + (mIsDefault ? 1 : 0);
        return result;
    }

    @Override
    public final Object clone() {

        return new Builder()
                .setType(this.mType)
                .setIsDefault(this.mIsDefault)
                .setValue(this.mValue)
                .createRescuerContact();
    }

    @Override
    public String toString() {
        return "RescuerContact{" +
                "mType='" + mType + '\'' +
                ", mValue='" + mValue + '\'' +
                ", mIsDefault=" + mIsDefault +
                '}';
    }



    @StringDef({RescuerContract.RescuerContactType.CONTACT_TYPE_PHONE,
            RescuerContract.RescuerContactType.CONTACT_TYPE_EMAIL})

    @Retention(RetentionPolicy.SOURCE)
    public @interface RescuerContactTypeDef {
    }

    public static class Builder {

        private String mType;
        private String mValue;
        private boolean mIsDefault = false;

        public Builder setType(@RescuerContactTypeDef String type) {
            mType = type;
            return this;
        }

        public Builder setValue(String value) {
            mValue = value;
            return this;
        }

        public Builder setIsDefault(boolean isDefault) {
            mIsDefault = isDefault;
            return this;
        }

        public RescuerContact createRescuerContact() {
            return new RescuerContact(mType, mValue, mIsDefault);
        }
    }
}
