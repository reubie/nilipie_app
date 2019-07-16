package com.mobipesa.nilipieapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mobipesa.nilipieapp.databinding.ActivityMainBinding;
import com.mobipesa.nilipieapp.dependants.DependantActivity;
import com.mobipesa.nilipieapp.helpers.Utils;
import com.mobipesa.nilipieapp.interfaces.ClickCallback;
import com.mobipesa.nilipieapp.payments.PayActivity;

import java.lang.reflect.Field;
import java.util.Objects;

import static com.mobipesa.nilipieapp.App.getContext;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private SharedPreferences prefs;
    private boolean isBalanceVisible;
    String balance;
    Context context;
    BottomNavigationView navigation;

    private ClickCallback mItemClickListener = new ClickCallback() {
        @Override
        public void onButtonClick() {

            if (!isBalanceVisible) {

                mBinding.accountBalance.setText(Objects.requireNonNull(getContext()).getString(R.string.plain_float_comma_kes, Float.parseFloat(balance)));
                mBinding.accountBalance.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                isBalanceVisible = true;

                new CountDownTimer(3000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        String blank = getResources().getString(R.string.hidden_amount);
                        mBinding.accountBalance.setText(blank);
                        Drawable img = getContext().getResources().getDrawable(R.drawable.ic_lock);
                        mBinding.accountBalance.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                        isBalanceVisible = false;
                    }
                }.start();

            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        prefs = Utils.userDetails(App.getContext());


        String name = (prefs.getString("first_name", ""));
        mBinding.accountName.setText(Objects.requireNonNull(getContext()).getString(R.string.plain_string, name));

//        navigation = findViewById(R.id.bottomBar);
//        disableShiftMode(navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        navigation.setSelectedItemId(R.id.navigation_home);


        String phone = (prefs.getString("msisdn", ""));
        mBinding.accountPhone.setText(Objects.requireNonNull(getContext()).getString(R.string.plain_string, phone));

        mBinding.notificationsImage.setOnClickListener(ant -> {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            startActivity(intent);
        });

        mBinding.pay.setOnClickListener(ant -> {
            Intent intent = new Intent(getContext(), PayActivity.class);
            startActivity(intent);
        });

        mBinding.receipts.setOnClickListener(ant -> {
            Intent intent = new Intent(getContext(), PayActivity.class);
            startActivity(intent);
        });


//        String user_type = (prefs.getString("is_dependant", ""));
//        if (Objects.requireNonNull(user_type).equals("true")) {
//            mBinding.dependants.setVisibility(View.VISIBLE);
//            mBinding.dependantsText.setVisibility(View.VISIBLE);

            mBinding.dependants.setOnClickListener(ant -> {
                Intent intent = new Intent(getContext(), DependantActivity.class);
                startActivity(intent);
            });
        }


    @SuppressLint("RestrictedApi")
    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    }


