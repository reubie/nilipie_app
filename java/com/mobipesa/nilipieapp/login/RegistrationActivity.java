package com.mobipesa.nilipieapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mobipesa.nilipieapp.App;
import com.mobipesa.nilipieapp.ProjectRepository;
import com.mobipesa.nilipieapp.R;
import com.mobipesa.nilipieapp.helpers.PromptPopUpView;
import com.mobipesa.nilipieapp.helpers.Status;
import com.mobipesa.nilipieapp.helpers.Utils;

import java.util.HashMap;
import java.util.Objects;

import static com.mobipesa.nilipieapp.App.getContext;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout textInputfirstName, textInputlastName, textInputphoneNumber, textInputid;
    private ProjectRepository repo;
    private EditText editTextpassword;
    private String msisdn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regitration);
        repo = ((App) Objects.requireNonNull(this).getApplicationContext()).getRepository();

        Bundle getBundle = getIntent().getExtras();
        if (getBundle != null) {
            msisdn = getBundle.getString("msisdn");
            Log.i("TAG", "onCreate: " + msisdn);

        }

        textInputfirstName = findViewById(R.id.first_name);
        textInputlastName = findViewById(R.id.last_name);
        textInputphoneNumber = findViewById(R.id.phoneNumber);
        textInputid = findViewById(R.id.id);
      editTextpassword = findViewById(R.id.pin_input);


        findViewById(R.id.buttonFinish).setOnClickListener(this);


    }        String id = textInputid.getEditText().getText().toString().trim();


    private void registerCall() {

        String firstName = textInputfirstName.getEditText().getText().toString().trim();
        String lastName = textInputlastName.getEditText().getText().toString().trim();
        String msisdn = textInputphoneNumber.getEditText().getText().toString().trim();
        String password = editTextpassword.getText().toString().trim();
        String id = textInputid.getEditText().getText().toString().trim();

//        {
//            "query": "mutation relayAddUser($input: RelayAddUserInput!) {relayAddUser(input: $input) {user {firstName}}}",
//                "variables": {
//            "input": {"firstName":"Agnes","lastName":"kavata","idNo":"30497433","msisdn":"254724971796"}
//        }
//        }

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("msisdn", Utils.getProperPhoneNumber(msisdn,"254"));
        userMap.put("firstName",firstName);
        userMap.put("lastName",lastName);
        userMap.put("idNo", id);
        userMap.put("password", password);


        HashMap<String, Object> inputMap = new HashMap<>();
        inputMap.put("input", userMap);


        HashMap<String, Object> map = new HashMap<>();
        map.put("variables", inputMap);
        map.put("query", "mutation relayAddUser($input: RelayAddUserInput!) {relayAddUser(input: $input) {user {firstName}}}");

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



        repo.customersRegister(result -> {

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
                        Intent intent = new Intent(getContext(), com.mobipesa.nilipieapp.login.LoginActivity.class);
                        startActivity(intent);
                    } else if (Objects.requireNonNull(result).containsKey(Status.FAIL)) {
                        dialog.dismiss();
                    }
                }
            });

        }, map);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonFinish:

                //resetPassword();
                Log.i("TAG", "onClick: ");
                registerCall();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
