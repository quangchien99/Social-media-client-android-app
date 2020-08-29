package com.qcp.facebookapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.qcp.facebookapp.R;
import com.qcp.facebookapp.activity.LoginActivity;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.model.Profile;
import com.qcp.facebookapp.utils.Validator;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileFragment extends Fragment {
    public static final String FONT_PATH = "fonts/Nabila.ttf";
    private TextView tvEditProfile;
    private TextInputEditText textInputEdtFirstName, textInputEdtLastName, textInputEdtEmail, textInputEdtPhone, textInputEdtAddress;
    private static ProfileFragment INSTANCE;
    private View view;
    private String profileName;
    private Profile profile;
    private Button btnUpdate;

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
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });
        return view;
    }

    private void updateInfo() {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<Profile> call = requestApi.getProfile(profileName);
        call.enqueue(new Callback<Profile>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                profile = response.body();
                if (textInputEdtFirstName.getText().toString().isEmpty() || textInputEdtLastName.getText().toString().isEmpty() || textInputEdtAddress.getText().toString().isEmpty() || textInputEdtEmail.getText().toString().isEmpty() || textInputEdtPhone.getText().toString().isEmpty()) {
                    showAlertDialog("Please fill in all information");
                } else if (!Validator.isValidEmail(textInputEdtEmail.getText().toString())) {
                    showAlertDialog("Incorrect email forrmat!");
                } else if (!Validator.isStringNumeric(textInputEdtPhone.getText().toString())) {
                    showAlertDialog("Incorrect phone forrmat!");
                } else {
                    profile.setFirstName(textInputEdtFirstName.getText().toString());
                    profile.setLastName(textInputEdtLastName.getText().toString());
                    profile.setEmail(textInputEdtEmail.getText().toString());
                    profile.setAddress(textInputEdtAddress.getText().toString());
                    profile.setPhoneNumber(textInputEdtPhone.getText().toString());
                    Call<ResponseBody> callUpdateProfile = requestApi.updateProfile(profile.getProfileName(), profile);
                    callUpdateProfile.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d("qcpp", "Update profile:" + response.code());
                            Toast.makeText(getContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            showAlertDialog("Something happened");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                showAlertDialog("Cant get data from server");
            }
        });
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Waring")
                .setMessage(message)
                .setCancelable(true)
                .show();
        Log.d("qcpTag", message + " ");
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
        tvEditProfile = view.findViewById(R.id.tv_edit_profile);
        btnUpdate = view.findViewById(R.id.btn_update);
    }

    private void setTypeface() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), FONT_PATH);
        tvEditProfile.setTypeface(typeface);
    }
}
