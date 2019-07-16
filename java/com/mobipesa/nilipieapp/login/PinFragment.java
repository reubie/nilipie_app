package com.mobipesa.nilipieapp.login;

import android.Manifest;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipesa.nilipieapp.App;
import com.mobipesa.nilipieapp.BuildConfig;
import com.mobipesa.nilipieapp.PermissionsActivity;
import com.mobipesa.nilipieapp.ProjectRepository;
import com.mobipesa.nilipieapp.R;
import com.mobipesa.nilipieapp.helpers.Status;
import com.mobipesa.nilipieapp.helpers.Utils;

import java.util.HashMap;
import java.util.Objects;





public class PinFragment extends Fragment {

    public final static String permissions = "PERMISSIONS";
    TextView initialText, forgotPin;
    ProgressBar progressBar;
    Button button;
    String[] PERMISSIONS = {
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private ProjectRepository repo;
    private String msisdn;
    Context context;
    private SharedPreferences prefs;

    public PinFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = ((App) Objects.requireNonNull(getActivity()).getApplicationContext()).getRepository();
        prefs = Utils.userDetails(App.getContext());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            msisdn = bundle.getString("msisdn");

        }

        if (BuildConfig.DEBUG) {
            Log.i(OTPFragment.class.getSimpleName(), "onCreate: " + msisdn);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment_account
        return inflater.inflate(R.layout.activity_register_pin, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialText = view.findViewById(R.id.initial_text);
        progressBar = view.findViewById(R.id.progressbar);
        button = view.findViewById(R.id.button);
        forgotPin = view.findViewById(R.id.forgot_pin);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorAccent));
            progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }

        button.setOnClickListener(this::onClick);
        forgotPin.setOnClickListener(this::onClick);
    }

    private EditText getPINInput() {
        return (EditText) Objects.requireNonNull(getView()).findViewById(R.id.pin_input);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:

                //Validation
                if (Utils.checkIfEmptyString(getPINInput().getText().toString().trim()) ||
                        getPINInput().getText().toString().trim().length() < 4) {
                    getPINInput().setError("Invalid input");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                button.setEnabled(false);
                button.setBackgroundResource(R.drawable.disabled_round_corner_rect);

                makeNetworkCall(getPINInput());


                break;

        }
    }

    public void makeNetworkCall(EditText input) {


        String formatted_password = input.getText().toString().trim();
        HashMap<String, Object> msisdnMap = new HashMap<>();
        msisdnMap.put("msisdn", Utils.getProperPhoneNumber(msisdn,"254"));


        HashMap<String, Object> map = new HashMap<>();
        map.put("query", "query($msisdn: String!){users(msisdn: $msisdn){firstName}}");
        map.put("variables",msisdnMap);



        repo.loginUsers(map).observe((LifecycleOwner) this, new Observer<HashMap<Status, String>>() {
            @Override
            public void onChanged(@NonNull HashMap<Status, String> result) {
                if (result.containsKey(Status.SUCCESS)) {

                    SharedPreferences.Editor editor = Utils.userDetails(Objects.requireNonNull(getActivity())).edit();
                    editor.putString("msisdn", Utils.attemptEncrypt(prefs.getString("msisdn", "")));
                    editor.putString("first_name", Utils.attemptEncrypt(prefs.getString("first_name", "")));
                    editor.apply();


                    Intent intent = new Intent(getActivity(), PermissionsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(permissions, Utils.hasPermissions(getActivity(), PERMISSIONS));
                    intent.putExtra("msisdn", msisdn);
                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else {
                    //error
                    progressBar.setVisibility(View.GONE);
                    button.setEnabled(true);
                    button.setBackgroundResource(R.drawable.blue_round_corner_rect);
                    Toast.makeText(getActivity(), "Network Error. Try again", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
}

