package com.mobipesa.nilipieapp.models;

import java.util.Date;

/**
 * Created by Mbariah on 7/25/18.
 */
public interface Notifications {

    int getId();
    String getTitle();
    String getBody();
    String getExtras();
    int getStatus(); //0 unread , 1 read
    Date getDate();
    String getFormatDate();

}
