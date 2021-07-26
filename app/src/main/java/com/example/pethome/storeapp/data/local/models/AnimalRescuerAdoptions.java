

package com.example.pethome.storeapp.data.local.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract;


public class AnimalRescuerAdoptions implements Parcelable, Cloneable {

    public static final Creator<AnimalRescuerAdoptions> CREATOR = new Creator<AnimalRescuerAdoptions>() {
        @Override
        public AnimalRescuerAdoptions createFromParcel(Parcel in) {
            return new AnimalRescuerAdoptions(in);
        }

        @Override
        public AnimalRescuerAdoptions[] newArray(int size) {
            return new AnimalRescuerAdoptions[size];
        }
    };

    private final int mItemId;

    private final int mRescuerId;

    private final String mRescuerName;

    private final String mRescuerCode;

    private final float mUnitPrice;

    private int mAvailableQuantity;

    private AnimalRescuerAdoptions(int itemId, int rescuerId, String rescuerName, String rescuerCode, float unitPrice, int availableQuantity) {
        mItemId = itemId;
        mRescuerId = rescuerId;
        mRescuerName = rescuerName;
        mRescuerCode = rescuerCode;
        mUnitPrice = unitPrice;
        mAvailableQuantity = availableQuantity;
    }

    protected AnimalRescuerAdoptions(Parcel in) {
        mItemId = in.readInt();
        mRescuerId = in.readInt();
        mRescuerName = in.readString();
        mRescuerCode = in.readString();
        mUnitPrice = in.readFloat();
        mAvailableQuantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mItemId);
        dest.writeInt(mRescuerId);
        dest.writeString(mRescuerName);
        dest.writeString(mRescuerCode);
        dest.writeFloat(mUnitPrice);
        dest.writeInt(mAvailableQuantity);
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

    public String getRescuerName() {
        return mRescuerName;
    }

    public String getRescuerCode() {
        return mRescuerCode;
    }

    public float getUnitPrice() {
        return mUnitPrice;
    }

    public int getAvailableQuantity() {
        return mAvailableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        mAvailableQuantity = availableQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnimalRescuerAdoptions that = (AnimalRescuerAdoptions) o;

        if (mItemId != that.mItemId) return false;
        if (mRescuerId != that.mRescuerId) return false;
        if (Float.compare(that.mUnitPrice, mUnitPrice) != 0) return false;
        if (mAvailableQuantity != that.mAvailableQuantity) return false;
        if (!mRescuerName.equals(that.mRescuerName)) return false;
        return mRescuerCode.equals(that.mRescuerCode);
    }

    @Override
    public int hashCode() {
        int result = mItemId;
        result = 31 * result + mRescuerId;
        result = 31 * result + mRescuerName.hashCode();
        result = 31 * result + mRescuerCode.hashCode();
        result = 31 * result + (mUnitPrice != +0.0f ? Float.floatToIntBits(mUnitPrice) : 0);
        result = 31 * result + mAvailableQuantity;
        return result;
    }

    @Override
    public final Object clone() {

        return new Builder()
                .setItemId(this.mItemId)
                .setRescuerId(this.mRescuerId)
                .setRescuerName(this.mRescuerName)
                .setRescuerCode(this.mRescuerCode)
                .setUnitPrice(this.mUnitPrice)
                .setAvailableQuantity(this.mAvailableQuantity)
                .createAnimalRescuerAdoptions();
    }

    @Override
    public String toString() {
        return "AnimalRescuerAdoptions{" +
                "mItemId=" + mItemId +
                ", mRescuerId=" + mRescuerId +
                ", mRescuerName='" + mRescuerName + '\'' +
                ", mRescuerCode='" + mRescuerCode + '\'' +
                ", mUnitPrice=" + mUnitPrice +
                ", mAvailableQuantity=" + mAvailableQuantity +
                '}';
    }

    public static class Builder {

        private int mItemId;
        private int mRescuerId;
        private String mRescuerName;
        private String mRescuerCode;
        private float mUnitPrice = AdoptionsContract.AnimalRescuerInfo.DEFAULT_ITEM_UNIT_PRICE;
        private int mAvailableQuantity = AdoptionsContract.AnimalRescuerInventory.DEFAULT_ITEM_AVAIL_QUANTITY;

        public Builder setItemId(int itemId) {
            mItemId = itemId;
            return this;
        }

        public Builder setRescuerId(int rescuerId) {
            mRescuerId = rescuerId;
            return this;
        }

        public Builder setRescuerName(String rescuerName) {
            mRescuerName = rescuerName;
            return this;
        }

        public Builder setRescuerCode(String rescuerCode) {
            mRescuerCode = rescuerCode;
            return this;
        }

        public Builder setUnitPrice(float unitPrice) {
            mUnitPrice = unitPrice;
            return this;
        }

        public Builder setAvailableQuantity(int availableQuantity) {
            mAvailableQuantity = availableQuantity;
            return this;
        }

        public AnimalRescuerAdoptions createAnimalRescuerAdoptions() {
            return new AnimalRescuerAdoptions(mItemId, mRescuerId, mRescuerName, mRescuerCode, mUnitPrice, mAvailableQuantity);
        }
    }
}
