package com.mobipesa.nilipieapp.models;

import java.util.Date;

/**
 * Created by Mbariah on 7/9/18.
 */
public interface Collectibles {

    int getId();
    String getName();
    String getHash();
    //Unique key for hashing Initialization Vectors (IV)- Cipher
    //String getKey();
    Date getDate();
    int getStatus();
}
