

package com.example.pethome.storeapp.data.local.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract;


public class AnimalRescuerInfo implements Parcelable, Cloneable {

    public static final Creator<AnimalRescuerInfo> CREATOR = new Creator<AnimalRescuerInfo>() {
        @Override
        public AnimalRescuerInfo createFromParcel(Parcel in) {
            return new AnimalRescuerInfo(in);
        }

        @Override
        public AnimalRescuerInfo[] newArray(int size) {
            return new AnimalRescuerInfo[size];
        }
    };

    private final int mItemId;

    private final int mRescuerId;

    private float mUnitPrice;

    private AnimalRescuerInfo(final int itemId, final int rescuerId, float unitPrice) {
        mItemId = itemId;
        mRescuerId = rescuerId;
        mUnitPrice = unitPrice;
    }

    protected AnimalRescuerInfo(Parcel in) {
        mItemId = in.readInt();
        mRescuerId = in.readInt();
        mUnitPrice = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mItemId);
        dest.writeInt(mRescuerId);
        dest.writeFloat(mUnitPrice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getItemId() {
        return mItemId;
    }

    public int getRescuerId() {
        return mRescuerId;
    }

    public float getUnitPrice() {
        return mUnitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        mUnitPrice = unitPrice;
    }

    @Override
    public final Object clone() {

        return new Builder()
                .setItemId(this.mItemId)
                .setRescuerId(this.mRescuerId)
                .setUnitPrice(this.mUnitPrice)
                .createAnimalRescuerInfo();
    }

    @Override
    public String toString() {
        return "AnimalRescuerInfo{" +
                "mItemId=" + mItemId +
                ", mRescuerId=" + mRescuerId +
                ", mUnitPrice=" + mUnitPrice +
                '}';
    }

    public static class Builder {

        private int mItemId;
        private int mRescuerId;
        private float mUnitPrice = AdoptionsContract.AnimalRescuerInfo.DEFAULT_ITEM_UNIT_PRICE;

        public Builder setItemId(int itemId) {
            mItemId = itemId;
            return this;
        }

        public Builder setRescuerId(int rescuerId) {
            mRescuerId = rescuerId;
            return this;
        }

        public Builder setUnitPrice(float unitPrice) {
            mUnitPrice = unitPrice;
            return this;
        }

        public AnimalRescuerInfo createAnimalRescuerInfo() {
            return new AnimalRescuerInfo(mItemId, mRescuerId, mUnitPrice);
        }
    }
}
