

package com.example.pethome.storeapp.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.os.Build;
import android.util.Log;

import com.example.pethome.storeapp.data.local.contracts.AnimalContract.Animal;
import com.example.pethome.storeapp.data.local.contracts.AnimalContract.AnimalAttribute;
import com.example.pethome.storeapp.data.local.contracts.AnimalContract.AnimalCategory;
import com.example.pethome.storeapp.data.local.contracts.AnimalContract.AnimalImage;
import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract.AnimalRescuerInfo;
import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract.AnimalRescuerInventory;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract.Rescuer;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract.RescuerContact;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract.RescuerContactType;
import com.example.pethome.storeapp.utils.AppConstants;
import com.example.pethome.storeapp.utils.AppExecutors;

import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.CLOSE_BRACE;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.COMMA;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.CONFLICT_FAIL;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.CONFLICT_REPLACE;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.CONSTRAINT;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.CREATE_INDEX;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.CREATE_TABLE;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.DEFAULT;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.DELETE_CASCADE;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.FOREIGN_KEY;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.INTEGER;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.NOT;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.NULL;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.ON;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.OPEN_BRACE;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.PRIMARY_KEY;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.PRIMARY_KEY_AUTOINCREMENT;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.REAL;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.REFERENCES;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.SPACE;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.TEXT;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.UNIQUE;

public class StoreDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = StoreDbHelper.class.getSimpleName();


    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "inventory.db";

    private static final String CREATE_TABLE_ITEM
            = CREATE_TABLE + Animal.TABLE_NAME
            + OPEN_BRACE
            + Animal._ID + SPACE + INTEGER + SPACE + PRIMARY_KEY_AUTOINCREMENT + COMMA + SPACE
            + Animal.COLUMN_ITEM_NAME + SPACE + TEXT + NOT + NULL + COMMA + SPACE
            + Animal.COLUMN_ITEM_SKU + SPACE + TEXT + NOT + NULL + COMMA + SPACE
            + Animal.COLUMN_ITEM_DESCRIPTION + SPACE + TEXT + NOT + NULL + COMMA + SPACE
            + Animal.COLUMN_ITEM_CATEGORY_ID + SPACE + INTEGER + COMMA
            + CONSTRAINT + "unique_item_sku" + UNIQUE + OPEN_BRACE + Animal.COLUMN_ITEM_SKU + CLOSE_BRACE + ON + CONFLICT_FAIL + COMMA
            + CONSTRAINT + "fk_category_id"
            + FOREIGN_KEY + OPEN_BRACE + Animal.COLUMN_ITEM_CATEGORY_ID + CLOSE_BRACE
            + REFERENCES + AnimalCategory.TABLE_NAME + OPEN_BRACE + AnimalCategory._ID + CLOSE_BRACE
            + CLOSE_BRACE;

    private static final String CREATE_TABLE_ITEM_CATEGORY
            = CREATE_TABLE + AnimalCategory.TABLE_NAME
            + OPEN_BRACE
            + AnimalCategory._ID + SPACE + INTEGER + SPACE + PRIMARY_KEY_AUTOINCREMENT + COMMA + SPACE
            + AnimalCategory.COLUMN_ITEM_CATEGORY_NAME + SPACE + TEXT + NOT + NULL + COMMA
            + CONSTRAINT + "unique_category_name" + UNIQUE + OPEN_BRACE + AnimalCategory.COLUMN_ITEM_CATEGORY_NAME + CLOSE_BRACE + ON + CONFLICT_FAIL
            + CLOSE_BRACE;

    private static final String CREATE_TABLE_ITEM_IMAGE
            = CREATE_TABLE + AnimalImage.TABLE_NAME
            + OPEN_BRACE
            + AnimalImage.COLUMN_ITEM_ID + SPACE + INTEGER + COMMA + SPACE
            + AnimalImage.COLUMN_ITEM_IMAGE_URI + SPACE + TEXT + COMMA + SPACE
            + AnimalImage.COLUMN_ITEM_IMAGE_DEFAULT + SPACE + INTEGER + NOT + NULL + DEFAULT + AnimalImage.ITEM_IMAGE_NON_DEFAULT + COMMA
            + CONSTRAINT + "unique_image_uri" + UNIQUE + OPEN_BRACE + AnimalImage.COLUMN_ITEM_ID + COMMA + SPACE + AnimalImage.COLUMN_ITEM_IMAGE_URI + CLOSE_BRACE + COMMA
            + CONSTRAINT + "fk_item_id"
            + FOREIGN_KEY + OPEN_BRACE + AnimalImage.COLUMN_ITEM_ID + CLOSE_BRACE
            + REFERENCES + Animal.TABLE_NAME + OPEN_BRACE + Animal._ID + CLOSE_BRACE
            + ON + DELETE_CASCADE
            + CLOSE_BRACE;

    private static final String CREATE_TABLE_ITEM_ATTR
            = CREATE_TABLE + AnimalAttribute.TABLE_NAME
            + OPEN_BRACE
            + AnimalAttribute.COLUMN_ITEM_ID + SPACE + INTEGER + COMMA + SPACE
            + AnimalAttribute.COLUMN_ITEM_ATTR_NAME + SPACE + TEXT + NOT + NULL + COMMA + SPACE
            + AnimalAttribute.COLUMN_ITEM_ATTR_VALUE + SPACE + TEXT + NOT + NULL + COMMA
            + CONSTRAINT + "unique_attr_name" + UNIQUE + OPEN_BRACE + AnimalImage.COLUMN_ITEM_ID + COMMA + SPACE + AnimalAttribute.COLUMN_ITEM_ATTR_NAME + CLOSE_BRACE + COMMA
            + CONSTRAINT + "fk_item_id"
            + FOREIGN_KEY + OPEN_BRACE + AnimalAttribute.COLUMN_ITEM_ID + CLOSE_BRACE
            + REFERENCES + Animal.TABLE_NAME + OPEN_BRACE + Animal._ID + CLOSE_BRACE
            + ON + DELETE_CASCADE
            + CLOSE_BRACE;

    private static final String CREATE_TABLE_RESCUER
            = CREATE_TABLE + Rescuer.TABLE_NAME
            + OPEN_BRACE
            + Rescuer._ID + SPACE + INTEGER + SPACE + PRIMARY_KEY_AUTOINCREMENT + COMMA + SPACE
            + Rescuer.COLUMN_RESCUER_NAME + SPACE + TEXT + NOT + NULL + COMMA + SPACE
            + Rescuer.COLUMN_RESCUER_CODE + SPACE + TEXT + NOT + NULL + COMMA
            + CONSTRAINT + "unique_rescuer_code" + UNIQUE + OPEN_BRACE + Rescuer.COLUMN_RESCUER_CODE + CLOSE_BRACE + ON + CONFLICT_FAIL
            + CLOSE_BRACE;

    private static final String CREATE_TABLE_RESCUER_CONTACT_TYPE
            = CREATE_TABLE + RescuerContactType.TABLE_NAME
            + OPEN_BRACE
            + RescuerContactType._ID + SPACE + INTEGER + SPACE + PRIMARY_KEY + COMMA + SPACE
            + RescuerContactType.COLUMN_CONTACT_TYPE_NAME + SPACE + TEXT + NOT + NULL + COMMA
            + CONSTRAINT + "unique_type_name" + UNIQUE + OPEN_BRACE + RescuerContactType.COLUMN_CONTACT_TYPE_NAME + CLOSE_BRACE
            + CLOSE_BRACE;

    private static final String CREATE_TABLE_RESCUER_CONTACT
            = CREATE_TABLE + RescuerContact.TABLE_NAME
            + OPEN_BRACE
            + RescuerContact.COLUMN_RESCUER_CONTACT_TYPE_ID + SPACE + INTEGER + COMMA + SPACE
            + RescuerContact.COLUMN_RESCUER_CONTACT_VALUE + SPACE + TEXT + NOT + NULL + COMMA + SPACE
            + RescuerContact.COLUMN_RESCUER_CONTACT_DEFAULT + SPACE + INTEGER + NOT + NULL + DEFAULT + RescuerContact.RESCUER_CONTACT_NON_DEFAULT + COMMA + SPACE
            + RescuerContact.COLUMN_RESCUER_ID + SPACE + INTEGER + COMMA
            + CONSTRAINT + "unique_record" + UNIQUE
            + OPEN_BRACE + RescuerContact.COLUMN_RESCUER_ID + COMMA + SPACE
            + RescuerContact.COLUMN_RESCUER_CONTACT_VALUE + CLOSE_BRACE + ON + CONFLICT_REPLACE + COMMA
            + CONSTRAINT + "fk_contact_type_id"
            + FOREIGN_KEY + OPEN_BRACE + RescuerContact.COLUMN_RESCUER_CONTACT_TYPE_ID + CLOSE_BRACE
            + REFERENCES + RescuerContactType.TABLE_NAME + OPEN_BRACE + RescuerContactType._ID + CLOSE_BRACE + COMMA
            + CONSTRAINT + "fk_rescuer_id"
            + FOREIGN_KEY + OPEN_BRACE + RescuerContact.COLUMN_RESCUER_ID + CLOSE_BRACE
            + REFERENCES + Rescuer.TABLE_NAME + OPEN_BRACE + Rescuer._ID + CLOSE_BRACE
            + ON + DELETE_CASCADE
            + CLOSE_BRACE;

    private static final String CREATE_TABLE_ITEM_RESCUER_INFO
            = CREATE_TABLE + AnimalRescuerInfo.TABLE_NAME
            + OPEN_BRACE
            + AnimalRescuerInfo.COLUMN_ITEM_ID + SPACE + INTEGER + COMMA + SPACE
            + AnimalRescuerInfo.COLUMN_RESCUER_ID + SPACE + INTEGER + COMMA + SPACE
            + AnimalRescuerInfo.COLUMN_ITEM_UNIT_PRICE + SPACE + REAL + NOT + NULL + DEFAULT + AnimalRescuerInfo.DEFAULT_ITEM_UNIT_PRICE + COMMA
            + CONSTRAINT + "unique_record" + UNIQUE
            + OPEN_BRACE + AnimalRescuerInfo.COLUMN_ITEM_ID + COMMA + SPACE
            + AnimalRescuerInfo.COLUMN_RESCUER_ID + CLOSE_BRACE + ON + CONFLICT_REPLACE + COMMA
            + CONSTRAINT + "fk_item_id"
            + FOREIGN_KEY + OPEN_BRACE + AnimalRescuerInfo.COLUMN_ITEM_ID + CLOSE_BRACE
            + REFERENCES + Animal.TABLE_NAME + OPEN_BRACE + Animal._ID + CLOSE_BRACE
            + ON + DELETE_CASCADE + COMMA
            + CONSTRAINT + "fk_rescuer_id"
            + FOREIGN_KEY + OPEN_BRACE + AnimalRescuerInfo.COLUMN_RESCUER_ID + CLOSE_BRACE
            + REFERENCES + Rescuer.TABLE_NAME + OPEN_BRACE + Rescuer._ID + CLOSE_BRACE
            + ON + DELETE_CASCADE
            + CLOSE_BRACE;

    private static final String CREATE_TABLE_ITEM_RESCUER_INVENTORY
            = CREATE_TABLE + AnimalRescuerInventory.TABLE_NAME
            + OPEN_BRACE
            + AnimalRescuerInventory.COLUMN_ITEM_ID + SPACE + INTEGER + COMMA + SPACE
            + AnimalRescuerInventory.COLUMN_RESCUER_ID + SPACE + INTEGER + COMMA + SPACE
            + AnimalRescuerInventory.COLUMN_ITEM_AVAIL_QUANTITY + SPACE + INTEGER + NOT + NULL + DEFAULT + AnimalRescuerInventory.DEFAULT_ITEM_AVAIL_QUANTITY + COMMA
            + CONSTRAINT + "unique_record" + UNIQUE
            + OPEN_BRACE + AnimalRescuerInventory.COLUMN_ITEM_ID + COMMA + SPACE
            + AnimalRescuerInventory.COLUMN_RESCUER_ID + CLOSE_BRACE + ON + CONFLICT_REPLACE + COMMA
            + CONSTRAINT + "fk_item_id"
            + FOREIGN_KEY + OPEN_BRACE + AnimalRescuerInventory.COLUMN_ITEM_ID + CLOSE_BRACE
            + REFERENCES + Animal.TABLE_NAME + OPEN_BRACE + Animal._ID + CLOSE_BRACE
            + ON + DELETE_CASCADE + COMMA
            + CONSTRAINT + "fk_rescuer_id"
            + FOREIGN_KEY + OPEN_BRACE + AnimalRescuerInventory.COLUMN_RESCUER_ID + CLOSE_BRACE
            + REFERENCES + Rescuer.TABLE_NAME + OPEN_BRACE + Rescuer._ID + CLOSE_BRACE
            + ON + DELETE_CASCADE
            + CLOSE_BRACE;


    private static final String CREATE_INDEX_RESCUER_QUANTITY
            = CREATE_INDEX + "quantity_idx" + ON + AnimalRescuerInventory.TABLE_NAME
            + SPACE + OPEN_BRACE + AnimalRescuerInventory.COLUMN_ITEM_AVAIL_QUANTITY + CLOSE_BRACE;

    private static volatile StoreDbHelper INSTANCE;

    private StoreDbHelper(Context context) {

        super(context,
                DATABASE_NAME,
                new AppCursorFactory(),
                DATABASE_VERSION
        );
    }

    public static synchronized StoreDbHelper getInstance(Context context) {
        if (INSTANCE == null) {

            synchronized (StoreDbHelper.class) {

                if (INSTANCE == null) {


                    INSTANCE = new StoreDbHelper(context.getApplicationContext());
                }
            }
        }

        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_ITEM_CATEGORY);
        db.execSQL(CREATE_TABLE_ITEM_IMAGE);
        db.execSQL(CREATE_TABLE_ITEM_ATTR);
        db.execSQL(CREATE_TABLE_RESCUER);
        db.execSQL(CREATE_TABLE_RESCUER_CONTACT_TYPE);
        db.execSQL(CREATE_TABLE_RESCUER_CONTACT);
        db.execSQL(CREATE_TABLE_ITEM_RESCUER_INFO);
        db.execSQL(CREATE_TABLE_ITEM_RESCUER_INVENTORY);



        db.execSQL(CREATE_INDEX_RESCUER_QUANTITY);


        insertPredefinedCategories();


        insertPredefinedContactTypes();
    }

    private void insertPredefinedCategories() {

        AppExecutors.getInstance().getDiskIO().execute(() -> {

            String[] preloadedCategories = AnimalCategory.getPreloadedCategories();


            SQLiteDatabase writableDatabase = INSTANCE.getWritableDatabase();


            int noOfRecordsInserted = 0;


            writableDatabase.beginTransaction();
            try {

                for (String categoryName : preloadedCategories) {

                    ContentValues categoryContentValues = new ContentValues();
                    categoryContentValues.put(AnimalCategory.COLUMN_ITEM_CATEGORY_NAME, categoryName);


                    long recordId = writableDatabase.insert(
                            AnimalCategory.TABLE_NAME,
                            null,
                            categoryContentValues
                    );


                    if (recordId == -1) {

                        break;
                    } else {



                        noOfRecordsInserted++;
                    }
                }
            } finally {


                if (noOfRecordsInserted == preloadedCategories.length) {

                    writableDatabase.setTransactionSuccessful();
                    Log.i(LOG_TAG, "insertPredefinedCategories: Predefined Categories inserted");
                } else {

                    Log.e(LOG_TAG, "insertPredefinedCategories: Predefined Categories failed to insert.");
                }


                writableDatabase.endTransaction();
            }

        });
    }

    private void insertPredefinedContactTypes() {

        AppExecutors.getInstance().getDiskIO().execute(() -> {

            String[] preloadedContactTypes = RescuerContactType.getPreloadedContactTypes();


            SQLiteDatabase writableDatabase = INSTANCE.getWritableDatabase();


            int noOfRecordsInserted = 0;


            int noOfContactTypes = preloadedContactTypes.length;


            writableDatabase.beginTransaction();
            try {

                for (int index = 0; index < noOfContactTypes; index++) {

                    ContentValues contactTypeContentValues = new ContentValues();
                    contactTypeContentValues.put(RescuerContactType._ID, String.valueOf(index));
                    contactTypeContentValues.put(RescuerContactType.COLUMN_CONTACT_TYPE_NAME, preloadedContactTypes[index]);


                    long recordId = writableDatabase.insert(
                            RescuerContactType.TABLE_NAME,
                            null,
                            contactTypeContentValues
                    );


                    if (recordId == -1) {

                        break;
                    } else {



                        noOfRecordsInserted++;
                    }
                }
            } finally {


                if (noOfRecordsInserted == noOfContactTypes) {

                    writableDatabase.setTransactionSuccessful();
                    Log.i(LOG_TAG, "insertPredefinedContactTypes: Predefined Contact types inserted");
                } else {

                    Log.e(LOG_TAG, "insertPredefinedContactTypes: Predefined Contact types failed to insert.");
                }


                writableDatabase.endTransaction();
            }

        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



        db.execSQL("DROP TABLE IF EXISTS " + Animal.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AnimalCategory.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AnimalImage.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AnimalAttribute.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Rescuer.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RescuerContactType.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RescuerContact.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AnimalRescuerInfo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AnimalRescuerInventory.TABLE_NAME);


        db.execSQL("DROP INDEX quantity_idx");


        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            db.setForeignKeyConstraintsEnabled(true);
        } else {


            String foreignKeyPragmaStr = "PRAGMA foreign_keys = ON";
            db.execSQL(foreignKeyPragmaStr);
        }
    }

    private static class AppCursorFactory implements SQLiteDatabase.CursorFactory {

        @Override
        public Cursor newCursor(SQLiteDatabase db,
                                SQLiteCursorDriver masterQuery,
                                String editTable, SQLiteQuery query) {


            if (AppConstants.LOG_CURSOR_QUERIES) {
                Log.i(LOG_TAG, "Table: " + editTable);
                Log.i(LOG_TAG, "newCursor: " + query.toString());
            }


            return new SQLiteCursor(masterQuery, editTable, query);
        }
    }
}