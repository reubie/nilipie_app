package com.mobipesa.nilipieapp.login;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mobipesa.nilipieapp.App;
import com.mobipesa.nilipieapp.ProjectRepository;
import com.mobipesa.nilipieapp.R;
import com.mobipesa.nilipieapp.helpers.Status;
import com.mobipesa.nilipieapp.helpers.Utils;
import com.poovam.pinedittextfield.PinField;

import java.util.HashMap;
import java.util.Objects;


/**
 * Created by Agnes on1 6/3/18.
 */
public class OTPFragment extends Fragment {

    private TextView timer;
    private TextView resend;
    private CountDownTimer countDownTimer;
    private String msisdn,otp;

    private ProjectRepository repo;
    public OTPFragment() {
        // Required empty public constructor
    }

//    public static OTPFragment newInstance() {
//
//        Bundle args = new Bundle();
//        OTPFragment fragment = new OTPFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = ((App) Objects.requireNonNull(getActivity()).getApplicationContext()).getRepository();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            msisdn = bundle.getString("msisdn");

        }


//        repo.generateOTP(new ProgressInterface<Status>() {
//            @Override
//            public void onResult(@NonNull Status data) {
//                if (data != Status.SUCCESS) {
//
//                    Toast.makeText(getActivity(), "Failed sending OTP. Try again", Toast.LENGTH_SHORT).show();
//
//                    Fragment phoneFragment = new com.mobipesa.nilipieapp.login.PhoneFragment();
//                    FragmentInterface fc = (FragmentInterface) getActivity();
//                    Objects.requireNonNull(fc).replaceFragment(phoneFragment);
//                }
//            }
//        }, map);

//
//       /* SmsRetrieverClient client = SmsRetriever.getClient(App.getContext());
//
//        // Starts SmsRetriever, waits for ONE matching SMS message until timeout
//        // (5 minutes).
//        Task<Void> task = client.startSmsRetriever();
//
//        // Listen for success/failure of the start Task.
//        task.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void v) {
//                Log.d("TAG", "Successfully started retriever");
//            }
//        });
//
//        task.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("TAG", "Failed to start retriever");
//            }
//        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment_account
        return inflater.inflate(R.layout.activity_register_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        timer = view.findViewById(R.id.timer_secs);
        resend = view.findViewById(R.id.resend_otp);
        TextView user = view.findViewById(R.id.prompt);
        PinField otp = view.findViewById(R.id.otp);
        otp.requestFocus();
        resend.setEnabled(false);
        user.setText(getString(R.string.code_prompt, msisdn));
        countDownTimer = new CountDownTimer(61000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                timer.setVisibility(View.GONE);
                resend.setEnabled(true);
                resend.setTextColor(ContextCompat.getColor(App.getContext(), R.color.colorPrimary));
            }
        }.start();

        otp.setOnTextCompleteListener(s -> {
            makeNetworkRequest(s);
            return false;
        });

        //Show keyboard immediately
        InputMethodManager imm = (InputMethodManager) App.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void makeNetworkRequest(String pin) {

        HashMap<String, Object> OTPMap = new HashMap<>();
        OTPMap.put("msisdn", Utils.getProperPhoneNumber(msisdn,"254"));
        OTPMap.put("otp", pin.trim());

        HashMap<String, Object> map = new HashMap<>();
        map.put("query","query($msisdn: String!,$otp: String!){otp(msisdn: $msisdn,otp: $otp){msisdn}}");
        map.put("variables",OTPMap);

        repo.verifyOTP(map).observe(this, new Observer<HashMap<Status, String>>() {
            @Override
            public void onChanged(@NonNull HashMap<Status, String> result) {
                if (result.containsKey(Status.SUCCESS)) {
                    //FragmentInterface fc = (FragmentInterface) getActivity();
                    //Objects.requireNonNull(fc).replaceFragment(new PinFragment());
                    Intent intent = new Intent(getContext(), RegistrationActivity.class);
                    intent.putExtra("msisdn", msisdn);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Verification failed. Enter correct OTP or Resend OTP", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}