

package com.example.pethome.storeapp.data.local.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.pethome.storeapp.data.local.utils.QueryArgsUtility;


public class AdoptionsLite implements Parcelable {

    public static final Creator<AdoptionsLite> CREATOR = new Creator<AdoptionsLite>() {
        @Override
        public AdoptionsLite createFromParcel(Parcel in) {
            return new AdoptionsLite(in);
        }

        @Override
        public AdoptionsLite[] newArray(int size) {
            return new AdoptionsLite[size];
        }
    };

    private final int mAnimalId;

    private final int mRescuerId;

    private final String mAnimalName;

    private final String mAnimalSku;

    private final String mCategoryName;

    private final String mDefaultImageUri;

    private final String mTopRescuerName;

    private final String mTopRescuerCode;

    private final float mRescuerUnitPrice;

    private final int mRescuerAvailableQuantity;

    private final int mTotalAvailableQuantity;

    private AdoptionsLite(int animalId, int rescuerId, String animalName, String animalSku,
                      String categoryName, String defaultImageUri, String topRescuerName,
                      String topRescuerCode, float rescuerUnitPrice,
                      int rescuerAvailableQuantity, int totalAvailableQuantity) {
        mAnimalId = animalId;
        mRescuerId = rescuerId;
        mAnimalName = animalName;
        mAnimalSku = animalSku;
        mCategoryName = categoryName;
        mDefaultImageUri = defaultImageUri;
        mTopRescuerName = topRescuerName;
        mTopRescuerCode = topRescuerCode;
        mRescuerUnitPrice = rescuerUnitPrice;
        mRescuerAvailableQuantity = rescuerAvailableQuantity;
        mTotalAvailableQuantity = totalAvailableQuantity;
    }

    protected AdoptionsLite(Parcel in) {
        mAnimalId = in.readInt();
        mRescuerId = in.readInt();
        mAnimalName = in.readString();
        mAnimalSku = in.readString();
        mCategoryName = in.readString();
        mDefaultImageUri = in.readString();
        mTopRescuerName = in.readString();
        mTopRescuerCode = in.readString();
        mRescuerUnitPrice = in.readFloat();
        mRescuerAvailableQuantity = in.readInt();
        mTotalAvailableQuantity = in.readInt();
    }

    public static AdoptionsLite from(Cursor cursor) {
        return new AdoptionsLite(
                cursor.getInt(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_ITEM_ID_INDEX),
                cursor.getInt(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_RESCUER_ID_INDEX),
                cursor.getString(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_ITEM_NAME_INDEX),
                cursor.getString(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_ITEM_SKU_INDEX),
                cursor.getString(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_ITEM_CATEGORY_NAME_INDEX),
                cursor.getString(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_ITEM_IMAGE_URI_INDEX),
                cursor.getString(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_RESCUER_NAME_INDEX),
                cursor.getString(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_RESCUER_CODE_INDEX),
                cursor.getFloat(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_ITEM_UNIT_PRICE_INDEX),
                cursor.getInt(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_RESCUER_AVAIL_QUANTITY_INDEX),
                cursor.getInt(QueryArgsUtility.AdoptionsShortInfoQuery.COLUMN_TOTAL_AVAIL_QUANTITY_INDEX)
        );
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mAnimalId);
        dest.writeInt(mRescuerId);
        dest.writeString(mAnimalName);
        dest.writeString(mAnimalSku);
        dest.writeString(mCategoryName);
        dest.writeString(mDefaultImageUri);
        dest.writeString(mTopRescuerName);
        dest.writeString(mTopRescuerCode);
        dest.writeFloat(mRescuerUnitPrice);
        dest.writeInt(mRescuerAvailableQuantity);
        dest.writeInt(mTotalAvailableQuantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getAnimalId() {
        return mAnimalId;
    }

    public int getRescuerId() {
        return mRescuerId;
    }

    public String getAnimalName() {
        return mAnimalName;
    }

    public String getAnimalSku() {
        return mAnimalSku;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getDefaultImageUri() {
        return mDefaultImageUri;
    }

    public String getTopRescuerName() {
        return mTopRescuerName;
    }

    public String getTopRescuerCode() {
        return mTopRescuerCode;
    }

    public float getRescuerUnitPrice() {
        return mRescuerUnitPrice;
    }

    public int getRescuerAvailableQuantity() {
        return mRescuerAvailableQuantity;
    }

    public int getTotalAvailableQuantity() {
        return mTotalAvailableQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdoptionsLite adoptionsLite = (AdoptionsLite) o;

        if (mAnimalId != adoptionsLite.mAnimalId) return false;
        if (mRescuerId != adoptionsLite.mRescuerId) return false;
        if (Float.compare(adoptionsLite.mRescuerUnitPrice, mRescuerUnitPrice) != 0) return false;
        if (mRescuerAvailableQuantity != adoptionsLite.mRescuerAvailableQuantity) return false;
        if (mTotalAvailableQuantity != adoptionsLite.mTotalAvailableQuantity) return false;
        if (!mAnimalName.equals(adoptionsLite.mAnimalName)) return false;
        if (!mAnimalSku.equals(adoptionsLite.mAnimalSku)) return false;
        if (!mCategoryName.equals(adoptionsLite.mCategoryName)) return false;
        if (mDefaultImageUri != null ? !mDefaultImageUri.equals(adoptionsLite.mDefaultImageUri) : adoptionsLite.mDefaultImageUri != null)
            return false;
        if (!mTopRescuerName.equals(adoptionsLite.mTopRescuerName)) return false;
        return mTopRescuerCode.equals(adoptionsLite.mTopRescuerCode);
    }

    @Override
    public int hashCode() {
        int result = mAnimalId;
        result = 31 * result + mRescuerId;
        result = 31 * result + mAnimalName.hashCode();
        result = 31 * result + mAnimalSku.hashCode();
        result = 31 * result + mCategoryName.hashCode();
        result = 31 * result + (mDefaultImageUri != null ? mDefaultImageUri.hashCode() : 0);
        result = 31 * result + mTopRescuerName.hashCode();
        result = 31 * result + mTopRescuerCode.hashCode();
        result = 31 * result + (mRescuerUnitPrice != +0.0f ? Float.floatToIntBits(mRescuerUnitPrice) : 0);
        result = 31 * result + mRescuerAvailableQuantity;
        result = 31 * result + mTotalAvailableQuantity;
        return result;
    }
}
