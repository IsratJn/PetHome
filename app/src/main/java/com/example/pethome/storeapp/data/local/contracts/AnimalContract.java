

package com.example.pethome.storeapp.data.local.contracts;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class AnimalContract implements StoreContract {


    public static final String PATH_ITEM = "item";

    public static final String PATH_CATEGORY = "category";

    public static final String PATH_ITEM_IMAGE = "image";

    public static final String PATH_ITEM_ATTR = "attr";

    private AnimalContract() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }

    public static final class Animal implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEM);

        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_ITEM;

        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_ITEM;

        public static final String PATH_SHORT_INFO = "short";

        public static final Uri CONTENT_URI_SHORT_INFO = Uri.withAppendedPath(CONTENT_URI, PATH_SHORT_INFO);

        public static final String CONTENT_LIST_TYPE_SHORT_INFO
                = CONTENT_LIST_TYPE + "." + PATH_SHORT_INFO;

        public static final String PATH_ITEM_SKU = "sku";

        public static final Uri CONTENT_URI_ITEM_SKU = Uri.withAppendedPath(CONTENT_URI, PATH_ITEM_SKU);

        public static final String CONTENT_ITEM_TYPE_SKU
                = CONTENT_ITEM_TYPE + "." + PATH_ITEM_SKU;

        public static final String TABLE_NAME = "item";

        public static final String COLUMN_ITEM_SKU = "item_sku";


        public static final String COLUMN_ITEM_NAME = "item_name";


        public static final String COLUMN_ITEM_DESCRIPTION = "item_description";


        public static final String COLUMN_ITEM_CATEGORY_ID = "category_id";


        public static Uri buildItemSkuUri(String itemSku) {
            return CONTENT_URI_ITEM_SKU.buildUpon().appendPath(itemSku).build();
        }


        @NonNull
        public static String getQualifiedColumnName(String columnNameStr) {
            return TextUtils.concat(TABLE_NAME, ".", columnNameStr).toString();
        }
    }


    public static final class AnimalCategory implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORY);


        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_CATEGORY;


        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_CATEGORY;


        public static final String TABLE_NAME = "item_category";


        public static final String COLUMN_ITEM_CATEGORY_NAME = "category_name";


        private static final String[] PRELOADED_CATEGORIES = new String[]{
                "Cats",
                "Dogs",
                "Rabbits",
                "Birds",
                "Monkeys"
        };


        public static Uri buildCategoryNameUri(String categoryName) {
            return CONTENT_URI.buildUpon().appendPath(categoryName).build();
        }


        @NonNull
        public static String getQualifiedColumnName(String columnNameStr) {
            return TextUtils.concat(TABLE_NAME, ".", columnNameStr).toString();
        }


        public static String[] getPreloadedCategories() {
            return PRELOADED_CATEGORIES;
        }
    }


    public static final class AnimalImage implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(Animal.CONTENT_URI, PATH_ITEM_IMAGE);


        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "." + PATH_ITEM + "." + PATH_ITEM_IMAGE;


        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "." + PATH_ITEM + "." + PATH_ITEM_IMAGE;


        public static final String TABLE_NAME = "item_image";


        public static final String COLUMN_ITEM_ID = "item_id";


        public static final String COLUMN_ITEM_IMAGE_URI = "image_uri";


        public static final String COLUMN_ITEM_IMAGE_DEFAULT = "is_default";


        public static final int ITEM_IMAGE_DEFAULT = 1;
        public static final int ITEM_IMAGE_NON_DEFAULT = 0;


        @NonNull
        public static String getQualifiedColumnName(String columnNameStr) {
            return TextUtils.concat(TABLE_NAME, ".", columnNameStr).toString();
        }
    }


    public static final class AnimalAttribute implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(Animal.CONTENT_URI, PATH_ITEM_ATTR);


        public static final String CONTENT_LIST_TYPE
                = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "." + PATH_ITEM + "." + PATH_ITEM_ATTR;


        public static final String CONTENT_ITEM_TYPE
                = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "." + PATH_ITEM + "." + PATH_ITEM_ATTR;


        public static final String TABLE_NAME = "item_attr";


        public static final String COLUMN_ITEM_ID = "item_id";


        public static final String COLUMN_ITEM_ATTR_NAME = "attr_name";


        public static final String COLUMN_ITEM_ATTR_VALUE = "attr_value";


        @NonNull
        public static String getQualifiedColumnName(String columnNameStr) {
            return TextUtils.concat(TABLE_NAME, ".", columnNameStr).toString();
        }
    }
}
