
package com.example.pethome.storeapp.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

public final class TextAppearanceUtility {
    private TextAppearanceUtility() {
        throw new AssertionError("No " + this.getClass().getCanonicalName() + " instances for you!");
    }
    public static void setHtmlText(TextView textView, String htmlTextToSet) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spannableStringBuilder.append(Html.fromHtml(htmlTextToSet, Html.FROM_HTML_MODE_COMPACT));
        } else {
            spannableStringBuilder.append(Html.fromHtml(htmlTextToSet));
        }
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }
    @NonNull
    public static String getHtmlFormattedText(String textWithHtmlContent) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spannableStringBuilder.append(Html.fromHtml(textWithHtmlContent, Html.FROM_HTML_MODE_COMPACT));
        } else {
            spannableStringBuilder.append(Html.fromHtml(textWithHtmlContent));
        }

        return spannableStringBuilder.toString();
    }

}
