

package com.example.pethome.storeapp.data.local;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.pethome.storeapp.data.local.contracts.AnimalContract;
import com.example.pethome.storeapp.data.local.contracts.AdoptionsContract;
import com.example.pethome.storeapp.data.local.contracts.RescuerContract;
import com.example.pethome.storeapp.data.local.utils.QueryArgsUtility;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class LoaderProvider {


    public static final int ANIMALS_LIST_TYPE = 0;
    public static final int RESCUER_LIST_TYPE = 1;
    public static final int ADOPTIONS_LIST_TYPE = 2;

    private static volatile LoaderProvider INSTANCE;

    private final WeakReference<Context> mContextWeakReference;

    private LoaderProvider(@NonNull Context context) {
        mContextWeakReference = new WeakReference<>(context);
    }

    public static LoaderProvider getInstance(@NonNull Context context) {
        if (INSTANCE == null) {

            synchronized (LoaderProvider.class) {

                if (INSTANCE == null) {

                    INSTANCE = new LoaderProvider(context);
                }
            }
        }

        return INSTANCE;
    }

    public Loader<Cursor> createCursorLoader(@LoadersTypeDef int loaderType) {

        Context context = mContextWeakReference.get();

        if (context == null) {
            return null;
        }


        switch (loaderType) {
            case ANIMALS_LIST_TYPE:

                return new CursorLoader(
                        context,
                        AnimalContract.Animal.CONTENT_URI_SHORT_INFO,
                        QueryArgsUtility.ItemsShortInfoQuery.getProjection(),
                        null,
                        null,
                        AnimalContract.Animal.getQualifiedColumnName(AnimalContract.Animal.COLUMN_ITEM_SKU)
                );
            case RESCUER_LIST_TYPE:

                return new CursorLoader(
                        context,
                        RescuerContract.Rescuer.CONTENT_URI_SHORT_INFO,
                        QueryArgsUtility.RescuersShortInfoQuery.getProjection(),
                        null,
                        null,
                        RescuerContract.Rescuer.COLUMN_RESCUER_CODE
                );
            case ADOPTIONS_LIST_TYPE:

                return new CursorLoader(
                        context,
                        AdoptionsContract.AnimalRescuerInventory.CONTENT_URI_SHORT_INFO,
                        QueryArgsUtility.AdoptionsShortInfoQuery.getProjection(),
                        null,
                        null,
                        AnimalContract.Animal.getQualifiedColumnName(AnimalContract.Animal.COLUMN_ITEM_SKU)
                );
        }



        return null;
    }




    @IntDef({ANIMALS_LIST_TYPE, RESCUER_LIST_TYPE, ADOPTIONS_LIST_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    @interface LoadersTypeDef {
    }

}
