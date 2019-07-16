package com.mobipesa.nilipieapp.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mobipesa.nilipieapp.database.entity.CollectionEntity;

import java.util.Date;

@Dao
public interface CollectionDAO {

    @Query("SELECT date_created FROM collection ORDER BY `_id` DESC LIMIT 1")
    Date lastInsert();

    @Query("DELETE FROM collection WHERE _id = :id")
    void deleteByID(String id);

    @Insert
    void insertSingleCollectible(CollectionEntity CollectionEntity);

    @Query("DELETE FROM collection")
    void deleteAll();

    @Update
    void updateStatus(CollectionEntity CollectionEntity);


}
