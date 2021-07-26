

package com.example.pethome.storeapp.data.local.contracts;

import android.net.Uri;

public interface StoreContract {

    String CONTENT_AUTHORITY = "com.example.pethome.storeapp.provider";

    Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
}
