package com.mobipesa.nilipieapp.login;

import android.arch.lifecycle.Observer;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobipesa.nilipieapp.App;
import com.mobipesa.nilipieapp.ProjectRepository;
import com.mobipesa.nilipieapp.R;
import com.mobipesa.nilipieapp.helpers.Status;
import com.mobipesa.nilipieapp.helpers.Utils;
import com.mobipesa.nilipieapp.interfaces.FragmentInterface;

import java.util.HashMap;
import java.util.Objects;


public class PhoneFragment extends Fragment {

    TextView initialText;
    ProgressBar progressBar;
    Button button;
    private  String msisdn;
    private ProjectRepository repo;

    public PhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = ((App) Objects.requireNonNull(getActivity()).getApplicationContext()).getRepository();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment_account
        return inflater.inflate(R.layout.activity_register_phone, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialText = view.findViewById(R.id.initial_text);
        progressBar = view.findViewById(R.id.progressbar);
        button = view.findViewById(R.id.button);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorAccent));
            progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }

        button.setOnClickListener(this::onClick);
    }

    private EditText getPhoneInput() {
        return (EditText) getView().findViewById(R.id.phone_input);
    }



    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:

                //Validation
                if (Utils.checkIfEmptyString(getPhoneInput().getText().toString().trim()) ||
                        getPhoneInput().getText().toString().trim().length() < 9) {
                    getPhoneInput().setError("Invalid input");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                button.setEnabled(false);
                button.setBackgroundResource(R.drawable.disabled_round_corner_rect);
                makeNetworkCall(getPhoneInput());

                break;
        }
    }

    public void makeNetworkCall(EditText input) {
        final String msisdn = input.getText().toString().trim();

        if (msisdn.isEmpty()) {
            input.setError("Mobile number is required");
            input.requestFocus();
            return;
        }

        HashMap<String, Object> msisdnMap = new HashMap<>();
        msisdnMap.put("msisdn", Utils.getProperPhoneNumber(msisdn,"254"));

        HashMap<String, Object> map = new HashMap<>();
        map.put("query", "query($msisdn: String!){users(msisdn: $msisdn){firstName}}");
        map.put("variables",msisdnMap);


        repo.checkifMember(map).observe(this, new Observer<HashMap<Status, String>>() {
            @Override
            public void onChanged(@NonNull HashMap<Status, String> result) {
                if (result.containsKey(Status.SUCCESS)) {

                progressBar.setVisibility(View.GONE);
                button.setEnabled(true);
                button.setBackgroundResource(R.drawable.blue_round_corner_rect);

                Bundle args = new Bundle();
                args.putString("msisdn", msisdn);

                Fragment enterFragment = new PinFragment();
                enterFragment.setArguments(args);

                FragmentInterface fc = (FragmentInterface) getActivity();
                Objects.requireNonNull(fc).replaceFragment(enterFragment);

            }else if(result.containsKey(Status.FAIL)) {

                progressBar.setVisibility(View.GONE);
                button.setEnabled(true);
                button.setBackgroundResource(R.drawable.blue_round_corner_rect);

                Bundle args = new Bundle();
                args.putString("msisdn", msisdn);
                Fragment otpFragment = new OTPFragment();
                otpFragment.setArguments(args);

                FragmentInterface fc = (FragmentInterface) getActivity();
                Objects.requireNonNull(fc).replaceFragment(otpFragment);
            }else {
                //Status.CONNECTION_ERROR

                progressBar.setVisibility(View.GONE);
                button.setEnabled(true);
                button.setBackgroundResource(R.drawable.blue_round_corner_rect);

                }

            }

        });
    }


}
