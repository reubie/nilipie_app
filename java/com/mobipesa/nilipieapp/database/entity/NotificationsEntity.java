package com.mobipesa.nilipieapp.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.mobipesa.nilipieapp.models.Notifications;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mbariah on 7/25/18.
 */

@Entity(tableName = "notifications")
public class NotificationsEntity implements Notifications {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "body")
    private String body;

    @ColumnInfo(name = "extras")
    private String extras;

    @ColumnInfo(name = "status")
    private int status = 0; //0 unread , 1 read

    @ColumnInfo(name = "date_created")
    private Date date;

    public NotificationsEntity() {
    }

    //Tests only
    @Ignore
    public NotificationsEntity(String title, String body, String extras, Date date, int status) {
        this.title = title;
        this.body = body;
        this.extras = extras;
        this.date = date;
        this.status = status;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFormatDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
        return formatter.format(date);
    }
}
