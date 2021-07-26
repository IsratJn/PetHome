
package com.example.pethome.storeapp.utils;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public final class SnackbarUtility {

    private Snackbar mSnackbar;

    public SnackbarUtility(Snackbar snackbar) {
        mSnackbar = snackbar;
    }

    public SnackbarUtility revealCompleteMessage() {
        View snackbarView = mSnackbar.getView();
        TextView snackbarTextView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackbarTextView.setMaxLines(Integer.MAX_VALUE);
        return this;
    }

    public SnackbarUtility setDismissAction(@StringRes int dismissActionLabelResId) {
        if (mSnackbar.getDuration() != Snackbar.LENGTH_INDEFINITE) {
            mSnackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        }
        return setAction(dismissActionLabelResId, (view) -> mSnackbar.dismiss());
    }

    public SnackbarUtility setDismissAction(String dismissActionLabel) {
        if (mSnackbar.getDuration() != Snackbar.LENGTH_INDEFINITE) {
            mSnackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        }
        return setAction(dismissActionLabel, (view) -> mSnackbar.dismiss());
    }

    public SnackbarUtility setAction(@StringRes int actionLabelResId, final View.OnClickListener listener) {
        mSnackbar.setAction(actionLabelResId, listener);
        return this;
    }

    public SnackbarUtility setAction(String actionLabel, final View.OnClickListener listener) {
        mSnackbar.setAction(actionLabel, listener);
        return this;
    }

    public void showSnack() {
        mSnackbar.show();
    }
}
