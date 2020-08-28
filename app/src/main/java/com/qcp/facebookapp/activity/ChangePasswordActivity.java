package com.qcp.facebookapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.qcp.facebookapp.R;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.constant.Const;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.model.Profile;
import com.qcp.facebookapp.utils.PasswordAuthentication;
import com.qcp.facebookapp.utils.Validator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePasswordActivity extends AppCompatActivity {
    public static final String FONT_PATH = "fonts/Nabila.ttf";
    private TextView tvChangePassword;
    private EditText edtPassword, edtConfirmPassword;
    private Button btChangePassword;
    private Profile profile;
    private String profileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        findViewById();
        getProfileName();
        setTypeface();
        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    private void getProfileName() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Const.PROFILE_NAME, Context.MODE_PRIVATE);
        profileName = prefs.getString(Const.PROFILE_NAME, "No name defined");
    }

    public void changePassword() {
        if (edtPassword.getText().toString().isEmpty() || edtConfirmPassword.getText().toString().isEmpty()) {
            showAlertDialog("Please fill in all information");
        } else if (!Validator.isValidatePassword(edtPassword.getText().toString())) {
            showAlertDialog("Password must be from 8 to 24 characters, have 1 Capital, 1 Special character!");
        } else if (!edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
            showAlertDialog("Password not match");
        } else {
            Retrofit retrofit = APIClient.getClient();
            RequestAPI requestApi = retrofit.create(RequestAPI.class);
            Call<Profile> call = requestApi.getProfile(profileName);
            call.enqueue(new Callback<Profile>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call<Profile> call, Response<Profile> response) {
                    profile = response.body();
                    Log.d("qcp", "ChangePasswordAct: check oldpassword is:" + profile.getPassword());
//                    if (edtPassword.getText().toString().equals(profile.getPassword())) {
                    if (PasswordAuthentication.checkPassword(edtPassword.getText().toString(), profile.getPassword())) {
                        showAlertDialog("Password is the same with the current one! Try another !");
                    } else {
                        //change to server
                        profile.setPassword(edtPassword.getText().toString());
                        Call<ResponseBody> callChangePw = requestApi.updateProfile(profile.getProfileName(), profile);
                        callChangePw.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d("qcpp", "ChangePassword:" + response.code());
                                Toast.makeText(getApplicationContext(), "Change Password Successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
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
    }


    private void showAlertDialog(String message) {
        new AlertDialog.Builder(ChangePasswordActivity.this)
                .setTitle("Waring")
                .setMessage(message)
                .setCancelable(true)
                .show();
        Log.d("qcpTag", message + " ");
    }

    private void findViewById() {
        tvChangePassword = findViewById(R.id.tv_change_password);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_cf_password);
        btChangePassword = findViewById(R.id.btn_change_password);
    }


    private void setTypeface() {
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), FONT_PATH);
        tvChangePassword.setTypeface(typeface);
    }
}
