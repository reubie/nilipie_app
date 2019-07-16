package com.mobipesa.nilipieapp.payments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.mobipesa.nilipieapp.App;
import com.mobipesa.nilipieapp.ProjectRepository;
import com.mobipesa.nilipieapp.R;
import com.mobipesa.nilipieapp.helpers.PromptPopUpView;
import com.mobipesa.nilipieapp.helpers.Status;
import com.mobipesa.nilipieapp.helpers.Utils;

import java.util.HashMap;
import java.util.Objects;

import static com.mobipesa.nilipieapp.App.getContext;

public class PayActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout textInputtranactionType, textInputamount, textInputphoneNumber, textInputtransactionId, textInputaccountNumber, textInputshortcode;
    private ProjectRepository repo;
    private String msisdn;
    private String channel;
    SharedPreferences prefs;

//    private static final String[] paths = {"C2B",
//            "B2B",
//            "B2C",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        repo = ((App) Objects.requireNonNull(this).getApplicationContext()).getRepository();
        prefs = Utils.userDetails(this);


        textInputtranactionType = findViewById(R.id.pay_text);
        textInputamount = findViewById(R.id.amount);
        textInputphoneNumber = findViewById(R.id.phoneNumber);
        textInputshortcode = findViewById(R.id.short_code);
        textInputaccountNumber = findViewById(R.id.account_number);
        textInputtransactionId = findViewById(R.id.pay_code);


        findViewById(R.id.buttonPay).setOnClickListener(this);
    }

    public void onRadioButtonClick(View view) {

        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.mpesa:
                if (checked) {
                    channel = "mpesa";
                }
                break;
            case R.id.airtel:
                if (checked) {
                    channel = "airtell";


                }
                break;
            case R.id.bank:
                if (checked) {
                    channel = "bank";
                }
        }
    }


    private void pay() {

        String transactionType = textInputtranactionType.getEditText().getText().toString().trim();
        String amount = textInputamount.getEditText().getText().toString().trim();
        String msisdn = textInputphoneNumber.getEditText().getText().toString().trim();
        String shortCode = textInputshortcode.getEditText().getText().toString().trim();
        String accountNo = textInputaccountNumber.getEditText().getText().toString().trim();
        String transactionId = textInputtransactionId.getEditText().getText().toString().trim();


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

        Log.i("TAG", "pay: " + prefs.getString("msisdn", ""));


        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("msisdnP", prefs.getString("msisdn", ""));
        userMap.put("msisdnD", Utils.getProperPhoneNumber(msisdn, "254"));
        userMap.put("transactionType", transactionType);
        userMap.put("amount", amount);
        userMap.put("shortCode", shortCode);
        userMap.put("accountNo", accountNo);
        userMap.put("identifier", transactionId);
        userMap.put("channel", channel);


        HashMap<String, Object> inputMap = new HashMap<>();
        inputMap.put("input", userMap);


        HashMap<String, Object> map = new HashMap<>();
        map.put("variables", inputMap);
        map.put("query", "mutation relayAddTransaction($input: RelayAddTransactionInput!) {relayAddTransaction(input: $input) {transaction {transactionType}}}");

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


        repo.customersPay(result -> {

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
                        Intent intent = new Intent(getContext(), com.mobipesa.nilipieapp.MainActivity.class);
                        startActivity(intent);
                    } else if (Objects.requireNonNull(result).containsKey(Status.FAIL)) {
                        Intent intent = new Intent(getContext(), com.mobipesa.nilipieapp.payments.PayActivity.class);
                        dialog.dismiss();
                    }
                }
            });

        }, map);

    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonPay:

                //resetPassword();
                Log.i("TAG", "onClick: ");
                pay();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}




