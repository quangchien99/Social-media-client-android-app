package com.qcp.facebookapp.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qcp.facebookapp.R;

public class ChangePasswordActivity extends AppCompatActivity {
    public static final String FONT_PATH = "fonts/Nabila.ttf";
    TextView tvChangePassword;
    EditText edtPassword, edtConfirmPassword, edtProfileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById();
        setTypeface();
    }

    private void findViewById() {
        tvChangePassword = findViewById(R.id.tv_change_password);
        edtProfileName = findViewById(R.id.edt_profile_name);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.text_input_edt_cf_password);
    }


    private void setTypeface() {
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), FONT_PATH);
        tvChangePassword.setTypeface(typeface);
    }
}
