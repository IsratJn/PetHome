
package com.example.pethome.storeapp.data.local.contracts;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class AdoptionsContract implements StoreContract {

    public static final String PATH_ITEM_RESCUER_INFO = "adoptionsinfo";

    public static final String PATH_ITEM_RESCUER_INVENTORY = "adoptionsinventory";


    private AdoptionsContract() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }


    public interface AnimalRescuerColumns extends BaseColumns {


        String COLUMN_ITEM_ID = "item_id";


        String COLUMN_RESCUER_ID = "rescuer_id";
    }


    public static final class AnimalRescuerInfo implements AnimalRescuerColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEM_RESCUER_INFO);


        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_ITEM_RESCUER_INFO;


        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_ITEM_RESCUER_INFO;


        public static final Uri CONTENT_URI_RESCUER_ITEMS = Uri.withAppendedPath(CONTENT_URI, RescuerContract.PATH_RESCUER);


        public static final String CONTENT_LIST_TYPE_RESCUER_ITEMS
                = CONTENT_LIST_TYPE + "." + RescuerContract.PATH_RESCUER;


        public static final Uri CONTENT_URI_ITEM_RESCUERS = Uri.withAppendedPath(CONTENT_URI, AnimalContract.PATH_ITEM);


        public static final String CONTENT_LIST_TYPE_ITEM_RESCUERS
                = CONTENT_LIST_TYPE + "." + AnimalContract.PATH_ITEM;


        public static final String TABLE_NAME = "item_rescuer_info";


        public static final String COLUMN_ITEM_UNIT_PRICE = "unit_price";


        public static final float DEFAULT_ITEM_UNIT_PRICE = 0.0f;


        @NonNull
        public static String getQualifiedColumnName(String columnNameStr) {
            return TextUtils.concat(TABLE_NAME, ".", columnNameStr).toString();
        }
    }


    public static final class AnimalRescuerInventory implements AnimalRescuerColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEM_RESCUER_INVENTORY);


        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_ITEM_RESCUER_INVENTORY;


        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_ITEM_RESCUER_INVENTORY;

        public static final Uri CONTENT_URI_INV_RESCUER = Uri.withAppendedPath(CONTENT_URI, RescuerContract.PATH_RESCUER);


        public static final String CONTENT_LIST_TYPE_INV_RESCUER
                = CONTENT_LIST_TYPE + "." + RescuerContract.PATH_RESCUER;

        public static final Uri CONTENT_URI_INV_ITEM = Uri.withAppendedPath(CONTENT_URI, AnimalContract.PATH_ITEM);


        public static final String CONTENT_LIST_TYPE_INV_ITEM
                = CONTENT_LIST_TYPE + "." + AnimalContract.PATH_ITEM;

        public static final String PATH_SHORT_INFO = "short";

        public static final Uri CONTENT_URI_SHORT_INFO = Uri.withAppendedPath(CONTENT_URI, PATH_SHORT_INFO);


        public static final String CONTENT_LIST_TYPE_SHORT_INFO
                = CONTENT_LIST_TYPE + "." + PATH_SHORT_INFO;


        public static final String TABLE_NAME = "item_rescuer_inventory";


        public static final String COLUMN_ITEM_AVAIL_QUANTITY = "available_quantity";


        public static final int DEFAULT_ITEM_AVAIL_QUANTITY = 0;


        @NonNull
        public static String getQualifiedColumnName(String columnNameStr) {
            return TextUtils.concat(TABLE_NAME, ".", columnNameStr).toString();
        }
    }
}
