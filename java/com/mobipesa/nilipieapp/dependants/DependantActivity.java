package com.mobipesa.nilipieapp.dependants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mobipesa.nilipieapp.App;
import com.mobipesa.nilipieapp.ProjectRepository;
import com.mobipesa.nilipieapp.R;
import com.mobipesa.nilipieapp.databinding.ActivityDependantBinding;
import com.mobipesa.nilipieapp.helpers.Utils;

import java.util.Objects;

public class DependantActivity extends AppCompatActivity {

    private ActivityDependantBinding binding;
    Context mContext;
    SharedPreferences prefs;
    ProjectRepository repo;
    String msisdn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        repo = ((App) Objects.requireNonNull(this).getApplicationContext()).getRepository();
        prefs = Utils.userDetails(this);

        Bundle getBundle = getIntent().getExtras();
        if (getBundle != null) {
            msisdn = getBundle.getString("msisdn");
            Log.i("TAG", "onCreate: " + msisdn);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dependant);


        binding.manageDependants.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ManageDependants.class);
            intent.putExtra("msisdn", msisdn);
            startActivity(intent);
        });

        binding.managePrincipals.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ManagePrincipals.class);
            startActivity(intent);
        });

    }
}
