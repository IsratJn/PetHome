

package com.example.pethome.storeapp.data.local.models;

import android.os.Parcel;
import android.os.Parcelable;

public class AnimalImage implements Parcelable, Cloneable {

    public static final Creator<AnimalImage> CREATOR = new Creator<AnimalImage>() {
        @Override
        public AnimalImage createFromParcel(Parcel in) {
            return new AnimalImage(in);
        }

        @Override
        public AnimalImage[] newArray(int size) {
            return new AnimalImage[size];
        }
    };

    private final String mImageUri;

    private boolean mIsDefault;

    private AnimalImage(String imageUri, boolean isDefault) {
        mImageUri = imageUri;
        mIsDefault = isDefault;
    }

    protected AnimalImage(Parcel in) {
        mImageUri = in.readString();
        mIsDefault = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImageUri);
        dest.writeByte((byte) (mIsDefault ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getImageUri() {
        return mImageUri;
    }

    public boolean isDefault() {
        return mIsDefault;
    }

    public void setDefault(boolean isDefault) {
        mIsDefault = isDefault;
    }

    @Override
    public final Object clone() {

        return new Builder()
                .setImageUri(this.mImageUri)
                .setIsDefault(this.mIsDefault)
                .createAnimalImage();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnimalImage that = (AnimalImage) o;

        if (mIsDefault != that.mIsDefault) return false;
        return mImageUri.equals(that.mImageUri);
    }

    @Override
    public int hashCode() {
        int result = mImageUri.hashCode();
        result = 31 * result + (mIsDefault ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AnimalImage{" +
                "mImageUri='" + mImageUri + '\'' +
                ", mIsDefault=" + mIsDefault +
                '}';
    }

    public static class Builder {

        private String mImageUri;
        private boolean mIsDefault = false;

        public Builder setImageUri(String imageUri) {
            mImageUri = imageUri;
            return this;
        }

        public Builder setIsDefault(boolean isDefault) {
            mIsDefault = isDefault;
            return this;
        }

        public AnimalImage createAnimalImage() {
            return new AnimalImage(mImageUri, mIsDefault);
        }
    }
}
