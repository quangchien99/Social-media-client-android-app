
package com.qcp.facebookapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.qcp.facebookapp.R;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.model.Profile;
import com.qcp.facebookapp.utils.PasswordAuthentication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    public static final String PROFILE_NAME = "profileName";
    public static final String FONT_PATH = "fonts/Nabila.ttf";
    private TextInputEditText textInputEdtUsername, textInputEdtPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister, tvLogin;
    private String username, password;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textInputEdtUsername = findViewById(R.id.input_edt_user_name);
        textInputEdtPassword = findViewById(R.id.input_edt_password);
        btnLogin = findViewById(R.id.btn_login);
        tvLogin = findViewById(R.id.tv_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvRegister = findViewById(R.id.tv_register);
        username = textInputEdtUsername.getText().toString();
        password = textInputEdtPassword.getText().toString();
        setTypeface();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                login(textInputEdtUsername.getText().toString(), textInputEdtPassword.getText().toString());
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    public void login(String username, String password) {
        final List<Profile> profiles = new ArrayList<Profile>();
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<Profile> call = requestApi.getProfile(username);
        call.enqueue(new Callback<Profile>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.d("qcpTag", "Status Code onRes = " + response.code());
                profiles.add(response.body());
                profile = profiles.get(0);
                Log.d("qcpTag", profile.getEmail() + "");
                Log.d("qcpTag", profile.getPassword() + "");
                if (profile != null) {
                    if (PasswordAuthentication.checkPassword(textInputEdtPassword.getText().toString(), profile.getPassword())) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        SharedPreferences.Editor editor = getSharedPreferences(PROFILE_NAME, MODE_PRIVATE).edit();
                        editor.putString("profileName", profile.getProfileName());
                        editor.apply();
                        startActivity(intent);
                    } else {
                        loginError();
                    }
                } else {
                    loginError();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Waring")
                        .setMessage("Can't get user data")
                        .setCancelable(true)
                        .show();
                Log.d("qcpTag", t.getMessage() + "");
            }
        });
    }

    public void loginError() {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Notification")
                .setMessage("Invalid Username or Password")
                .setCancelable(true)
                .show();
    }

    private void setTypeface() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_PATH);
        tvLogin.setTypeface(typeface);
    }
}