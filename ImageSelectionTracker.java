package com.example.pethome.storeapp.ui.animals.image;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
public class ImageSelectionTracker implements Parcelable, Cloneable {
    public static final Creator<ImageSelectionTracker> CREATOR = new Creator<ImageSelectionTracker>() {
        @Override
        public ImageSelectionTracker createFromParcel(Parcel in) {
            return new ImageSelectionTracker(in);
        }
        @Override
        public ImageSelectionTracker[] newArray(int size) {
            return new ImageSelectionTracker[size];
        }
    };
    private final int mPosition;
    @NonNull
    @AnimalImageContract.PhotoGridSelectModeDef
    private final String mPhotoGridMode;
    private final String mImageContentUri;
    private boolean mSelected;
    private ImageSelectionTracker(int position,
                                  @NonNull @AnimalImageContract.PhotoGridSelectModeDef String photoGridMode,
                                  String imageContentUri, boolean selected) {
        mPosition = position;
        mPhotoGridMode = photoGridMode;
        mImageContentUri = imageContentUri;
        mSelected = selected;
    }
    protected ImageSelectionTracker(Parcel in) {
        mPosition = in.readInt();
        mPhotoGridMode = in.readString();
        mImageContentUri = in.readString();
        mSelected = in.readByte() != 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPosition);
        dest.writeString(mPhotoGridMode);
        dest.writeString(mImageContentUri);
        dest.writeByte((byte) (mSelected ? 1 : 0));
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public boolean isSelected() {
        return mSelected;
    }
    public void setSelected(boolean selected) {
        mSelected = selected;
    }
    public int getPosition() {
        return mPosition;
    }
    @NonNull
    public String getPhotoGridMode() {
        return mPhotoGridMode;
    }
    public String getImageContentUri() {
        return mImageContentUri;
    }
    @Override
    public String toString() {
        return "ImageSelectionTracker{" +
                "mPosition=" + mPosition +
                ", mPhotoGridMode='" + mPhotoGridMode + '\'' +
                ", mSelected=" + mSelected +
                '}';
    }
    @Override
    protected final Object clone() {
        return new Builder()
                .setSelected(this.mSelected)
                .setPhotoGridMode(this.mPhotoGridMode)
                .setImageContentUri(this.mImageContentUri)
                .setPosition(this.mPosition)
                .createTracker();
    }
    public static class Builder {
        private int mPosition;
        private String mPhotoGridMode;
        private String mImageContentUri;
        private boolean mSelected = false;
        public Builder setPosition(int position) {
            mPosition = position;
            return this;
        }
        public Builder setPhotoGridMode(String photoGridMode) {
            mPhotoGridMode = photoGridMode;
            return this;
        }
        public Builder setImageContentUri(String imageContentUri) {
            mImageContentUri = imageContentUri;
            return this;
        }
        public Builder setSelected(boolean selected) {
            mSelected = selected;
            return this;
        }
        public ImageSelectionTracker createTracker() {
            return new ImageSelectionTracker(mPosition, mPhotoGridMode, mImageContentUri, mSelected);
        }
    }
}
