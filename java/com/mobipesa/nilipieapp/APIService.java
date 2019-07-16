package com.mobipesa.nilipieapp;


import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


/**
 * Created by Agnes on 6/03/19.
 */
public interface APIService {

    int CONNECT_TIMEOUT = 10 * 1000;

    //Connection Read timeout duration
    int READ_TIMEOUT = 10 * 1000;

    //Connection write timeout duration
    int WRITE_TIMEOUT = 10 * 1000;

    String BASE_URL = "http://159.122.225.153:5005/nilipie/";

    @POST("?")
    Call<JsonObject> checkUser(@Body HashMap<String, Object> data);

    @POST("?")
    Call<JsonObject> login(@Body HashMap<String, Object> data);

    @POST("?")
    Call<JsonObject> registerUsers(@Body HashMap<String, Object> data);

    @POST("?")
    Call<JsonObject> customerPay(@Body HashMap<String, Object> data);

    @POST("?")
    Call<JsonObject> confirmOTP(@Body HashMap<String, Object> data);


    @POST("?")
    Call<JsonObject> addDependant(@Header("Authorization") String auth, @Header("From") String from, @Body HashMap<String, Object> data);


    @POST("?")
    Call<JsonObject> fetchMyDependants(@Header("Authorization") String auth, @Header("From") String from, @Body HashMap<String, Object> data);



}
