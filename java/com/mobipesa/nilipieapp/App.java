package com.mobipesa.nilipieapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.google.crypto.tink.Aead;
import com.mobipesa.nilipieapp.helpers.AppExecutors;

/**
 * Created by Mbariah on 6/21/18.
 */

public class App extends Application {

    public AppExecutors mAppExecutors;

    private static final String TINK_KEYSET_NAME = "app_gen_keyset";
    private static final String MASTER_KEY_URI = "android-keystore://app_gen_key";
    public Aead aead;


    @SuppressLint("StaticFieldLeak")
    private static Context mContext;


    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mAppExecutors = new AppExecutors();
        //setInMemoryRoomDatabases();
        //GenerateRsaKeyPair();
//        try {
//            Bitmap.Config.register(TinkConfig.LATEST);
//            aead = getOrGenerateNewKeysetHandle().getPrimitive(Aead.class);
//        } catch (GeneralSecurityException | IOException e) {
//            throw new RuntimeException(e);
//        }

    }



    public AppExecutors getExecutors() {
        return mAppExecutors;
    }

//    public AppDatabase getDatabase() {
//        return AppDatabase.getInstance(this);
//    }

    public ProjectRepository getRepository() {
        return ProjectRepository.getInstance();
    }

}