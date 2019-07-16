package com.mobipesa.nilipieapp;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.livinglifetechway.quickpermissions.annotations.OnPermissionsDenied;
import com.livinglifetechway.quickpermissions.annotations.OnShowRationale;
import com.livinglifetechway.quickpermissions.annotations.WithPermissions;
import com.livinglifetechway.quickpermissions.util.QuickPermissionsRequest;
import com.mobipesa.nilipieapp.databinding.ActivityPermissionsBinding;
import com.mobipesa.nilipieapp.helpers.Status;
import com.mobipesa.nilipieapp.helpers.Utils;
import com.mobipesa.nilipieapp.interfaces.FetchPermission;
import com.mobipesa.nilipieapp.interfaces.SwitchClickCallback;

import java.util.HashMap;


public class PermissionsActivity extends AppCompatActivity implements FetchPermission, SwitchClickCallback {

    public final static String permissions = "PERMISSIONS";
    boolean checkNetwork;
    boolean checkPermission;
    String[] PERMISSIONS = {
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE,
    };

    private ActivityPermissionsBinding activityLoadingBinding;
    private ProjectRepository repo;
    private SharedPreferences prefs;
    private KProgressHUD hud;
    private String msisdn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            checkPermission = getIntent().getBooleanExtra(permissions, false);
            msisdn = getIntent().getStringExtra("msisdn");
        }

        repo = ((App) this.getApplicationContext()).getRepository();
        checkNetwork = Utils.checkInternetConnection();
        prefs = Utils.userDetails(this);

        activityLoadingBinding = DataBindingUtil.setContentView(this, R.layout.activity_permissions);
        activityLoadingBinding.setCallback(this);


        fetchCustomerDetails();
    }


    private void checkPermissions() {

        if (hud.isShowing())
            hud.dismiss();

        if (!checkNetwork) {

            //set permissions view as hidden and network as shown
            activityLoadingBinding.setIsError(true);

            return;

        } else if (!checkPermission) {
            //some permissions are missing

            //set permissions view visible
            activityLoadingBinding.setIsPermission(true);

            if (!Utils.hasPermissions(this, PERMISSIONS)) {
                return;
            }

        }

        Intent intent = new Intent(PermissionsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @WithPermissions(permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS})
    @Override
    public void getAllPermissions() {
        checkPermissions();
    }

    public void fetchCustomerDetails() {

        hud = KProgressHUD.create(PermissionsActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Kindly wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.6f)
                .show();

        HashMap<String, Object> msisdnMap = new HashMap<>();
        msisdnMap.put("msisdn", Utils.getProperPhoneNumber(msisdn, "254"));

        HashMap<String, Object> map = new HashMap<>();
        map.put("query", "query($msisdn: String!){users(msisdn: $msisdn){firstName wallet msisdn}}");
        map.put("variables", msisdnMap);

        repo.customerDetails(map).observe(this, new Observer<HashMap<Status, String>>() {

            @Override

            public void onChanged(@NonNull HashMap<Status, String> result) {
                activityLoadingBinding.progressbar.setVisibility(View.GONE);

                if (result.containsKey(Status.SUCCESS)) {

                    //set permissions view as own and network as shown
                    activityLoadingBinding.setIsError(false);
                    activityLoadingBinding.setIsPermission(true);
                    hud.dismiss();


                } else if (result.containsKey(Status.FAIL)) {

                    Toast.makeText(PermissionsActivity.this, "Error", Toast.LENGTH_SHORT).show();

                }



            }


        });
    }


    @OnShowRationale
    public void onShowRational(final QuickPermissionsRequest arg) {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Denied")
                .setCancelable(false)
                .setMessage("These permissions are required to proceed any further. Please retry")
                .setPositiveButton("Try Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        arg.proceed(); // this will continue asking for the permissions for the denied once
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //arg.cancel(); // this will cancel flow and the method won't be called
                        checkPermissions();
                    }
                })
                .show();

    }

    @OnPermissionsDenied
    public void onDenied(QuickPermissionsRequest arg) {
        checkPermissions();
        Toast.makeText(
                this,
                "Without permissions, this app cannot function correctly",
                Toast.LENGTH_SHORT
        ).show();
    }

    public void onClick() {

        getAllPermissions();
    }

    @Override
    public void onRetryClick() {
        checkNetwork = Utils.checkInternetConnection();
        fetchCustomerDetails();
    }

    //"Without this permission, this feature cannot function correctly",

}
