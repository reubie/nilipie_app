package com.mobipesa.nilipieapp.settings;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.view.MenuItem;

import com.mobipesa.nilipieapp.App;
import com.mobipesa.nilipieapp.R;
import com.mobipesa.nilipieapp.database.AppDatabase;
import com.mobipesa.nilipieapp.helpers.AppExecutors;
import com.mobipesa.nilipieapp.helpers.Utils;

public class SettingsActivity extends AppCompatPreferenceActivity {

    Context context;

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@mobipesa.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Marafiki Loans");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;


        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            SharedPreferences prefs = Utils.userDetails(getActivity());
            AppDatabase mDb = AppDatabase.getInstance(App.getContext());
            AppExecutors executors = ((App) App.getContext()).getExecutors();

            addPreferencesFromResource(R.xml.pref_general);

            // Feedback preference click listener
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(preference -> {
//                Instabug.show();
                return true;
            });

            Preference logout = findPreference(getString(R.string.key_logout));
            logout.setTitle(Html.fromHtml("<font color='red'>" + logout.getTitle() + "</font>"));
            logout.setOnPreferenceClickListener(preference -> {

                //remove all notifications
                NotificationManager notificationManager = (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

                //delete all shared prefs
                prefs.edit().clear().apply();

                executors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        //clear all db's
                        mDb.notificationDAO().deleteAll();
                        mDb.collectionDAO().deleteAll();

                    }
                });

                Intent intent = new Intent(getActivity(), com.mobipesa.nilipieapp.login.LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                return true;
            });
        }
    }
}
