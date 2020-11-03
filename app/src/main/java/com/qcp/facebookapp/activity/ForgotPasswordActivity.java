package com.qcp.facebookapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.qcp.facebookapp.R;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.constant.Const;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.model.Profile;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Profile profile;
    public static final String FONT_PATH = "fonts/Nabila.ttf";
    private TextView tvForgotPassword;
    private Spinner spnQuestion;
    private EditText edtProfileName, edtAnswer;
    private Button btnContinue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        findViewById();
        setTypeface();
        setQuestion();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChangePassword();
            }

        });
    }

    private void goToChangePassword() {
        if (edtProfileName.getText().toString().isEmpty() || edtAnswer.getText().toString().isEmpty()) {
            showAlertDialog("Please fill in all information");
        } else {
            validateProfile();
        }
    }

    private void validateProfile() {
        final List<Profile> profiles = new ArrayList<Profile>();

        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<Profile> call = requestApi.getProfile(edtProfileName.getText().toString());
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.d("qcpTag", "Status Code onRes = " + response.code());
                profiles.add(response.body());
                profile = profiles.get(0);
                if (profile != null) {
                    Log.d("qcpp", spnQuestion.getSelectedItem().toString() + "");
                    if (spnQuestion.getSelectedItem().toString().equals(profile.getQuestion()) && edtAnswer.getText().toString().equals(profile.getAnswer())) {
                        SharedPreferences.Editor editor = getSharedPreferences(Const.PROFILE_NAME, MODE_PRIVATE).edit();
                        editor.clear();
                        editor.putString(Const.PROFILE_NAME, profile.getProfileName());
                        editor.apply();
                        Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
                        startActivity(intent);
                    } else {
                        showAlertDialog("The answer is not match. Please try again");
                    }
                } else {
                    showAlertDialog("Invalid Username");
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                showAlertDialog("ForgotpasswordActivity.validateProfile(): can't get data.");
            }
        });
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(ForgotPasswordActivity.this)
                .setTitle("Warning")
                .setMessage(message)
                .setCancelable(true)
                .show();
        Log.d("qcpTag", message + " ");
    }

    private void findViewById() {
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        edtProfileName = findViewById(R.id.edt_profile_name);
        edtAnswer = findViewById(R.id.input_edt_answer);
        spnQuestion = findViewById(R.id.spn_questions);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void setQuestion() {
        ArrayAdapter<CharSequence> spnQuestionAdapter = ArrayAdapter.createFromResource(this, R.array.question_array, R.layout.support_simple_spinner_dropdown_item);
        spnQuestionAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnQuestion.setAdapter(spnQuestionAdapter);
    }

    private void setTypeface() {
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), FONT_PATH);
        tvForgotPassword.setTypeface(typeface);
    }
}

