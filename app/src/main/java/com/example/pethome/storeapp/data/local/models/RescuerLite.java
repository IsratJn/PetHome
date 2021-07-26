

package com.example.pethome.storeapp.data.local.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.pethome.storeapp.data.local.utils.QueryArgsUtility;

public class RescuerLite implements Parcelable {

    public static final Creator<RescuerLite> CREATOR = new Creator<RescuerLite>() {
        @Override
        public RescuerLite createFromParcel(Parcel in) {
            return new RescuerLite(in);
        }

        @Override
        public RescuerLite[] newArray(int size) {
            return new RescuerLite[size];
        }
    };

    private final int mId;

    @NonNull
    private final String mName;

    @NonNull
    private final String mCode;

    private final String mDefaultPhone;

    private final String mDefaultEmail;

    private final int mItemCount;

    private RescuerLite(int id, @NonNull String name, @NonNull String code, String defaultPhone, String defaultEmail, int itemCount) {
        mId = id;
        mName = name;
        mCode = code;
        mDefaultPhone = defaultPhone;
        mDefaultEmail = defaultEmail;
        mItemCount = itemCount;
    }

    protected RescuerLite(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mCode = in.readString();
        mDefaultPhone = in.readString();
        mDefaultEmail = in.readString();
        mItemCount = in.readInt();
    }

    public static RescuerLite from(Cursor cursor) {
        return new RescuerLite(
                cursor.getInt(QueryArgsUtility.RescuersShortInfoQuery.COLUMN_RESCUER_ID_INDEX),
                cursor.getString(QueryArgsUtility.RescuersShortInfoQuery.COLUMN_RESCUER_NAME_INDEX),
                cursor.getString(QueryArgsUtility.RescuersShortInfoQuery.COLUMN_RESCUER_CODE_INDEX),
                cursor.getString(QueryArgsUtility.RescuersShortInfoQuery.COLUMN_RESCUER_DEFAULT_PHONE_INDEX),
                cursor.getString(QueryArgsUtility.RescuersShortInfoQuery.COLUMN_RESCUER_DEFAULT_EMAIL_INDEX),
                cursor.getInt(QueryArgsUtility.RescuersShortInfoQuery.COLUMN_RESCUER_ITEM_COUNT_INDEX)
        );
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mCode);
        dest.writeString(mDefaultPhone);
        dest.writeString(mDefaultEmail);
        dest.writeInt(mItemCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getCode() {
        return mCode;
    }

    public String getDefaultPhone() {
        return mDefaultPhone;
    }

    public String getDefaultEmail() {
        return mDefaultEmail;
    }

    public int getItemCount() {
        return mItemCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RescuerLite that = (RescuerLite) o;

        if (mId != that.mId) return false;
        if (mItemCount != that.mItemCount) return false;
        if (!mName.equals(that.mName)) return false;
        if (!mCode.equals(that.mCode)) return false;
        if (mDefaultPhone != null ? !mDefaultPhone.equals(that.mDefaultPhone) : that.mDefaultPhone != null)
            return false;
        return mDefaultEmail != null ? mDefaultEmail.equals(that.mDefaultEmail) : that.mDefaultEmail == null;
    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + mName.hashCode();
        result = 31 * result + mCode.hashCode();
        result = 31 * result + (mDefaultPhone != null ? mDefaultPhone.hashCode() : 0);
        result = 31 * result + (mDefaultEmail != null ? mDefaultEmail.hashCode() : 0);
        result = 31 * result + mItemCount;
        return result;
    }
}
