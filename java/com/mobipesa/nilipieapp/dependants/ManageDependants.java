package com.mobipesa.nilipieapp.dependants;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.livinglifetechway.k4kotlin.ToastKt;
import com.livinglifetechway.quickpermissions.annotations.WithPermissions;
import com.mbariah.multicontactpicker.ContactResult;
import com.mbariah.multicontactpicker.MultiContactPicker;
import com.mobipesa.nilipieapp.App;
import com.mobipesa.nilipieapp.ProjectRepository;
import com.mobipesa.nilipieapp.R;
import com.mobipesa.nilipieapp.databinding.ActivityManageDependantsBinding;
import com.mobipesa.nilipieapp.helpers.PromptPopUpView;
import com.mobipesa.nilipieapp.helpers.Status;
import com.mobipesa.nilipieapp.helpers.Utils;
import com.mobipesa.nilipieapp.models.DependantItem;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.mobipesa.nilipieapp.App.getContext;


public class ManageDependants extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 105;
    private final int CALLBACK_NUMBER = 99;
    Context mContext;
    SharedPreferences prefs;

    private ActivityManageDependantsBinding manageDependants;
    private ProjectRepository repo;
    MaterialDialog materialDialog = null;
    private DependantsViewModel viewModel;
    private DependantsAdapter dependantsAdapter;
    private String  amountEntry, dependantName, depenantPhoneNumber;
    private Float msisdn = 0f;
    boolean isManual;
    private EditText customerInputAmount,customerInputName,customerInputPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = ((App) Objects.requireNonNull(this).getApplicationContext()).getRepository();
        prefs = Utils.userDetails(this);
        mContext = this;
        manageDependants = DataBindingUtil.setContentView(this, R.layout.activity_manage_dependants);

        viewModel = ViewModelProviders.of(this).get(DependantsViewModel.class);


        dependantsAdapter = new DependantsAdapter();
        //layout manager set
        manageDependants.viewCustomers.setLayoutManager(new LinearLayoutManager(this));
        manageDependants.viewCustomers.setAdapter(dependantsAdapter);

        initToolbar();
        manageDependants.addButton.setOnClickListener(view -> {
            materialpopup();
        });
        subscribeUi(viewModel);


    }


    private void initToolbar() {
        setSupportActionBar(manageDependants.toolbarFrag);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Manage Dependants");
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        manageDependants.toolbarFrag.setNavigationOnClickListener(view -> finish());
    }


    public void materialpopup() {




        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .customView(R.layout.confirm_dependant, false)
                .positiveText(R.string.add_dependants)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if(!isManual){
                            amountEntry = customerInputAmount.getText().toString();

                            fetchContacts();
                        }else {
                            //on    manual entry
                            dependantName = customerInputName.getText().toString();
                            depenantPhoneNumber = customerInputPhoneNumber.getText().toString();
                            amountEntry = customerInputAmount.getText().toString();


                            //NETWORK CALL HERE

                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("msisdnP", prefs.getString("msisdn", ""));
                            userMap.put("name", dependantName);
                            userMap.put("msisdnD", Utils.getProperPhoneNumber(depenantPhoneNumber, "254"));
                            userMap.put("upperLimit", amountEntry);


                            HashMap<String, Object> inputMap = new HashMap<>();
                            inputMap.put("input", userMap);


                            HashMap<String, Object> map = new HashMap<>();
                            map.put("variables", inputMap);
                            map.put("query", "mutation relayAddDependant($input: RelayAddDependantInput!) {relayAddDependant(input: $input) {dependant {name}}}");

                            registerDependant(map);//networkcall

                        }


                    }
                })
                .negativeText(R.string.cancel);



        materialDialog = builder.build();
        customerInputAmount = materialDialog.getCustomView().findViewById(R.id.limit);
        customerInputPhoneNumber = materialDialog.getCustomView().findViewById(R.id.custom_input);
        customerInputName = materialDialog.getCustomView().findViewById(R.id.custom_input_name);

        TextView advice = materialDialog.getCustomView().findViewById(R.id.addDependant);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            advice.setText(Html.fromHtml(getString(R.string.clear_loan), Html.FROM_HTML_MODE_LEGACY));
        } else {
            advice.setText(Html.fromHtml(getString(R.string.clear_loan)));
        }


        RadioButton phoneNumber = materialDialog.getCustomView().findViewById(R.id.phoneNumber);
        phoneNumber.setText(getString(R.string.plain_string, "Select from contacts"));


        RadioButton userNumber = materialDialog.getCustomView().findViewById(R.id.manual);
        userNumber.setText(getString(R.string.plain_string, "Enter Contact Manually"));

        RadioGroup radioGroup = materialDialog.getCustomView().findViewById(R.id.contacts);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup r, int i) {
                switch (i) {
                    case R.id.phoneNumber:
                        isManual = false;
                        materialDialog.getCustomView().findViewById(R.id.custom_input).setVisibility(View.GONE);
                        materialDialog.getCustomView().findViewById(R.id.custom_input_name).setVisibility(View.GONE);
                        break;
                    case R.id.manual:
                        isManual = true;
                        materialDialog.getCustomView().findViewById(R.id.custom_input).setVisibility(View.VISIBLE);
                        materialDialog.getCustomView().findViewById(R.id.custom_input_name).setVisibility(View.VISIBLE);
                        break;


                }
            }
        });




        materialDialog.show();
    }

    @WithPermissions(permissions = {Manifest.permission.READ_CONTACTS})
    public void fetchContacts() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // User may have declined earlier, ask Android if we should show him a reason
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale("This permission is needed to continue.")) {
                    // show an explanation to the user
                    // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
                } else {
                    // request the permission.
                    // CALLBACK_NUMBER is a integer constants
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CALLBACK_NUMBER);
                    // The callback method gets the result of the request.
                }
            } else {
                ToastKt.toast(this, "Contact Permission is needed", Toast.LENGTH_SHORT);
            }
        } else {
            // got permission use it
            collectContacts();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALLBACK_NUMBER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, do your work....
                    collectContacts();
                }
                return;
            }
            // other 'case' statements for other permssions
        }
    }

    private void collectContacts() {

        new MultiContactPicker.Builder(this) //Activity/fragment context
                //.theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_SINGLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .setActivityAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out) //Optional - default: No animation overrides
                .showPickerForResult(CONTACT_PICKER_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                List<ContactResult> results = MultiContactPicker.obtainResult(data);

                for (ContactResult r : results) {

                    Log.d("DATA", r.getDisplayName() + "Phone :" + r.getPhoneNumbers() + "name");
                    //NETWORK CALL HERE

                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("msisdnP", prefs.getString("msisdn", ""));
                    userMap.put("name", r.getDisplayName());
                    userMap.put("msisdnD", Utils.getProperPhoneNumber(r.getPhoneNumbers().get(0).getNumber(), "254"));
                    userMap.put("upperLimit", amountEntry);


                    HashMap<String, Object> inputMap = new HashMap<>();
                    inputMap.put("input", userMap);


                    HashMap<String, Object> map = new HashMap<>();
                    map.put("variables", inputMap);
                    map.put("query", "mutation relayAddDependant($input: RelayAddDependantInput!) {relayAddDependant(input: $input) {dependant {name}}}");

                    registerDependant(map);//networkcall


                }
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }

    private void subscribeUi(DependantsViewModel viewModel) {

//        if (materialDialog.isShowing())
//            materialDialog.dismiss();


        SharedPreferences prefs = Utils.userDetails(getContext());

        HashMap<String, Object> dependantMap = new HashMap<>();
        dependantMap.put("msisdnP", prefs.getString("msisdn", ""));


        HashMap<String, Object> map = new HashMap<>();
        map.put("variables", dependantMap);
        map.put("query", "query dependant($msisdnP:String!) {dependant(msisdnP: $msisdnP)  {name upperLimit limitBalance}}");


        viewModel.getListOfDependants(map).observe(this, new Observer<List<DependantItem>>() {
            @Override
            public void onChanged(@Nullable List<DependantItem> dependantItems) {
                if (dependantItems.size() > 0) {

                    Log.i("TAG", "onChanged: I A HERE");
                    manageDependants.setIsLoading(false);
                    dependantsAdapter.setMyDependantsList(dependantItems);
                    manageDependants.progressbar.setVisibility(View.GONE);
                } else {
                    manageDependants.setIsLoading(false);
                }
                // espresso does not know how to wait for data binding's loop so we execute changes
                // sync.
                manageDependants.progressbar.setVisibility(View.GONE);

            }

        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    //"variables": {
    //        "input": {"name":"Agnes","msisdnD":"254722448625","upperLimit":"6000","msisdnP":"254724971796"}

    private void registerDependant(HashMap<String, Object> map) {


        PromptPopUpView promptPopUpView = new PromptPopUpView(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setPositiveButton("PROCESSING..", (dialogInterface, i) -> {
                    finish();
                })
                .setCancelable(false)
                .setView(promptPopUpView)
                .show();

        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setEnabled(false);
        button.setTextColor(getResources().getColor(R.color.colorBlackish));

        repo.inviteDependants(result -> {

            if (Objects.requireNonNull(result).containsKey(Status.SUCCESS)) {
                promptPopUpView.changeStatus(2, result.get(Status.SUCCESS));
            } else if (Objects.requireNonNull(result).containsKey(Status.FAIL)) {
                promptPopUpView.changeStatus(1, result.get(Status.FAIL));
            }

            button.setText(R.string.exit);
            button.setEnabled(true);
            button.setTextColor(getResources().getColor(R.color.colorText));
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (Objects.requireNonNull(result).containsKey(Status.SUCCESS)) {

                        dialog.dismiss();
                    } else if (Objects.requireNonNull(result).containsKey(Status.FAIL)) {
                        dialog.dismiss();
                    }
                    subscribeUi(viewModel);
                }
            });

        }, map);

    }


}

