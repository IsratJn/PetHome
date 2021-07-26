

package com.example.pethome.storeapp.data.local.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.pethome.storeapp.data.local.utils.QueryArgsUtility;


public class AnimalLite implements Parcelable {

    public static final Creator<AnimalLite> CREATOR = new Creator<AnimalLite>() {
        @Override
        public AnimalLite createFromParcel(Parcel in) {
            return new AnimalLite(in);
        }

        @Override
        public AnimalLite[] newArray(int size) {
            return new AnimalLite[size];
        }
    };

    private final int mId;

    private final String mName;

    private final String mSku;

    private final String mCategory;

    private final String mDefaultImageUri;

    private AnimalLite(int id, String name, String sku, String category, String defaultImageUri) {
        mId = id;
        mName = name;
        mSku = sku;
        mCategory = category;
        mDefaultImageUri = defaultImageUri;
    }

    protected AnimalLite(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mSku = in.readString();
        mCategory = in.readString();
        mDefaultImageUri = in.readString();
    }

    @NonNull
    public static AnimalLite from(Cursor cursor) {
        return new AnimalLite(
                cursor.getInt(QueryArgsUtility.ItemsShortInfoQuery.COLUMN_ITEM_ID_INDEX),
                cursor.getString(QueryArgsUtility.ItemsShortInfoQuery.COLUMN_ITEM_NAME_INDEX),
                cursor.getString(QueryArgsUtility.ItemsShortInfoQuery.COLUMN_ITEM_SKU_INDEX),
                cursor.getString(QueryArgsUtility.ItemsShortInfoQuery.COLUMN_ITEM_CATEGORY_NAME_INDEX),
                cursor.getString(QueryArgsUtility.ItemsShortInfoQuery.COLUMN_ITEM_IMAGE_URI_INDEX)
        );
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mSku);
        dest.writeString(mCategory);
        dest.writeString(mDefaultImageUri);
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

    public String getSku() {
        return mSku;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getDefaultImageUri() {
        return mDefaultImageUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnimalLite that = (AnimalLite) o;

        if (mId != that.mId) return false;
        if (!mName.equals(that.mName)) return false;
        if (!mSku.equals(that.mSku)) return false;
        if (!mCategory.equals(that.mCategory)) return false;

        return mDefaultImageUri != null ? mDefaultImageUri.equals(that.mDefaultImageUri) : that.mDefaultImageUri == null;
    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + mName.hashCode();
        result = 31 * result + mSku.hashCode();
        result = 31 * result + mCategory.hashCode();
        result = 31 * result + (mDefaultImageUri != null ? mDefaultImageUri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AnimalLite{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mSku='" + mSku + '\'' +
                ", mCategory='" + mCategory + '\'' +
                ", mDefaultImageUri='" + mDefaultImageUri + '\'' +
                '}';
    }
}