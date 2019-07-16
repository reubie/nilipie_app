package com.mobipesa.nilipieapp.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mobipesa.nilipieapp.database.entity.NotificationsEntity;

import java.util.List;

@Dao
public interface NotificationDAO {

    @Query("SELECT * FROM notifications ORDER BY `date_created` DESC")
    List<NotificationsEntity> fetchAll();

    @Query("SELECT * FROM notifications ORDER BY `date_created` DESC ")
    LiveData<List<NotificationsEntity>> livefetchAll();

    @Query("SELECT * FROM notifications WHERE `_id` =:id")
    NotificationsEntity findnotifications(int id);

    @Query("SELECT COUNT(_id) from notifications WHERE status = 0")
    LiveData<Integer> getNumberOfUnreadRows();

    //no live data
    @Query("SELECT COUNT() from notifications")
    int initialNumberOfRows();

    /*@Query("DELETE FROM cart WHERE `product_name` IN \n" +
            " (SELECT `product_name` FROM cart \n" +
            " WHERE `product_name` = :name LIMIT 1);")
    void deleteByProductName(String name);*/

    //@Query("SELECT * FROM cart WHERE `product_name` = :name ORDER BY `_id` DESC ")
    //Cursor deleteByProductName(String name);

    @Query("DELETE FROM notifications WHERE _id = :id")
    void deleteByID(String id);


    @Query("UPDATE notifications SET status = 1  WHERE _id = :id")
    void updateRead(String id);

    //varargs ...
    @Insert
    void insertCart(NotificationsEntity... notificationsEntities);

    @Insert
    void insert(List<NotificationsEntity> lstEntity);

    @Insert
    long insertSingleCollectible(NotificationsEntity notificationssEntity);

    @Delete
    void delete(NotificationsEntity notificationssEntity);

    @Query("DELETE FROM notifications")
    void deleteAll();

    @Update
    void updateStatus(NotificationsEntity notificationssEntity);


}
