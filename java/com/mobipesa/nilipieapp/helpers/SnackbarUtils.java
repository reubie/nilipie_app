package com.mobipesa.nilipieapp.helpers;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Provides a method to show a Snackbar.
 */
public class SnackbarUtils {

    public static void showSnackbar(View v, String snackerText) {

        if (v == null || snackerText == null) {
            return;
        }

        final Snackbar snack = Snackbar.make(v, snackerText, Snackbar.LENGTH_LONG);
        snack.setAction("Dismiss", v1 -> snack.dismiss()).show();
    }

}
