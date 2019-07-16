package com.mobipesa.nilipieapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mobipesa.nilipieapp.helpers.Status;
import com.mobipesa.nilipieapp.helpers.Utils;
import com.mobipesa.nilipieapp.interfaces.ProgressInterface;
import com.mobipesa.nilipieapp.models.DependantItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mobipesa.nilipieapp.APIService.CONNECT_TIMEOUT;
import static com.mobipesa.nilipieapp.APIService.READ_TIMEOUT;
import static com.mobipesa.nilipieapp.APIService.WRITE_TIMEOUT;
import static com.mobipesa.nilipieapp.App.getContext;

public class ProjectRepository {

    private static ProjectRepository projectRepository;

    private APIService service;

    //constructor
    private ProjectRepository() {


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                //.addNetworkInterceptor(new CacheInterceptor())
                //TODO : DISABLE ON PRODUCTION
                .addInterceptor(logging);


        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        service = retrofit.create(APIService.class);

    }

    //Singleton
    synchronized static ProjectRepository getInstance() {

        if (projectRepository == null) {
            synchronized (ProjectRepository.class) {
                if (projectRepository == null) {
                    projectRepository = new ProjectRepository();
                }
            }
        }

        return projectRepository;
    }


    public LiveData<HashMap<Status, String>> checkifMember(HashMap<String, Object> data) {

        final MutableLiveData<HashMap<Status, String>> results = new MutableLiveData<>();

        service.checkUser(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                HashMap<Status, String> map = new HashMap<>();

                if (response.isSuccessful()) {

                    JsonObject object = response.body();

                    Log.i("TEST", "onResponse: " + new Gson().toJson(response.body()));
                    if (object != null) {
                        JsonObject data = object.get("data").getAsJsonObject();

                        JsonArray users = data.get("users").getAsJsonArray();
                        if (users.size() == 0) {
                            map.put(Status.FAIL, "users");
                        } else {
                            map.put(Status.SUCCESS, "agnes");
                        }

                    }

                    results.postValue(map);

                } else {

                    JSONObject jObjError = null;
                    String message = "Failed. Something went wrong";
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject gsonObject = (JsonObject) jsonParser.parse(jObjError.toString());
                        JsonObject error = gsonObject.get("error").getAsJsonObject();
                        message = error.get("message").getAsString();

                        if (BuildConfig.DEBUG) {
                            Log.i(ProjectRepository.class.getSimpleName(), "onResponse: " + message);
                        }
                        map.put(Status.FAIL, message);
                        results.postValue(map);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                HashMap<Status, String> map = new HashMap<>();
                map.put(Status.FAIL, "Failed. Something went wrong.\nTry again later");
                results.postValue(map);

            }
        });

        return results;
    }

    public LiveData<HashMap<Status, String>> loginUsers(HashMap<String, Object> data) {

        final MutableLiveData<HashMap<Status, String>> results = new MutableLiveData<>();

        service.login(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                HashMap<Status, String> map = new HashMap<>();

                if (response.isSuccessful()) {

                    JsonObject object = response.body();

                    Log.i("TEST", "onResponse: " + new Gson().toJson(response.body()));
                    if (object != null) {
                        JsonObject data = object.get("data").getAsJsonObject();

                        JsonArray users = data.get("users").getAsJsonArray();
                        if (users.size() == 0) {
                            map.put(Status.FAIL, "users");
                        } else {
                            map.put(Status.SUCCESS, "agnes");
                        }

                    }

                    results.postValue(map);

                } else {

                    JSONObject jObjError = null;
                    String message = "Failed. Something went wrong";
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject gsonObject = (JsonObject) jsonParser.parse(jObjError.toString());
                        JsonObject error = gsonObject.get("error").getAsJsonObject();
                        message = error.get("message").getAsString();

                        if (BuildConfig.DEBUG) {
                            Log.i(ProjectRepository.class.getSimpleName(), "onResponse: " + message);
                        }
                        map.put(Status.FAIL, message);
                        results.postValue(map);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                HashMap<Status, String> map = new HashMap<>();
                map.put(Status.FAIL, "Failed. Something went wrong.\nTry again later");
                results.postValue(map);

            }
        });

        return results;
    }

    public LiveData<HashMap<Status, String>> verifyOTP(HashMap<String, Object> data) {

        final MutableLiveData<HashMap<Status, String>> results = new MutableLiveData<>();

        service.confirmOTP(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                HashMap<Status, String> map = new HashMap<>();

                if (response.isSuccessful()) {

                    JsonObject object = response.body();

                    Log.i("TEST", "onResponse: " + new Gson().toJson(response.body()));
                    if (object != null) {

                        //{"data":{"otp":[{"msisdn":"254724971796"}]}}
                        JsonObject data = object.get("data").getAsJsonObject();
                        JsonArray otp = data.get("otp").getAsJsonArray();

                        JsonObject innerData = otp.get(0).getAsJsonObject();
                        innerData.get("msisdn");

                        if (otp.size() == 0) {
                            map.put(Status.FAIL, "msisdn");
                        } else {
                            map.put(Status.SUCCESS, "agnes");
                        }

                    }

                    results.postValue(map);

                } else {

                    JSONObject jObjError = null;
                    String message = "Failed. Something went wrong";
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject gsonObject = (JsonObject) jsonParser.parse(jObjError.toString());
                        JsonObject error = gsonObject.get("error").getAsJsonObject();
                        message = error.get("message").getAsString();

                        if (BuildConfig.DEBUG) {
                            Log.i(ProjectRepository.class.getSimpleName(), "onResponse: " + message);
                        }
                        map.put(Status.FAIL, message);
                        results.postValue(map);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                HashMap<Status, String> map = new HashMap<>();
                map.put(Status.FAIL, "Failed. Something went wrong.\nTry again later");
                results.postValue(map);

            }
        });

        return results;
    }

//    {
//        "query": "mutation relayAddUser($input: RelayAddUserInput!) {relayAddUser(input: $input) {user {firstName}}}",
//            "variables": {
//        "input": {"firstName":"Agnes","lastName":"kavata","idNo":"30497433","msisdn":"254724971796"}
//    }
//    }


    public void customersRegister(ProgressInterface<HashMap<Status, String>> progressInterface, HashMap<String, Object> data) {

        service.registerUsers(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                HashMap<Status, String> map = new HashMap<>();

                if (response.isSuccessful()) {

                    map.put(Status.SUCCESS, "You have been successfully registered. Log in to continue.");

                    progressInterface.onResult(map);


                } else {

                    JSONObject jObjError = null;
                    String message = "Failed. Something went wrong";
                    try {
                        jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject gsonObject = (JsonObject) jsonParser.parse(jObjError.toString());
                        JsonObject error = gsonObject.get("error").getAsJsonObject();
                        message = error.get("message").getAsString();

                        if (BuildConfig.DEBUG) {
                            Log.i(ProjectRepository.class.getSimpleName(), "onResponse: " + message);
                        }
                        map.put(Status.FAIL, message);
                        progressInterface.onResult(map);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                HashMap<Status, String> map = new HashMap<>();
                map.put(Status.FAIL, "Failed. Something went wrong.\nTry again later");
                progressInterface.onResult(map);


            }
        });
    }

    public LiveData<HashMap<Status, String>> customerDetails(HashMap<String, Object> data) {
        final MutableLiveData<HashMap<Status, String>> results = new MutableLiveData<>();
        SharedPreferences prefs = Utils.userDetails(getContext());

        service.checkUser(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                HashMap<Status, String> map = new HashMap<>();

                if (response.isSuccessful()) {

                    JsonObject object = response.body();

                    Log.i("TEST", "onResponse: " + new Gson().toJson(response.body()));
                    if (object != null) {
                        JsonObject data = object.get("data").getAsJsonObject();
                        JsonArray users = data.get("users").getAsJsonArray();
                        if (users.size() == 0) {
                            map.put(Status.FAIL, "users");
                        } else {
                            map.put(Status.SUCCESS, "agnes");

                            JsonObject userArray = users.get(0).getAsJsonObject();

                            Log.i("hhhh", userArray.get("firstName").getAsString());
                            Log.i("hhhh", userArray.get("msisdn").getAsString());
                            Log.i("hhhh", userArray.get("wallet").getAsString());


                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("msisdn", userArray.get("msisdn").getAsString());
                            editor.putString("firstName", userArray.get("firstName").getAsString());
                            editor.putString("balance", userArray.get("wallet").getAsString());
                            editor.apply();
                        }

                    }

                    results.postValue(map);

                } else {

                    JSONObject jObjError = null;
                    String message = "Failed. Something went wrong";
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        JsonParser jsonParser = new JsonParser();
                        JsonObject gsonObject = (JsonObject) jsonParser.parse(jObjError.toString());
                        JsonObject error = gsonObject.get("error").getAsJsonObject();
                        message = error.get("message").getAsString();

                        if (BuildConfig.DEBUG) {
                            Log.i(ProjectRepository.class.getSimpleName(), "onResponse: " + message);
                        }
                        map.put(Status.FAIL, message);
                        results.postValue(map);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                HashMap<Status, String> map = new HashMap<>();
                map.put(Status.FAIL, "Failed. Something went wrong.\nTry again later");
                results.postValue(map);

            }
        });

        return results;
    }
//
//"query": "mutation relayAddDependant($input: RelayAddDependantInput!) {relayAddDependant(input: $input) {dependant {name}}}",
//    "variables": {
//        "input": {"name":"Agnes","msisdnD":"254722448625","upperLimit":"6000","msisdnP":"254724971796"}


    public void inviteDependants(ProgressInterface<HashMap<Status, String>> progressInterface, HashMap<String, Object> data) {

        service.addDependant("3", "3", data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                HashMap<Status, String> map = new HashMap<>();

                if (response.isSuccessful()) {

                    map.put(Status.SUCCESS, "You have added a  dependant.");

                    progressInterface.onResult(map);


                } else {
                    String message = "Failed. Something went wrong";
                    map.put(Status.FAIL, message);
                    progressInterface.onResult(map);

                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                HashMap<Status, String> map = new HashMap<>();
                map.put(Status.FAIL, "Failed. Something went wrong.\nTry again later");
                progressInterface.onResult(map);


            }
        });
    }

//    {
//        "query":"mutation relayAddTransaction($input: RelayAddTransactionInput!) {relayAddTransaction(input: $input) {transaction {transactionType}}}",
//            "variables": {
//        "input": {"transactionType":"paybill",
//                "msisdnD":"254722448625",
//                "amount":"6000",
//                "msisdnP":"254712886310",
//                "channel":"mpesa",
//                "shortCode":"1234",
//                "accountNo":"2344",
//                "identifier":"nilipie"}
//    }

    public void customersPay(ProgressInterface<HashMap<Status, String>> progressInterface, HashMap<String, Object> data) {

        service.customerPay(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                HashMap<Status, String> map = new HashMap<>();

                if (response.isSuccessful()) {

                    map.put(Status.SUCCESS, "Your payment was successful.");

                    progressInterface.onResult(map);


                } else {


                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                HashMap<Status, String> map = new HashMap<>();
                map.put(Status.FAIL, "Failed. Something went wrong.\nTry again later");
                progressInterface.onResult(map);


            }
        });
    }




    public MutableLiveData<List<DependantItem>> listDependants(HashMap<String, Object> map) {

        MutableLiveData<List<DependantItem>> results = new MutableLiveData<>();

        service.fetchMyDependants("3", "3", map).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                if (response.isSuccessful()) {

                    JsonObject object = response.body();
                    JsonObject data = object.get("data").getAsJsonObject();
                    JsonArray dependant = data.get("dependant").getAsJsonArray();

                    Type listType = new TypeToken<List<DependantItem>>() {
                    }.getType();

                    //Have to set date format or crash if Date is in object POJO class
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

                    //ArrayList jsonObjList = gson.fromJson(dependant, ArrayList.class);
                    //Log.i("RESULTS100", "onResponse: " + new Gson().toJson(jsonObjList));

                    //From internet
                    List<DependantItem> dependantsList = gson.fromJson(dependant, listType);

                    Log.i("RESULTS200", "onResponse: " + new Gson().toJson(dependantsList));

                    results.postValue(dependantsList);

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.i(ProjectRepository.class.getSimpleName(), "onFailure: " + t.toString());
                }
                results.setValue(null);
            }
        });

        return results;
    }


}




