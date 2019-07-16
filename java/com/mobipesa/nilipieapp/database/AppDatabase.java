package com.mobipesa.nilipieapp.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.mobipesa.nilipieapp.database.converter.DateConverter;
import com.mobipesa.nilipieapp.database.dao.CollectionDAO;
import com.mobipesa.nilipieapp.database.dao.NotificationDAO;
import com.mobipesa.nilipieapp.database.entity.CollectionEntity;
import com.mobipesa.nilipieapp.database.entity.NotificationsEntity;

/**
 * Written by Mbariah on 26-Oct-17.
 */

@Database(entities = {CollectionEntity.class, NotificationsEntity.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)

//abstract to avoid initialization
public abstract class AppDatabase extends RoomDatabase {

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
    private static AppDatabase INSTANCE;



    //Can only be called by ne thread at a time
    public synchronized static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = getAppDatabase(context);
        }
        return INSTANCE;
    }

    private static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, AppDatabase.class, "mobipesa_db")
                            //.addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

//                                    Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
//                                        @Override
////                                        public void run() {
////                                            getInstance(context).loanDefaultsDAO().insertAll(com.mobipesa.loan.database.entity.LoanDefaultsEntity.populateDB());
////                                            getInstance(context).feesDao().insertAll(com.mobipesa.loan.database.entity.FeesEntity.populateDB());
////                                        }
//                                    });

                                }
                            })
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract CollectionDAO collectionDAO();

    public abstract NotificationDAO notificationDAO();
}