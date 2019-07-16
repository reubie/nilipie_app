package com.mobipesa.nilipieapp.login;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.mobipesa.nilipieapp.App;
import com.mobipesa.nilipieapp.R;
import com.mobipesa.nilipieapp.helpers.ActivityUtils;
import com.mobipesa.nilipieapp.helpers.Utils;
import com.mobipesa.nilipieapp.interfaces.FragmentInterface;

public class LoginActivity extends AppCompatActivity implements FragmentInterface {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_blank_login);
        prefs = Utils.userDetails(App.getContext());

        init();

    }


/*//    @SuppressLint("HardwareIds")
//    public void getDeviceIdentifier() {
//        String deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//
//        if (prefs.getString("session_token", "").equals("")) {
//            //EMPTY SO PLACE IT
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putString("session_token", Utils.attemptEncrypt(deviceUniqueIdentifier)); //user type
//            editor.apply();
//        }
//
//        init(deviceUniqueIdentifier);
//
//    }*/

    private void init() {

        String username = prefs.getString("username", "");
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (!prefs.getString("token", "").equals("") && username != null) {

            Bundle args = new Bundle();
            args.putString("msisdn", username);

            Fragment pinFragment = new com.mobipesa.nilipieapp.login.PinFragment();
            pinFragment.setArguments(args);

            fragmentManager.beginTransaction()
                    .replace(R.id.contentContainer, pinFragment)
                    .commit();

        } else {

            fragmentManager.beginTransaction()
                    .replace(R.id.contentContainer, new com.mobipesa.nilipieapp.login.PhoneFragment())
                    .commit();
        }

    }

    @Override
    public void replaceFragment(Fragment fragment) {
        ActivityUtils.replaceFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentContainer);
    }
}