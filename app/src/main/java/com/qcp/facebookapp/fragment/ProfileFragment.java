package com.qcp.facebookapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.qcp.facebookapp.R;
import com.qcp.facebookapp.activity.HomeActivity;
import com.qcp.facebookapp.activity.LoginActivity;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.model.Profile;
import com.qcp.facebookapp.utils.PasswordAuthentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileFragment extends Fragment {
    public static final String FONT_PATH = "fonts/Nabila.ttf";
    private TextView tvEditProfile;
    private TextInputEditText textInputEdtFirstName, textInputEdtLastName, textInputEdtEmail, textInputEdtPhone, textInputEdtAddress, textInputEdtPassword, textInputEdtConfirmPassword;
    private static ProfileFragment INSTANCE;
    private View view;
    private String profileName;
    private Profile profile;

    public static ProfileFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ProfileFragment();
        }
        return INSTANCE;
    }

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        findViewById();
        setTypeface();
        profileName = getProfileName();
        setInfo();
        return view;
    }

    private void setInfo() {
        final List<Profile> profiles = new ArrayList<Profile>();
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<Profile> call = requestApi.getProfile(profileName);
        call.enqueue(new Callback<Profile>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.d("qcpTag", "Status Code onRes = " + response.code());
                profiles.add(response.body());
                profile = profiles.get(0);
                if (profile != null) {
                    Log.d("qcpTag", "getProfile = " + profile.getFirstName());
                    textInputEdtFirstName.setText(profile.getFirstName());
                    textInputEdtLastName.setText(profile.getLastName());
                    textInputEdtEmail.setText(profile.getEmail());
                    textInputEdtAddress.setText(profile.getAddress());
                    textInputEdtPhone.setText(profile.getPhoneNumber());
                    textInputEdtPassword.setText(profile.getPassword());
                    textInputEdtConfirmPassword.setText(profile.getPassword());
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Waring")
                            .setMessage("Can't get user data - check")
                            .setCancelable(true)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Waring")
                        .setMessage("Can't get user data")
                        .setCancelable(true)
                        .show();
                Log.d("qcpTag", t.getMessage() + "");
            }
        });
    }

    private String getProfileName() {
        SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PROFILE_NAME, Context.MODE_PRIVATE);
        String profileName = prefs.getString("profileName", "No name defined");
        Log.d("qcpTag", "getProfile" + profileName + "check");
        return profileName;
    }

    private void findViewById() {
        textInputEdtFirstName = view.findViewById(R.id.text_input_edt_first_name);
        textInputEdtLastName = view.findViewById(R.id.text_input_edt_last_name);
        textInputEdtEmail = view.findViewById(R.id.text_input_edt_email);
        textInputEdtAddress = view.findViewById(R.id.text_input_edt_address);
        textInputEdtPhone = view.findViewById(R.id.text_input_edt_phone);
        textInputEdtPassword = view.findViewById(R.id.text_input_edt_password);
        textInputEdtConfirmPassword = view.findViewById(R.id.text_input_edt_cf_password);
        tvEditProfile = view.findViewById(R.id.tv_edit_profile);

    }

    private void setTypeface() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), FONT_PATH);
        tvEditProfile.setTypeface(typeface);
    }
}
