package com.mobipesa.nilipieapp.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.mobipesa.nilipieapp.models.Collectibles;

import java.util.Date;

/**
 * Created by Mbariah on 7/9/18.
 */

@Entity(tableName = "collection")
public class CollectionEntity implements Collectibles {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    @NonNull
    int id;

    @ColumnInfo(name = "filename")
    String name;

    @ColumnInfo(name = "hash")
    String hash;

    @ColumnInfo(name = "date_created")
    Date date;

    @ColumnInfo(name = "status")
    int status;

    public CollectionEntity() {
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
