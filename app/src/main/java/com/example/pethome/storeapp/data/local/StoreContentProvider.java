

package com.example.pethome.storeapp.data.local;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import com.example.pethome.storeapp.data.local.contracts.AnimalContract;
import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract;
import com.example.pethome.storeapp.data.local.contracts.StoreContract;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract;
import com.example.pethome.storeapp.data.local.utils.QueryArgsUtility;

import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.AND;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.EQUALS;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.PLACEHOLDER;
import static com.example.pethome.storeapp.data.local.utils.SqliteUtility.combineSelectionPairs;

public class StoreContentProvider extends ContentProvider {


    private static final String LOG_TAG = StoreContentProvider.class.getSimpleName();

    private static final int ITEMS = 10;
    private static final int ITEM_SHORT_INFO = 11;
    private static final int ITEM_ID = 12;
    private static final int ITEM_BY_SKU = 13;
    private static final int ITEM_ATTRS_ID = 14;
    private static final int ITEM_IMAGES_ID = 15;

    private static final int CATEGORIES = 20;
    private static final int CATEGORY_BY_ID = 21;
    private static final int CATEGORY_BY_NAME = 22;

    private static final int RESCUERS = 30;
    private static final int RESCUER_SHORT_INFO = 31;
    private static final int RESCUER_ID = 32;
    private static final int RESCUER_BY_CODE = 33;
    private static final int RESCUER_CONTACTS_ID = 34;

    private static final int RESCUER_ITEMS_ID = 41;
    private static final int ITEM_RESCUERS_ID = 42;

    private static final int ADOPTIONS_SHORT_INFO = 50;
    private static final int ADOPTIONS_INVENTORY_ITEM_ID = 51;
    private static final int ADOPTIONS_INVENTORY_RESCUER_ID = 52;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private StoreDbHelper mDbHelper;
    private static UriMatcher buildUriMatcher() {

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);


        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AnimalContract.PATH_ITEM, ITEMS);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AnimalContract.PATH_ITEM + "/" + AnimalContract.Animal.PATH_SHORT_INFO,
                ITEM_SHORT_INFO);


        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AnimalContract.PATH_ITEM + "/#", ITEM_ID);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AnimalContract.PATH_ITEM + "/" + AnimalContract.Animal.PATH_ITEM_SKU + "/*",
                ITEM_BY_SKU);


        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AnimalContract.PATH_ITEM + "/" + AnimalContract.PATH_ITEM_ATTR + "/#",
                ITEM_ATTRS_ID);


        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AnimalContract.PATH_ITEM + "/" + AnimalContract.PATH_ITEM_IMAGE + "/#",
                ITEM_IMAGES_ID);


        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AnimalContract.PATH_CATEGORY, CATEGORIES);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AnimalContract.PATH_CATEGORY + "/#", CATEGORY_BY_ID);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AnimalContract.PATH_CATEGORY + "/*", CATEGORY_BY_NAME);


        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                RescuerContract.PATH_RESCUER, RESCUERS);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                RescuerContract.PATH_RESCUER + "/" + RescuerContract.Rescuer.PATH_SHORT_INFO,
                RESCUER_SHORT_INFO);


        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                RescuerContract.PATH_RESCUER + "/#", RESCUER_ID);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                RescuerContract.PATH_RESCUER + "/" + RescuerContract.Rescuer.PATH_RESCUER_CODE + "/*",
                RESCUER_BY_CODE);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                RescuerContract.PATH_RESCUER + "/" + RescuerContract.PATH_RESCUER_CONTACT + "/#",
                RESCUER_CONTACTS_ID);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AdoptionsContract.PATH_ITEM_RESCUER_INFO + "/" + RescuerContract.PATH_RESCUER + "/#",
                RESCUER_ITEMS_ID);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AdoptionsContract.PATH_ITEM_RESCUER_INFO + "/" + AnimalContract.PATH_ITEM + "/#",
                ITEM_RESCUERS_ID);

        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AdoptionsContract.PATH_ITEM_RESCUER_INVENTORY + "/" + AdoptionsContract.AnimalRescuerInventory.PATH_SHORT_INFO,
                ADOPTIONS_SHORT_INFO);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AdoptionsContract.PATH_ITEM_RESCUER_INVENTORY + "/" + AnimalContract.PATH_ITEM + "/#",
                ADOPTIONS_INVENTORY_ITEM_ID);



        matcher.addURI(StoreContract.CONTENT_AUTHORITY,
                AdoptionsContract.PATH_ITEM_RESCUER_INVENTORY + "/" + RescuerContract.PATH_RESCUER + "/#",
                ADOPTIONS_INVENTORY_RESCUER_ID);


        return matcher;
    }

    @Override
    public boolean onCreate() {

        mDbHelper = StoreDbHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable Bundle queryArgs, @Nullable CancellationSignal cancellationSignal) {

        return super.query(uri, projection, queryArgs, cancellationSignal);
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase readableDatabase = mDbHelper.getReadableDatabase();


        Cursor retCursor;


        switch (sUriMatcher.match(uri)) {
            case ITEM_ATTRS_ID:

                retCursor = getItemAttributes(uri, readableDatabase, projection);
                break;
            case ITEM_IMAGES_ID:

                retCursor = getItemImages(uri, readableDatabase, projection);
                break;
            case ITEM_ID:

                retCursor = getItemDetails(uri, readableDatabase, projection);
                break;
            case ITEM_BY_SKU:

                retCursor = getItemBySku(uri, readableDatabase, projection);
                break;
            case ITEM_SHORT_INFO:

                retCursor = getItemsWithShortInfo(readableDatabase, projection, selection, selectionArgs, sortOrder);
                break;
            case CATEGORIES:

                retCursor = getCategories(readableDatabase, projection, sortOrder);
                break;
            case CATEGORY_BY_ID:

                retCursor = getCategoryById(uri, readableDatabase, projection);
                break;
            case CATEGORY_BY_NAME:

                retCursor = getCategoryByName(uri, readableDatabase, projection);
                break;
            case RESCUER_ID:

                retCursor = getRescuerDetails(uri, readableDatabase, projection);
                break;
            case RESCUER_BY_CODE:

                retCursor = getRescuerByCode(uri, readableDatabase, projection);
                break;
            case RESCUER_CONTACTS_ID:

                retCursor = getRescuerContacts(uri, readableDatabase, projection);
                break;
            case RESCUER_ITEMS_ID:

                retCursor = getRescuerItems(uri, readableDatabase, projection);
                break;
            case ITEM_RESCUERS_ID:

                retCursor = getItemRescuers(uri, readableDatabase, projection);
                break;
            case RESCUER_SHORT_INFO:

                retCursor = getRescuersWithShortInfo(readableDatabase, projection, selection, selectionArgs, sortOrder);
                break;
            case ADOPTIONS_SHORT_INFO:

                retCursor = getAdoptionsWithShortInfo(readableDatabase, projection, selection, selectionArgs, sortOrder);
                break;
            case ADOPTIONS_INVENTORY_ITEM_ID:

                retCursor = getItemRescuersAdoptionsInfo(uri, readableDatabase, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Cannot query unknown URI " + uri);
        }



        retCursor.setNotificationUri(getContext().getContentResolver(), uri);


        return retCursor;
    }

    private Cursor getItemBySku(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {
        return readableDatabase.query(
                AnimalContract.Animal.TABLE_NAME,
                projection,

                QueryArgsUtility.ItemBySkuQuery.getSelection(),

                QueryArgsUtility.ItemBySkuQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getItemsWithShortInfo(SQLiteDatabase readableDatabase, String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        QueryArgsUtility.ItemsShortInfoQuery.setTables(queryBuilder);

        QueryArgsUtility.ItemsShortInfoQuery.setProjectionMap(queryBuilder);

        Pair<String, String[]> selectionPairs = Pair.create(
                QueryArgsUtility.ItemsShortInfoQuery.getSelection(),
                QueryArgsUtility.ItemsShortInfoQuery.getSelectionArgs());

        if (!TextUtils.isEmpty(selection)) {

            selectionPairs = combineSelectionPairs(
                    selectionPairs,
                    Pair.create(selection, selectionArgs),
                    AND
            );
        }


        return queryBuilder.query(
                readableDatabase,
                projection,

                selectionPairs != null ? selectionPairs.first : null,

                selectionPairs != null ? selectionPairs.second : null,
                null,
                null,
                sortOrder
        );
    }


    private Cursor getItemDetails(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        QueryArgsUtility.ItemByIdQuery.setTables(queryBuilder);

        QueryArgsUtility.ItemByIdQuery.setProjectionMap(queryBuilder);


        return queryBuilder.query(
                readableDatabase,
                projection,

                QueryArgsUtility.ItemByIdQuery.getSelection(),

                QueryArgsUtility.ItemByIdQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getCategoryById(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {
        return readableDatabase.query(
                AnimalContract.AnimalCategory.TABLE_NAME,
                projection,

                QueryArgsUtility.CategoryByIdQuery.getSelection(),

                QueryArgsUtility.CategoryByIdQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getCategoryByName(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {
        return readableDatabase.query(
                AnimalContract.AnimalCategory.TABLE_NAME,
                projection,

                QueryArgsUtility.CategoryByNameQuery.getSelection(),

                QueryArgsUtility.CategoryByNameQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getCategories(SQLiteDatabase readableDatabase, @Nullable String[] projection, String sortOrder) {
        return readableDatabase.query(
                AnimalContract.AnimalCategory.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getItemImages(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        QueryArgsUtility.ItemImagesQuery.setTables(queryBuilder);

        QueryArgsUtility.ItemImagesQuery.setProjectionMap(queryBuilder);


        return queryBuilder.query(
                readableDatabase,
                projection,

                QueryArgsUtility.ItemImagesQuery.getSelection(),

                QueryArgsUtility.ItemImagesQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getItemAttributes(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        QueryArgsUtility.ItemAttributesQuery.setTables(queryBuilder);

        QueryArgsUtility.ItemAttributesQuery.setProjectionMap(queryBuilder);


        return queryBuilder.query(
                readableDatabase,
                projection,

                QueryArgsUtility.ItemAttributesQuery.getSelection(),

                QueryArgsUtility.ItemAttributesQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getRescuerDetails(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {
        return readableDatabase.query(
                RescuerContract.Rescuer.TABLE_NAME,
                projection,

                QueryArgsUtility.RescuerByIdQuery.getSelection(),

                QueryArgsUtility.RescuerByIdQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getRescuerByCode(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {
        return readableDatabase.query(
                RescuerContract.Rescuer.TABLE_NAME,
                projection,

                QueryArgsUtility.RescuerByCodeQuery.getSelection(),

                QueryArgsUtility.RescuerByCodeQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getRescuerItems(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {
        return readableDatabase.query(
                AdoptionsContract.AnimalRescuerInfo.TABLE_NAME,
                projection,

                QueryArgsUtility.RescuerItemsQuery.getSelection(),

                QueryArgsUtility.RescuerItemsQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getItemRescuers(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {
        return readableDatabase.query(
                AdoptionsContract.AnimalRescuerInfo.TABLE_NAME,
                projection,

                QueryArgsUtility.ItemRescuersQuery.getSelection(),

                QueryArgsUtility.ItemRescuersQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getRescuerContacts(Uri uri, SQLiteDatabase readableDatabase, String[] projection) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        QueryArgsUtility.RescuerContactsQuery.setTables(queryBuilder);

        QueryArgsUtility.RescuerContactsQuery.setProjectionMap(queryBuilder);


        return queryBuilder.query(
                readableDatabase,
                projection,

                QueryArgsUtility.RescuerContactsQuery.getSelection(),

                QueryArgsUtility.RescuerContactsQuery.getSelectionArgs(uri),
                null,
                null,
                null
        );
    }

    private Cursor getRescuersWithShortInfo(SQLiteDatabase readableDatabase, String[] projection,
                                             String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        QueryArgsUtility.RescuersShortInfoQuery.setTables(queryBuilder);

        QueryArgsUtility.RescuersShortInfoQuery.setProjectionMap(queryBuilder);


        return queryBuilder.query(
                readableDatabase,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getAdoptionsWithShortInfo(SQLiteDatabase readableDatabase, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        QueryArgsUtility.AdoptionsShortInfoQuery.setTables(queryBuilder);

        QueryArgsUtility.AdoptionsShortInfoQuery.setProjectionMap(queryBuilder);

        Pair<String, String[]> selectionPairs = Pair.create(
                QueryArgsUtility.AdoptionsShortInfoQuery.getSelection(),
                QueryArgsUtility.AdoptionsShortInfoQuery.getSelectionArgs());

        if (!TextUtils.isEmpty(selection)) {

            selectionPairs = combineSelectionPairs(
                    selectionPairs,
                    Pair.create(selection, selectionArgs),
                    AND
            );
        }


        return queryBuilder.query(
                readableDatabase,
                projection,

                selectionPairs != null ? selectionPairs.first : null,

                selectionPairs != null ? selectionPairs.second : null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getItemRescuersAdoptionsInfo(Uri uri, SQLiteDatabase readableDatabase, String[] projection, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        QueryArgsUtility.ItemRescuersAdoptionsQuery.setTables(queryBuilder);

        QueryArgsUtility.ItemRescuersAdoptionsQuery.setProjectionMap(queryBuilder);


        return queryBuilder.query(
                readableDatabase,
                projection,

                QueryArgsUtility.ItemRescuersAdoptionsQuery.getSelection(),

                QueryArgsUtility.ItemRescuersAdoptionsQuery.getSelectionArgs(uri),
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        if (values == null || values.size() == 0) {
            throw new IllegalArgumentException("Empty values");
        }


        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();


        Uri returnUri;


        writableDatabase.beginTransaction();
        try {

            switch (sUriMatcher.match(uri)) {
                case CATEGORIES:

                    returnUri = insertWithConflictFail(uri,
                            AnimalContract.AnimalCategory.TABLE_NAME,
                            writableDatabase,
                            values
                    );
                    break;
                case ITEMS:

                    returnUri = insertWithConflictFail(uri,
                            AnimalContract.Animal.TABLE_NAME,
                            writableDatabase,
                            values
                    );
                    break;
                case RESCUERS:

                    returnUri = insertWithConflictFail(uri,
                            RescuerContract.Rescuer.TABLE_NAME,
                            writableDatabase,
                            values
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unknown/Unsupported uri: " + uri);
            }
        } finally {

            writableDatabase.endTransaction();
        }


        return returnUri;
    }


    @Nullable
    private Uri insertWithConflictFail(Uri uri, String tableName, SQLiteDatabase writableDatabase, ContentValues values) {

        Uri returnUri = null;

        try {

            long recordId = writableDatabase.insertOrThrow(
                    tableName,
                    null,
                    values
            );


            if (recordId == -1) {

                Log.e(LOG_TAG, "insertWithConflictFail: " + tableName + ": Failed to insert row for " + uri);
            } else {



                writableDatabase.setTransactionSuccessful();



                getContext().getContentResolver().notifyChange(uri, null);


                returnUri = ContentUris.withAppendedId(uri, recordId);
            }
        } catch (SQLiteConstraintException e) {

            Log.e(LOG_TAG, "insertWithConflictFail: " + tableName + ": Record already exists for " + uri, e);
        } catch (SQLException e) {


            Log.e(LOG_TAG, "insertWithConflictFail: " + tableName + ": Failed to insert row for " + uri, e);
        }


        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        if (values.length == 0) {

            return super.bulkInsert(uri, values);
        }


        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();


        int noOfRecordsInserted;


        switch (sUriMatcher.match(uri)) {
            case ITEM_ATTRS_ID:



                noOfRecordsInserted = bulkInsertHangOffTable(uri,
                        AnimalContract.AnimalAttribute.TABLE_NAME,
                        writableDatabase,
                        values,
                        true
                );
                break;
            case ITEM_IMAGES_ID:



                noOfRecordsInserted = bulkInsertHangOffTable(uri,
                        AnimalContract.AnimalImage.TABLE_NAME,
                        writableDatabase,
                        values,
                        true
                );
                break;
            case RESCUER_CONTACTS_ID:




                noOfRecordsInserted = bulkInsertHangOffTable(uri,
                        RescuerContract.RescuerContact.TABLE_NAME,
                        writableDatabase,
                        values,
                        false
                );
                break;
            case RESCUER_ITEMS_ID:
            case ITEM_RESCUERS_ID:




                noOfRecordsInserted = bulkInsertHangOffTable(uri,
                        AdoptionsContract.AnimalRescuerInfo.TABLE_NAME,
                        writableDatabase,
                        values,
                        false
                );
                break;
            case ADOPTIONS_INVENTORY_ITEM_ID:
            case ADOPTIONS_INVENTORY_RESCUER_ID:




                noOfRecordsInserted = bulkInsertHangOffTable(uri,
                        AdoptionsContract.AnimalRescuerInventory.TABLE_NAME,
                        writableDatabase,
                        values,
                        false
                );
                break;
            default:
                noOfRecordsInserted = super.bulkInsert(uri, values);
        }


        return noOfRecordsInserted;
    }

    private int bulkInsertHangOffTable(Uri uri, String tableName, SQLiteDatabase writableDatabase,
                                       ContentValues[] values, boolean deleteAllFirst) {

        if (deleteAllFirst) {



            delete(uri, null, null);
        }


        int noOfRecordsInserted = 0;


        writableDatabase.beginTransaction();
        try {

            switch (sUriMatcher.match(uri)) {
                case ITEM_ATTRS_ID:

                {

                    final long itemId = ContentUris.parseId(uri);


                    for (ContentValues contentValue : values) {

                        contentValue.put(AnimalContract.AnimalAttribute.COLUMN_ITEM_ID, itemId);


                        if (insertBulkRecord(uri, tableName, writableDatabase, contentValue)) {

                            noOfRecordsInserted++;
                        }
                    }
                }
                break;
                case ITEM_IMAGES_ID:

                {

                    final long itemId = ContentUris.parseId(uri);


                    for (ContentValues contentValue : values) {

                        contentValue.put(AnimalContract.AnimalImage.COLUMN_ITEM_ID, itemId);


                        if (insertBulkRecord(uri, tableName, writableDatabase, contentValue)) {

                            noOfRecordsInserted++;
                        }
                    }
                }
                break;
                case RESCUER_CONTACTS_ID:

                {

                    final long rescuerId = ContentUris.parseId(uri);


                    for (ContentValues contentValue : values) {

                        contentValue.put(RescuerContract.RescuerContact.COLUMN_RESCUER_ID, rescuerId);


                        if (insertBulkRecord(uri, tableName, writableDatabase, contentValue)) {

                            noOfRecordsInserted++;
                        }
                    }
                }
                break;
                case RESCUER_ITEMS_ID:

                {

                    final long rescuerId = ContentUris.parseId(uri);


                    for (ContentValues contentValue : values) {

                        contentValue.put(AdoptionsContract.AnimalRescuerInfo.COLUMN_RESCUER_ID, rescuerId);


                        if (insertBulkRecord(uri, tableName, writableDatabase, contentValue)) {

                            noOfRecordsInserted++;
                        }
                    }
                }
                break;
                case ITEM_RESCUERS_ID:

                {

                    final long itemId = ContentUris.parseId(uri);


                    for (ContentValues contentValue : values) {

                        contentValue.put(AdoptionsContract.AnimalRescuerInfo.COLUMN_ITEM_ID, itemId);


                        if (insertBulkRecord(uri, tableName, writableDatabase, contentValue)) {

                            noOfRecordsInserted++;
                        }
                    }
                }
                break;
                case ADOPTIONS_INVENTORY_ITEM_ID:

                {

                    final long itemId = ContentUris.parseId(uri);


                    for (ContentValues contentValue : values) {

                        contentValue.put(AdoptionsContract.AnimalRescuerInventory.COLUMN_ITEM_ID, itemId);


                        if (insertBulkRecord(uri, tableName, writableDatabase, contentValue)) {

                            noOfRecordsInserted++;
                        }
                    }
                }
                break;
                case ADOPTIONS_INVENTORY_RESCUER_ID:

                {

                    final long rescuerId = ContentUris.parseId(uri);


                    for (ContentValues contentValue : values) {

                        contentValue.put(AdoptionsContract.AnimalRescuerInventory.COLUMN_RESCUER_ID, rescuerId);


                        if (insertBulkRecord(uri, tableName, writableDatabase, contentValue)) {

                            noOfRecordsInserted++;
                        }
                    }
                }
                break;
            }
        } finally {

            if (noOfRecordsInserted == values.length) {

                writableDatabase.setTransactionSuccessful();
            }


            writableDatabase.endTransaction();
        }


        return noOfRecordsInserted;
    }

    private boolean insertBulkRecord(Uri uri, String tableName, SQLiteDatabase writableDatabase, ContentValues contentValue) {

        long recordId = writableDatabase.insert(tableName,
                null, contentValue);


        if (recordId == -1) {

            Log.e(LOG_TAG, "insertBulkRecord: " + tableName + ": Failed to insert row for " + uri);

            return false;
        } else {




            getContext().getContentResolver().notifyChange(uri, null);


            return true;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();


        int noOfRecordsDeleted;


        writableDatabase.beginTransaction();
        try {

            switch (sUriMatcher.match(uri)) {
                case ITEM_ATTRS_ID:




                    selection = AnimalContract.AnimalAttribute.COLUMN_ITEM_ID + EQUALS + PLACEHOLDER;
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    noOfRecordsDeleted = writableDatabase.delete(
                            AnimalContract.AnimalAttribute.TABLE_NAME,
                            selection,
                            selectionArgs
                    );

                    writableDatabase.setTransactionSuccessful();
                    break;

                case ITEM_IMAGES_ID:




                    selection = AnimalContract.AnimalImage.COLUMN_ITEM_ID + EQUALS + PLACEHOLDER;
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    noOfRecordsDeleted = writableDatabase.delete(
                            AnimalContract.AnimalImage.TABLE_NAME,
                            selection,
                            selectionArgs
                    );

                    writableDatabase.setTransactionSuccessful();
                    break;

                case ITEM_ID:




                    selection = AnimalContract.Animal._ID + EQUALS + PLACEHOLDER;
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    noOfRecordsDeleted = writableDatabase.delete(
                            AnimalContract.Animal.TABLE_NAME,
                            selection,
                            selectionArgs
                    );

                    writableDatabase.setTransactionSuccessful();
                    break;

                case RESCUER_ID:




                    selection = RescuerContract.Rescuer._ID + EQUALS + PLACEHOLDER;
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    noOfRecordsDeleted = writableDatabase.delete(
                            RescuerContract.Rescuer.TABLE_NAME,
                            selection,
                            selectionArgs
                    );

                    writableDatabase.setTransactionSuccessful();
                    break;

                case RESCUER_CONTACTS_ID:


                {

                    String selection1 = RescuerContract.RescuerContact.COLUMN_RESCUER_ID + EQUALS + PLACEHOLDER;
                    String[] selectionArgs1 = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    Pair<String, String[]> selectionPairs = Pair.create(
                            selection1,
                            selectionArgs1);

                    if (!TextUtils.isEmpty(selection)) {

                        selectionPairs = combineSelectionPairs(
                                selectionPairs,
                                Pair.create(selection, selectionArgs),
                                AND
                        );
                    }


                    noOfRecordsDeleted = writableDatabase.delete(
                            RescuerContract.RescuerContact.TABLE_NAME,
                            selectionPairs != null ? selectionPairs.first : null,
                            selectionPairs != null ? selectionPairs.second : null
                    );

                    writableDatabase.setTransactionSuccessful();
                }
                break;

                case RESCUER_ITEMS_ID:


                {

                    String selection1 = AdoptionsContract.AnimalRescuerInfo.COLUMN_RESCUER_ID + EQUALS + PLACEHOLDER;
                    String[] selectionArgs1 = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    Pair<String, String[]> selectionPairs = Pair.create(
                            selection1,
                            selectionArgs1);

                    if (!TextUtils.isEmpty(selection)) {

                        selectionPairs = combineSelectionPairs(
                                selectionPairs,
                                Pair.create(selection, selectionArgs),
                                AND
                        );
                    }


                    noOfRecordsDeleted = writableDatabase.delete(
                            AdoptionsContract.AnimalRescuerInfo.TABLE_NAME,
                            selectionPairs != null ? selectionPairs.first : null,
                            selectionPairs != null ? selectionPairs.second : null
                    );

                    writableDatabase.setTransactionSuccessful();
                }
                break;

                case ITEM_RESCUERS_ID:


                {

                    String selection1 = AdoptionsContract.AnimalRescuerInfo.COLUMN_ITEM_ID + EQUALS + PLACEHOLDER;
                    String[] selectionArgs1 = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    Pair<String, String[]> selectionPairs = Pair.create(
                            selection1,
                            selectionArgs1);

                    if (!TextUtils.isEmpty(selection)) {

                        selectionPairs = combineSelectionPairs(
                                selectionPairs,
                                Pair.create(selection, selectionArgs),
                                AND
                        );
                    }


                    noOfRecordsDeleted = writableDatabase.delete(
                            AdoptionsContract.AnimalRescuerInfo.TABLE_NAME,
                            selectionPairs != null ? selectionPairs.first : null,
                            selectionPairs != null ? selectionPairs.second : null
                    );

                    writableDatabase.setTransactionSuccessful();
                }
                break;

                case ADOPTIONS_INVENTORY_ITEM_ID:


                {

                    String selection1 = AdoptionsContract.AnimalRescuerInventory.COLUMN_ITEM_ID + EQUALS + PLACEHOLDER;
                    String[] selectionArgs1 = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    Pair<String, String[]> selectionPairs = Pair.create(
                            selection1,
                            selectionArgs1);

                    if (!TextUtils.isEmpty(selection)) {

                        selectionPairs = combineSelectionPairs(
                                selectionPairs,
                                Pair.create(selection, selectionArgs),
                                AND
                        );
                    }


                    noOfRecordsDeleted = writableDatabase.delete(
                            AdoptionsContract.AnimalRescuerInventory.TABLE_NAME,
                            selectionPairs != null ? selectionPairs.first : null,
                            selectionPairs != null ? selectionPairs.second : null
                    );

                    writableDatabase.setTransactionSuccessful();
                }
                break;

                case ADOPTIONS_INVENTORY_RESCUER_ID:


                {

                    String selection1 = AdoptionsContract.AnimalRescuerInventory.COLUMN_RESCUER_ID + EQUALS + PLACEHOLDER;
                    String[] selectionArgs1 = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    Pair<String, String[]> selectionPairs = Pair.create(
                            selection1,
                            selectionArgs1);

                    if (!TextUtils.isEmpty(selection)) {

                        selectionPairs = combineSelectionPairs(
                                selectionPairs,
                                Pair.create(selection, selectionArgs),
                                AND
                        );
                    }


                    noOfRecordsDeleted = writableDatabase.delete(
                            AdoptionsContract.AnimalRescuerInventory.TABLE_NAME,
                            selectionPairs != null ? selectionPairs.first : null,
                            selectionPairs != null ? selectionPairs.second : null
                    );

                    writableDatabase.setTransactionSuccessful();
                }
                break;

                default:
                    throw new IllegalArgumentException("Delete operation is not supported for " + uri);
            }
        } finally {

            writableDatabase.endTransaction();
        }

        if (noOfRecordsDeleted > 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }


        return noOfRecordsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        if (values == null || values.size() == 0) {
            throw new IllegalArgumentException("Empty values");
        }


        SQLiteDatabase writableDatabase = mDbHelper.getWritableDatabase();


        int noOfRecordsUpdated;


        writableDatabase.beginTransaction();
        try {

            switch (sUriMatcher.match(uri)) {
                case ITEM_ID:




                    selection = AnimalContract.Animal._ID + EQUALS + PLACEHOLDER;
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    noOfRecordsUpdated = writableDatabase.update(
                            AnimalContract.Animal.TABLE_NAME,
                            values,
                            selection,
                            selectionArgs
                    );

                    writableDatabase.setTransactionSuccessful();
                    break;

                case RESCUER_ID:




                    selection = RescuerContract.Rescuer._ID + EQUALS + PLACEHOLDER;
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    noOfRecordsUpdated = writableDatabase.update(
                            RescuerContract.Rescuer.TABLE_NAME,
                            values,
                            selection,
                            selectionArgs
                    );

                    writableDatabase.setTransactionSuccessful();
                    break;

                case ADOPTIONS_INVENTORY_ITEM_ID:


                {

                    String selection1 = AdoptionsContract.AnimalRescuerInventory.COLUMN_ITEM_ID + EQUALS + PLACEHOLDER;
                    String[] selectionArgs1 = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    Pair<String, String[]> selectionPairs = Pair.create(
                            selection1,
                            selectionArgs1);

                    if (!TextUtils.isEmpty(selection)) {

                        selectionPairs = combineSelectionPairs(
                                selectionPairs,
                                Pair.create(selection, selectionArgs),
                                AND
                        );
                    }


                    noOfRecordsUpdated = writableDatabase.update(
                            AdoptionsContract.AnimalRescuerInventory.TABLE_NAME,
                            values,
                            selectionPairs != null ? selectionPairs.first : null,
                            selectionPairs != null ? selectionPairs.second : null
                    );

                    writableDatabase.setTransactionSuccessful();
                }
                break;

                case ADOPTIONS_INVENTORY_RESCUER_ID:


                {

                    String selection1 = AdoptionsContract.AnimalRescuerInventory.COLUMN_RESCUER_ID + EQUALS + PLACEHOLDER;
                    String[] selectionArgs1 = new String[]{String.valueOf(ContentUris.parseId(uri))};

                    Pair<String, String[]> selectionPairs = Pair.create(
                            selection1,
                            selectionArgs1);

                    if (!TextUtils.isEmpty(selection)) {

                        selectionPairs = combineSelectionPairs(
                                selectionPairs,
                                Pair.create(selection, selectionArgs),
                                AND
                        );
                    }


                    noOfRecordsUpdated = writableDatabase.update(
                            AdoptionsContract.AnimalRescuerInventory.TABLE_NAME,
                            values,
                            selectionPairs != null ? selectionPairs.first : null,
                            selectionPairs != null ? selectionPairs.second : null
                    );

                    writableDatabase.setTransactionSuccessful();
                }
                break;

                default:
                    throw new IllegalArgumentException("Update operation is not supported for " + uri);
            }
        } finally {

            writableDatabase.endTransaction();
        }

        if (noOfRecordsUpdated > 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }


        return noOfRecordsUpdated;
    }
}
