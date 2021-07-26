
package com.example.pethome.storeapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

public class IntentUtility {

    private static final String URI_STR_TELEPHONE_SCHEME = "tel:";
    private static final String URI_STR_EMAIL_SCHEME = "mailto:";
    private IntentUtility() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }
    public static void dialPhoneNumber(FragmentActivity activity, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(URI_STR_TELEPHONE_SCHEME + phoneNumber));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }
    public static void composeEmail(FragmentActivity activity, String[] toAddresses,
                                    @Nullable String[] ccAddresses, @Nullable String subject,
                                    @Nullable String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(URI_STR_EMAIL_SCHEME));
        intent.putExtra(Intent.EXTRA_EMAIL, toAddresses);
        intent.putExtra(Intent.EXTRA_CC, ccAddresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (!TextUtils.isEmpty(body)) {
            intent.putExtra(Intent.EXTRA_TEXT, TextAppearanceUtility.getHtmlFormattedText(body));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                intent.putExtra(Intent.EXTRA_HTML_TEXT, TextAppearanceUtility.getHtmlFormattedText(body));
            }
        }

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }
    public static void openLink(Context context, String webUrl) {
        Uri webPageUri = Uri.parse(webUrl);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPageUri);
        if (webIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(webIntent);
        }
    }
}
