

package com.example.pethome.storeapp.data.local.contracts;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;


public class RescuerContract implements StoreContract {

    public static final String PATH_RESCUER = "rescuer";

    public static final String PATH_CONTACT_TYPE = "contacttype";

    public static final String PATH_RESCUER_CONTACT = "contact";


    private RescuerContract() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }


    public static final class Rescuer implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RESCUER);


        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_RESCUER;


        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_RESCUER;


        public static final String PATH_SHORT_INFO = "short";

        public static final Uri CONTENT_URI_SHORT_INFO = Uri.withAppendedPath(CONTENT_URI, PATH_SHORT_INFO);


        public static final String CONTENT_LIST_TYPE_SHORT_INFO
                = CONTENT_LIST_TYPE + "." + PATH_SHORT_INFO;

        public static final String PATH_RESCUER_CODE = "code";

        public static final Uri CONTENT_URI_RESCUER_CODE = Uri.withAppendedPath(CONTENT_URI, PATH_RESCUER_CODE);


        public static final String CONTENT_ITEM_TYPE_RESCUER_CODE
                = CONTENT_ITEM_TYPE + "." + PATH_RESCUER_CODE;


        public static final String TABLE_NAME = "rescuer";


        public static final String COLUMN_RESCUER_NAME = "rescuer_name";


        public static final String COLUMN_RESCUER_CODE = "rescuer_code";


        public static Uri buildRescuerCodeUri(String rescuerCode) {
            return CONTENT_URI_RESCUER_CODE.buildUpon().appendPath(rescuerCode).build();
        }


        @NonNull
        public static String getQualifiedColumnName(String columnNameStr) {
            return TextUtils.concat(TABLE_NAME, ".", columnNameStr).toString();
        }
    }


    public static final class RescuerContactType implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CONTACT_TYPE);


        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_CONTACT_TYPE;


        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_CONTACT_TYPE;


        public static final String TABLE_NAME = "contact_type";


        public static final String COLUMN_CONTACT_TYPE_NAME = "type_name";


        public static final String CONTACT_TYPE_PHONE = "Phone";
        public static final int CONTACT_TYPE_ID_PHONE = 0;
        public static final String CONTACT_TYPE_EMAIL = "Email";
        public static final int CONTACT_TYPE_ID_EMAIL = 1;

        private static final String[] PRELOADED_CONTACT_TYPES = new String[]{
                CONTACT_TYPE_PHONE,
                CONTACT_TYPE_EMAIL
        };


        @NonNull
        public static String getQualifiedColumnName(String columnNameStr) {
            return TextUtils.concat(TABLE_NAME, ".", columnNameStr).toString();
        }


        public static String[] getPreloadedContactTypes() {
            return PRELOADED_CONTACT_TYPES;
        }
    }


    public static final class RescuerContact implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(Rescuer.CONTENT_URI, PATH_RESCUER_CONTACT);


        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "." + PATH_RESCUER + "." + PATH_RESCUER_CONTACT;


        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "." + PATH_RESCUER + "." + PATH_RESCUER_CONTACT;


        public static final String TABLE_NAME = "rescuer_contact";


        public static final String COLUMN_RESCUER_CONTACT_TYPE_ID = "contact_type_id";


        public static final String COLUMN_RESCUER_CONTACT_VALUE = "contact_value";


        public static final String COLUMN_RESCUER_CONTACT_DEFAULT = "is_default";


        public static final String COLUMN_RESCUER_ID = "rescuer_id";


        public static final int RESCUER_CONTACT_DEFAULT = 1;
        public static final int RESCUER_CONTACT_NON_DEFAULT = 0;


        @NonNull
        public static String getQualifiedColumnName(String columnNameStr) {
            return TextUtils.concat(TABLE_NAME, ".", columnNameStr).toString();
        }
    }

}
