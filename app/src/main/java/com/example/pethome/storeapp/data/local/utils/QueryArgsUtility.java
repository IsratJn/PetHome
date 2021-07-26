
package com.example.pethome.storeapp.data.local.utils;

import android.content.ContentUris;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.pethome.storeapp.data.local.contracts.AnimalContract.Animal;
import com.example.pethome.storeapp.data.local.contracts.AnimalContract.AnimalAttribute;
import com.example.pethome.storeapp.data.local.contracts.AnimalContract.AnimalCategory;
import com.example.pethome.storeapp.data.local.contracts.AnimalContract.AnimalImage;
import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract.AnimalRescuerInfo;
import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract.AnimalRescuerInventory;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract.Rescuer;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract.RescuerContact;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract.RescuerContactType;

import java.util.HashMap;
import java.util.Map;

import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.AND;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.AS;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.CLOSE_BRACE;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.COUNT;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.DESC;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.EQUALS;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.IS;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.JOIN;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.LEFT_JOIN;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.NULL;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.ON;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.OPEN_BRACE;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.OR;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.PLACEHOLDER;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.SUM;


public final class QueryArgsUtility {

    private QueryArgsUtility() {
        //Suppressing with an error to enforce noninstantiability
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }

    public static final class ItemAttributesQuery {

        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_ITEM_ID_INDEX = 0;
        public static final int COLUMN_ITEM_ATTR_NAME_INDEX = 1;
        public static final int COLUMN_ITEM_ATTR_VALUE_INDEX = 2;

        public static void setTables(SQLiteQueryBuilder queryBuilder) {
            String inTables = Animal.TABLE_NAME + JOIN
                    + AnimalAttribute.TABLE_NAME + ON
                    + Animal.getQualifiedColumnName(Animal._ID)
                    + EQUALS
                    + AnimalAttribute.getQualifiedColumnName(AnimalAttribute.COLUMN_ITEM_ID);
            queryBuilder.setTables(inTables);
        }

        public static void setProjectionMap(SQLiteQueryBuilder queryBuilder) {
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(AnimalAttribute.getQualifiedColumnName(AnimalAttribute.COLUMN_ITEM_ID),
                    AnimalAttribute.getQualifiedColumnName(AnimalAttribute.COLUMN_ITEM_ID));
            columnMap.put(AnimalAttribute.getQualifiedColumnName(AnimalAttribute.COLUMN_ITEM_ATTR_NAME),
                    AnimalAttribute.getQualifiedColumnName(AnimalAttribute.COLUMN_ITEM_ATTR_NAME));
            columnMap.put(AnimalAttribute.getQualifiedColumnName(AnimalAttribute.COLUMN_ITEM_ATTR_VALUE),
                    AnimalAttribute.getQualifiedColumnName(AnimalAttribute.COLUMN_ITEM_ATTR_VALUE));
            queryBuilder.setProjectionMap(columnMap);
        }

        public static String[] getProjection() {
            return new String[]{
                    AnimalAttribute.getQualifiedColumnName(AnimalAttribute.COLUMN_ITEM_ID),
                    AnimalAttribute.getQualifiedColumnName(AnimalAttribute.COLUMN_ITEM_ATTR_NAME),
                    AnimalAttribute.getQualifiedColumnName(AnimalAttribute.COLUMN_ITEM_ATTR_VALUE)
            };
        }

        public static String getSelection() {
            //Where clause is the 'Item' table's Key
            return Animal.getQualifiedColumnName(Animal._ID) + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the 'id' passed in the URI
                    String.valueOf(ContentUris.parseId(uri))
            };
        }

    }

    public static final class ItemImagesQuery {

        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_ITEM_ID_INDEX = 0;
        public static final int COLUMN_ITEM_IMAGE_URI_INDEX = 1;
        public static final int COLUMN_ITEM_IMAGE_DEFAULT_INDEX = 2;

        public static void setTables(SQLiteQueryBuilder queryBuilder) {
            String inTables = Animal.TABLE_NAME + JOIN
                    + AnimalImage.TABLE_NAME + ON
                    + AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_ID)
                    + EQUALS
                    + Animal.getQualifiedColumnName(Animal._ID);
            queryBuilder.setTables(inTables);
        }

        public static void setProjectionMap(SQLiteQueryBuilder queryBuilder) {
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_ID),
                    AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_ID));
            columnMap.put(AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_URI),
                    AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_URI));
            columnMap.put(AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_DEFAULT),
                    AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_DEFAULT));
            queryBuilder.setProjectionMap(columnMap);
        }

        public static String[] getProjection() {
            return new String[]{
                    AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_ID),
                    AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_URI),
                    AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_DEFAULT)
            };
        }

        public static String getSelection() {
            //Where clause is the 'Item' table's Key
            return Animal.getQualifiedColumnName(Animal._ID) + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the 'id' passed in the URI
                    String.valueOf(ContentUris.parseId(uri))
            };
        }
    }

    public static final class CategoriesQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_ITEM_CATEGORY_NAME_INDEX = 0;

        public static String[] getProjection() {
            return new String[]{
                    AnimalCategory.COLUMN_ITEM_CATEGORY_NAME
            };
        }
    }

    public static final class CategoryByNameQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_ITEM_CATEGORY_ID_INDEX = 0;
        public static final int COLUMN_ITEM_CATEGORY_NAME_INDEX = 1;

        public static String[] getProjection() {
            return new String[]{
                    AnimalCategory._ID,
                    AnimalCategory.COLUMN_ITEM_CATEGORY_NAME
            };
        }

        public static String getSelection() {
            //Where clause is only the 'item_category' table's category_name column
            return AnimalCategory.COLUMN_ITEM_CATEGORY_NAME + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the 'category_name' passed in the URI
                    uri.getLastPathSegment()
            };
        }
    }

    public static final class CategoryByIdQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_ITEM_CATEGORY_ID_INDEX = 0;
        public static final int COLUMN_ITEM_CATEGORY_NAME_INDEX = 1;

        public static String[] getProjection() {
            return new String[]{
                    AnimalCategory._ID,
                    AnimalCategory.COLUMN_ITEM_CATEGORY_NAME
            };
        }

        public static String getSelection() {
            //Where clause is only the 'item_category' table's _id column
            return AnimalCategory._ID + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the '_id' passed in the URI
                    String.valueOf(ContentUris.parseId(uri))
            };
        }
    }

    public static final class ItemByIdQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_ITEM_ID_INDEX = 0;
        public static final int COLUMN_ITEM_NAME_INDEX = 1;
        public static final int COLUMN_ITEM_SKU_INDEX = 2;
        public static final int COLUMN_ITEM_DESCRIPTION_INDEX = 3;
        public static final int COLUMN_ITEM_CATEGORY_NAME_INDEX = 4;

        public static void setTables(SQLiteQueryBuilder queryBuilder) {
            String inTables = Animal.TABLE_NAME + JOIN
                    + AnimalCategory.TABLE_NAME + ON
                    + Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_CATEGORY_ID)
                    + EQUALS
                    + AnimalCategory.getQualifiedColumnName(AnimalCategory._ID);
            queryBuilder.setTables(inTables);
        }

        public static void setProjectionMap(SQLiteQueryBuilder queryBuilder) {
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(Animal.getQualifiedColumnName(Animal._ID),
                    Animal.getQualifiedColumnName(Animal._ID));
            columnMap.put(Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_NAME),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_NAME));
            columnMap.put(Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_SKU),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_SKU));
            columnMap.put(Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_DESCRIPTION),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_DESCRIPTION));
            columnMap.put(AnimalCategory.getQualifiedColumnName(AnimalCategory.COLUMN_ITEM_CATEGORY_NAME),
                    AnimalCategory.getQualifiedColumnName(AnimalCategory.COLUMN_ITEM_CATEGORY_NAME));
            queryBuilder.setProjectionMap(columnMap);
        }

        public static String[] getProjection() {
            return new String[]{
                    Animal.getQualifiedColumnName(Animal._ID),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_NAME),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_SKU),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_DESCRIPTION),
                    AnimalCategory.getQualifiedColumnName(AnimalCategory.COLUMN_ITEM_CATEGORY_NAME)
            };
        }

        public static String getSelection() {
            //Where clause is the 'Item' table's Key
            return Animal.getQualifiedColumnName(Animal._ID) + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the 'id' passed in the URI
                    String.valueOf(ContentUris.parseId(uri))
            };
        }
    }

    public static final class ItemsShortInfoQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_ITEM_ID_INDEX = 0;
        public static final int COLUMN_ITEM_NAME_INDEX = 1;
        public static final int COLUMN_ITEM_SKU_INDEX = 2;
        public static final int COLUMN_ITEM_CATEGORY_NAME_INDEX = 3;
        public static final int COLUMN_ITEM_IMAGE_URI_INDEX = 4;

        public static void setTables(SQLiteQueryBuilder queryBuilder) {
            String inTables = Animal.TABLE_NAME + JOIN
                    + AnimalCategory.TABLE_NAME + ON
                    + Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_CATEGORY_ID)
                    + EQUALS
                    + AnimalCategory.getQualifiedColumnName(AnimalCategory._ID)
                    + LEFT_JOIN
                    + AnimalImage.TABLE_NAME + ON
                    + Animal.getQualifiedColumnName(Animal._ID)
                    + EQUALS
                    + AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_ID);
            queryBuilder.setTables(inTables);
        }

        public static void setProjectionMap(SQLiteQueryBuilder queryBuilder) {
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(Animal.getQualifiedColumnName(Animal._ID),
                    Animal.getQualifiedColumnName(Animal._ID));
            columnMap.put(Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_NAME),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_NAME));
            columnMap.put(Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_SKU),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_SKU));
            columnMap.put(AnimalCategory.getQualifiedColumnName(AnimalCategory.COLUMN_ITEM_CATEGORY_NAME),
                    AnimalCategory.getQualifiedColumnName(AnimalCategory.COLUMN_ITEM_CATEGORY_NAME));
            columnMap.put(AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_URI),
                    AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_URI));
            queryBuilder.setProjectionMap(columnMap);
        }

        public static String[] getProjection() {
            return new String[]{
                    Animal.getQualifiedColumnName(Animal._ID),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_NAME),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_SKU),
                    AnimalCategory.getQualifiedColumnName(AnimalCategory.COLUMN_ITEM_CATEGORY_NAME),
                    AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_URI)
            };
        }

        public static String getSelection() {
            //Where clause is the 'item_image' table's 'is_default' column
            //(item_image.is_default is null or item_image.is_default = 1)
            return AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_DEFAULT) + IS + NULL +
                    OR + AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_DEFAULT) + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs() {
            return new String[]{
                    //Where clause value is '1' that denotes the default image of the item
                    String.valueOf(AnimalImage.ITEM_IMAGE_DEFAULT)
            };
        }

    }

    public static final class ItemBySkuQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_ITEM_ID_INDEX = 0;
        public static final int COLUMN_ITEM_NAME_INDEX = 1;
        public static final int COLUMN_ITEM_SKU_INDEX = 2;

        public static String[] getProjection() {
            return new String[]{
                    Animal._ID,
                    Animal.COLUMN_ITEM_NAME,
                    Animal.COLUMN_ITEM_SKU
            };
        }

        public static String getSelection() {
            //Where clause is only the 'item' table's item_sku column
            return Animal.COLUMN_ITEM_SKU + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the 'item_sku' passed in the URI
                    uri.getLastPathSegment()
            };
        }
    }

    public static final class RescuerByIdQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_RESCUER_ID_INDEX = 0;
        public static final int COLUMN_RESCUER_NAME_INDEX = 1;
        public static final int COLUMN_RESCUER_CODE_INDEX = 2;

        public static String[] getProjection() {
            return new String[]{
                    Rescuer._ID,
                    Rescuer.COLUMN_RESCUER_NAME,
                    Rescuer.COLUMN_RESCUER_CODE
            };
        }

        public static String getSelection() {
            //Where clause is only the 'rescuer' table's _id column
            return Rescuer._ID + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the '_id' passed in the URI
                    String.valueOf(ContentUris.parseId(uri))
            };
        }
    }

    public static final class RescuerContactsQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_RESCUER_ID_INDEX = 0;
        public static final int COLUMN_RESCUER_CONTACT_TYPE_ID_INDEX = 1;
        public static final int COLUMN_RESCUER_CONTACT_VALUE_INDEX = 2;
        public static final int COLUMN_RESCUER_CONTACT_DEFAULT_INDEX = 3;

        public static void setTables(SQLiteQueryBuilder queryBuilder) {
            String inTables = Rescuer.TABLE_NAME + JOIN
                    + RescuerContact.TABLE_NAME + ON
                    + RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_ID)
                    + EQUALS
                    + Rescuer.getQualifiedColumnName(Rescuer._ID);
            queryBuilder.setTables(inTables);
        }

        public static void setProjectionMap(SQLiteQueryBuilder queryBuilder) {
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(Rescuer.getQualifiedColumnName(Rescuer._ID),
                    Rescuer.getQualifiedColumnName(Rescuer._ID));
            columnMap.put(RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_TYPE_ID),
                    RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_TYPE_ID));
            columnMap.put(RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_VALUE),
                    RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_VALUE));
            columnMap.put(RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_DEFAULT),
                    RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_DEFAULT));
            queryBuilder.setProjectionMap(columnMap);
        }

        public static String[] getProjection() {
            return new String[]{
                    Rescuer.getQualifiedColumnName(Rescuer._ID),
                    RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_TYPE_ID),
                    RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_VALUE),
                    RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_DEFAULT)
            };
        }

        public static String getSelection() {
            //Where clause is only the 'rescuer' table's _id column
            return Rescuer.getQualifiedColumnName(Rescuer._ID) + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the '_id' passed in the URI
                    String.valueOf(ContentUris.parseId(uri))
            };
        }
    }

    public static final class RescuerItemsQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_RESCUER_ID_INDEX = 0;
        public static final int COLUMN_ITEM_ID_INDEX = 1;
        public static final int COLUMN_ITEM_UNIT_PRICE_INDEX = 2;

        public static String[] getProjection() {
            return new String[]{
                    AnimalRescuerInfo.COLUMN_RESCUER_ID,
                    AnimalRescuerInfo.COLUMN_ITEM_ID,
                    AnimalRescuerInfo.COLUMN_ITEM_UNIT_PRICE
            };
        }

        public static String getSelection() {
            //Where clause is only the 'item_rescuer_info' table's rescuer_id column
            return AnimalRescuerInfo.COLUMN_RESCUER_ID + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the 'rescuer_id' passed in the URI
                    String.valueOf(ContentUris.parseId(uri))
            };
        }
    }

    public static final class ItemRescuersQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_RESCUER_ID_INDEX = 0;
        public static final int COLUMN_ITEM_ID_INDEX = 1;
        public static final int COLUMN_ITEM_UNIT_PRICE_INDEX = 2;

        public static String[] getProjection() {
            return new String[]{
                    AnimalRescuerInfo.COLUMN_RESCUER_ID,
                    AnimalRescuerInfo.COLUMN_ITEM_ID,
                    AnimalRescuerInfo.COLUMN_ITEM_UNIT_PRICE
            };
        }

        public static String getSelection() {
            //Where clause is only the 'item_rescuer_info' table's item_id column
            return AnimalRescuerInfo.COLUMN_ITEM_ID + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the 'item_id' passed in the URI
                    String.valueOf(ContentUris.parseId(uri))
            };
        }
    }

    public static final class RescuerByCodeQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_RESCUER_ID_INDEX = 0;
        public static final int COLUMN_RESCUER_NAME_INDEX = 1;
        public static final int COLUMN_RESCUER_CODE_INDEX = 2;

        public static String[] getProjection() {
            return new String[]{
                    Rescuer._ID,
                    Rescuer.COLUMN_RESCUER_NAME,
                    Rescuer.COLUMN_RESCUER_CODE
            };
        }

        public static String getSelection() {
            //Where clause is only the 'rescuer' table's 'rescuer_code' column
            return Rescuer.COLUMN_RESCUER_CODE + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the 'rescuer_code' passed in the URI
                    uri.getLastPathSegment()
            };
        }
    }

    public static final class RescuersShortInfoQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_RESCUER_ID_INDEX = 0;
        public static final int COLUMN_RESCUER_NAME_INDEX = 1;
        public static final int COLUMN_RESCUER_CODE_INDEX = 2;
        public static final int COLUMN_RESCUER_DEFAULT_PHONE_INDEX = 3;
        public static final int COLUMN_RESCUER_DEFAULT_EMAIL_INDEX = 4;
        public static final int COLUMN_RESCUER_ITEM_COUNT_INDEX = 5;
        //Column Name constants for the custom columns
        private static final String COLUMN_RESCUER_ITEM_COUNT = "item_count";
        private static final String COLUMN_RESCUER_DEFAULT_PHONE = "default_phone";
        private static final String COLUMN_RESCUER_DEFAULT_EMAIL = "default_email";

        private static String getDefaultContactSubQuery(int contactTypeId) {
            //Initializing the QueryBuilder
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

            //Constructing the Projection Map
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_VALUE),
                    RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_VALUE));
            queryBuilder.setProjectionMap(columnMap);

            //Setting the Projection
            String[] projection = new String[]{
                    RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_VALUE)
            };

            //Constructing the relationship between Tables involved
            String inTables = RescuerContact.TABLE_NAME + JOIN
                    + RescuerContactType.TABLE_NAME + ON
                    + RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_TYPE_ID)
                    + EQUALS
                    + RescuerContactType.getQualifiedColumnName(RescuerContactType._ID);
            queryBuilder.setTables(inTables);

            //Constructing the WHERE Clause
            String selection = RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_ID)
                    + EQUALS + Rescuer.getQualifiedColumnName(Rescuer._ID) + AND
                    + RescuerContactType.getQualifiedColumnName(RescuerContactType._ID)
                    + EQUALS + contactTypeId + AND
                    + RescuerContact.getQualifiedColumnName(RescuerContact.COLUMN_RESCUER_CONTACT_DEFAULT)
                    + EQUALS + RescuerContact.RESCUER_CONTACT_DEFAULT;

            //Returning the Sub Query built
            return queryBuilder.buildQuery(projection, selection, null, null, null, null);
        }

        private static String getItemCountSubQuery() {
            //Initializing the QueryBuilder
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

            //Constructing the Projection Map
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(COLUMN_RESCUER_ITEM_COUNT, COUNT + OPEN_BRACE + Animal.getQualifiedColumnName(Animal._ID) + CLOSE_BRACE + AS + COLUMN_RESCUER_ITEM_COUNT);
            queryBuilder.setProjectionMap(columnMap);

            //Setting the Projection
            String[] projection = new String[]{
                    COLUMN_RESCUER_ITEM_COUNT
            };

            //Constructing the relationship between Tables involved
            String inTables = Animal.TABLE_NAME + JOIN
                    + AnimalRescuerInfo.TABLE_NAME + ON
                    + AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_ID)
                    + EQUALS
                    + Animal.getQualifiedColumnName(Animal._ID);
            queryBuilder.setTables(inTables);

            //Constructing the WHERE Clause
            String selection = AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_RESCUER_ID)
                    + EQUALS + Rescuer.getQualifiedColumnName(Rescuer._ID);

            //Returning the Sub Query built
            return queryBuilder.buildQuery(projection, selection, null, null, null, null);
        }

        public static void setTables(SQLiteQueryBuilder queryBuilder) {
            String inTables = Rescuer.TABLE_NAME;
            queryBuilder.setTables(inTables);
        }

        public static void setProjectionMap(SQLiteQueryBuilder queryBuilder) {
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(Rescuer.getQualifiedColumnName(Rescuer._ID), Rescuer.getQualifiedColumnName(Rescuer._ID));
            columnMap.put(Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_NAME), Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_NAME));
            columnMap.put(Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_CODE), Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_CODE));
            columnMap.put(COLUMN_RESCUER_DEFAULT_PHONE, OPEN_BRACE + getDefaultContactSubQuery(RescuerContactType.CONTACT_TYPE_ID_PHONE) + CLOSE_BRACE + AS + COLUMN_RESCUER_DEFAULT_PHONE);
            columnMap.put(COLUMN_RESCUER_DEFAULT_EMAIL, OPEN_BRACE + getDefaultContactSubQuery(RescuerContactType.CONTACT_TYPE_ID_EMAIL) + CLOSE_BRACE + AS + COLUMN_RESCUER_DEFAULT_EMAIL);
            columnMap.put(COLUMN_RESCUER_ITEM_COUNT, OPEN_BRACE + getItemCountSubQuery() + CLOSE_BRACE + AS + COLUMN_RESCUER_ITEM_COUNT);
            queryBuilder.setProjectionMap(columnMap);
        }

        public static String[] getProjection() {
            return new String[]{
                    Rescuer.getQualifiedColumnName(Rescuer._ID),
                    Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_NAME),
                    Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_CODE),
                    COLUMN_RESCUER_DEFAULT_PHONE,
                    COLUMN_RESCUER_DEFAULT_EMAIL,
                    COLUMN_RESCUER_ITEM_COUNT
            };
        }
    }

    public static final class AdoptionsShortInfoQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_ITEM_ID_INDEX = 0;
        public static final int COLUMN_RESCUER_ID_INDEX = 1;
        public static final int COLUMN_ITEM_NAME_INDEX = 2;
        public static final int COLUMN_ITEM_SKU_INDEX = 3;
        public static final int COLUMN_ITEM_CATEGORY_NAME_INDEX = 4;
        public static final int COLUMN_ITEM_IMAGE_URI_INDEX = 5;
        public static final int COLUMN_RESCUER_NAME_INDEX = 6;
        public static final int COLUMN_RESCUER_CODE_INDEX = 7;
        public static final int COLUMN_ITEM_UNIT_PRICE_INDEX = 8;
        public static final int COLUMN_RESCUER_AVAIL_QUANTITY_INDEX = 9;
        public static final int COLUMN_TOTAL_AVAIL_QUANTITY_INDEX = 10;
        //Column Name constants for the custom columns
        private static final String COLUMN_RESCUER_AVAIL_QUANTITY = "rescuer_available_quantity";
        private static final String COLUMN_TOTAL_AVAIL_QUANTITY = "total_available_quantity";

        private static String getTotalAvailQuantitySubQuery() {
            //Initializing the QueryBuilder
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

            //Constructing the Projection Map
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(COLUMN_TOTAL_AVAIL_QUANTITY, SUM + OPEN_BRACE + AnimalRescuerInventory.COLUMN_ITEM_AVAIL_QUANTITY + CLOSE_BRACE + AS + COLUMN_TOTAL_AVAIL_QUANTITY);
            queryBuilder.setProjectionMap(columnMap);

            //Setting the Projection
            String[] projection = new String[]{
                    COLUMN_TOTAL_AVAIL_QUANTITY
            };

            //Constructing the relationship between Tables involved
            String inTables = AnimalRescuerInventory.TABLE_NAME;
            queryBuilder.setTables(inTables);

            //Constructing the WHERE Clause
            String selection = AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_ID)
                    + EQUALS + Animal.getQualifiedColumnName(Animal._ID);

            //Returning the Sub Query built
            return queryBuilder.buildQuery(projection, selection, null, null, null, null);
        }

        private static String getTopRescuerIdSubQuery() {
            //Initializing the QueryBuilder
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

            //Constructing the Projection Map
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(Rescuer.getQualifiedColumnName(Rescuer._ID), Rescuer.getQualifiedColumnName(Rescuer._ID));
            queryBuilder.setProjectionMap(columnMap);

            //Setting the Projection
            String[] projection = new String[]{
                    Rescuer.getQualifiedColumnName(Rescuer._ID)
            };

            //Constructing the relationship between Tables involved
            String inTables = Rescuer.TABLE_NAME + JOIN
                    + AnimalRescuerInventory.TABLE_NAME + ON
                    + AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_RESCUER_ID)
                    + EQUALS
                    + Rescuer.getQualifiedColumnName(Rescuer._ID);
            queryBuilder.setTables(inTables);

            //Constructing the WHERE Clause
            String selection = AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_ID)
                    + EQUALS + Animal.getQualifiedColumnName(Animal._ID);

            //Returning the Sub Query built
            return queryBuilder.buildQuery(projection, selection, null, null,
                    AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_AVAIL_QUANTITY) + DESC,
                    "1"
            );
        }

        public static void setTables(SQLiteQueryBuilder queryBuilder) {
            String inTables = Animal.TABLE_NAME + JOIN
                    + AnimalCategory.TABLE_NAME + ON
                    + Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_CATEGORY_ID)
                    + EQUALS + AnimalCategory.getQualifiedColumnName(AnimalCategory._ID)
                    + LEFT_JOIN + AnimalImage.TABLE_NAME + ON
                    + AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_ID)
                    + EQUALS + Animal.getQualifiedColumnName(Animal._ID)
                    + JOIN + AnimalRescuerInfo.TABLE_NAME + ON
                    + AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_ID)
                    + EQUALS + Animal.getQualifiedColumnName(Animal._ID)
                    + JOIN + AnimalRescuerInventory.TABLE_NAME + ON
                    + AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_ID)
                    + EQUALS + Animal.getQualifiedColumnName(Animal._ID)
                    + JOIN + Rescuer.TABLE_NAME + ON
                    + Rescuer.getQualifiedColumnName(Rescuer._ID)
                    + EQUALS + AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_RESCUER_ID);
            queryBuilder.setTables(inTables);
        }

        public static void setProjectionMap(SQLiteQueryBuilder queryBuilder) {
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_ID), AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_ID));
            columnMap.put(AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_RESCUER_ID), AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_RESCUER_ID));
            columnMap.put(Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_NAME), Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_NAME));
            columnMap.put(Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_SKU), Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_SKU));
            columnMap.put(AnimalCategory.getQualifiedColumnName(AnimalCategory.COLUMN_ITEM_CATEGORY_NAME), AnimalCategory.getQualifiedColumnName(AnimalCategory.COLUMN_ITEM_CATEGORY_NAME));
            columnMap.put(AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_URI), AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_URI));
            columnMap.put(Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_NAME), Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_NAME));
            columnMap.put(Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_CODE), Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_CODE));
            columnMap.put(AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_UNIT_PRICE), AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_UNIT_PRICE));
            columnMap.put(COLUMN_RESCUER_AVAIL_QUANTITY, AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_AVAIL_QUANTITY) + AS + COLUMN_RESCUER_AVAIL_QUANTITY);
            columnMap.put(COLUMN_TOTAL_AVAIL_QUANTITY, OPEN_BRACE + getTotalAvailQuantitySubQuery() + CLOSE_BRACE + AS + COLUMN_TOTAL_AVAIL_QUANTITY);
            queryBuilder.setProjectionMap(columnMap);
        }

        public static String[] getProjection() {
            return new String[]{
                    AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_ID),
                    AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_RESCUER_ID),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_NAME),
                    Animal.getQualifiedColumnName(Animal.COLUMN_ITEM_SKU),
                    AnimalCategory.getQualifiedColumnName(AnimalCategory.COLUMN_ITEM_CATEGORY_NAME),
                    AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_URI),
                    Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_NAME),
                    Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_CODE),
                    AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_UNIT_PRICE),
                    COLUMN_RESCUER_AVAIL_QUANTITY,
                    COLUMN_TOTAL_AVAIL_QUANTITY
            };
        }

        public static String getSelection() {
            return OPEN_BRACE + AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_DEFAULT) + IS + NULL +
                    OR + AnimalImage.getQualifiedColumnName(AnimalImage.COLUMN_ITEM_IMAGE_DEFAULT) + EQUALS + PLACEHOLDER + CLOSE_BRACE +
                    AND + AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_RESCUER_ID) + EQUALS + OPEN_BRACE + getTopRescuerIdSubQuery() + CLOSE_BRACE +
                    AND + AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_RESCUER_ID) +
                    EQUALS + AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_RESCUER_ID);
        }

        public static String[] getSelectionArgs() {
            return new String[]{
                    //'1' that denotes the default image of the item
                    String.valueOf(AnimalImage.ITEM_IMAGE_DEFAULT)
            };
        }
    }

    public static final class ItemRescuersAdoptionsQuery {
        //Constants of Column Index as they would appear in the Select clause
        public static final int COLUMN_ITEM_ID_INDEX = 0;
        public static final int COLUMN_RESCUER_ID_INDEX = 1;
        public static final int COLUMN_RESCUER_NAME_INDEX = 2;
        public static final int COLUMN_RESCUER_CODE_INDEX = 3;
        public static final int COLUMN_ITEM_UNIT_PRICE_INDEX = 4;
        public static final int COLUMN_AVAIL_QUANTITY_INDEX = 5;

        public static void setTables(SQLiteQueryBuilder queryBuilder) {
            String inTables = Rescuer.TABLE_NAME
                    + JOIN + AnimalRescuerInfo.TABLE_NAME + ON
                    + AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_RESCUER_ID)
                    + EQUALS + Rescuer.getQualifiedColumnName(Rescuer._ID)
                    + JOIN + AnimalRescuerInventory.TABLE_NAME + ON
                    + AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_RESCUER_ID)
                    + EQUALS + AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_RESCUER_ID);
            queryBuilder.setTables(inTables);
        }

        public static void setProjectionMap(SQLiteQueryBuilder queryBuilder) {
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put(AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_ID),
                    AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_ID));
            columnMap.put(AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_RESCUER_ID),
                    AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_RESCUER_ID));
            columnMap.put(Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_NAME),
                    Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_NAME));
            columnMap.put(Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_CODE),
                    Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_CODE));
            columnMap.put(AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_UNIT_PRICE),
                    AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_UNIT_PRICE));
            columnMap.put(AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_AVAIL_QUANTITY),
                    AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_AVAIL_QUANTITY));
            queryBuilder.setProjectionMap(columnMap);
        }

        public static String[] getProjection() {
            return new String[]{
                    AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_ID),
                    AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_RESCUER_ID),
                    Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_NAME),
                    Rescuer.getQualifiedColumnName(Rescuer.COLUMN_RESCUER_CODE),
                    AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_UNIT_PRICE),
                    AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_AVAIL_QUANTITY)
            };
        }

        public static String getSelection() {
            return AnimalRescuerInventory.getQualifiedColumnName(AnimalRescuerInventory.COLUMN_ITEM_ID)
                    + EQUALS + AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_ID)
                    + AND + AnimalRescuerInfo.getQualifiedColumnName(AnimalRescuerInfo.COLUMN_ITEM_ID)
                    + EQUALS + PLACEHOLDER;
        }

        public static String[] getSelectionArgs(@NonNull Uri uri) {
            return new String[]{
                    //Where clause value is the 'item_id' passed in the URI
                    String.valueOf(ContentUris.parseId(uri))
            };
        }
    }
}