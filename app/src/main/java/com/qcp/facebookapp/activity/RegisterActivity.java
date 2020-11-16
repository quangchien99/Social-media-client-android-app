package com.qcp.facebookapp.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.qcp.facebookapp.R;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.model.Profile;
import com.qcp.facebookapp.utils.Validator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RegisterActivity extends AppCompatActivity {
    public static final String FONT_PATH = "fonts/Nabila.ttf";
    private TextView tvCreateAccount;
    private TextInputEditText inputEdtFirstName, inputEdtLastName, inputEdtUserName, inputEdtEmail, inputEdtPhoneNumber, inputEdtAddress, inputEdtPassword, inputEdtConfirmPassword, inputEdtAnswer;
    private Button btnRegister;
    private Spinner spnQuestion;
    private Profile profile;
    private List<Profile> profiles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewById();
        setTypeface();
        setQuestion();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (inputEdtFirstName.getText().toString().isEmpty() || inputEdtLastName.getText().toString().isEmpty() || inputEdtUserName.getText().toString().isEmpty() || inputEdtAddress.getText().toString().isEmpty() || inputEdtEmail.getText().toString().isEmpty() || inputEdtPhoneNumber.getText().toString().isEmpty() || inputEdtAnswer.getText().toString().isEmpty() || inputEdtPassword.getText().toString().isEmpty() || inputEdtConfirmPassword.getText().toString().isEmpty()) {
                    showAlertDialog("Please fill in all information");
                    Log.d("qcpTag", "BtnRegister - Missing data input");
                } else if (!Validator.isValidEmail(inputEdtEmail.getText().toString())) {
                    showAlertDialog("INVALID EMAIL");
                } else if (!Validator.isStringNumeric(inputEdtPhoneNumber.getText().toString())) {
                    showAlertDialog("INVALID PHONE NUMBER");
                } else if (!Validator.isValidatePassword(inputEdtPassword.getText().toString())) {
                    showAlertDialog("Password must have 1 capital,1 special character and more than 8 characters!");
                } else {
                    if (!inputEdtPassword.getText().toString().equals(inputEdtConfirmPassword.getText().toString())) {
                        showAlertDialog("Passwords are not matched - please try again!");
                    } else {
                        setProfile();
                        validateUserName();
                    }
                }
            }
        });
    }

    private void validateUserName() {
        Retrofit retrofit = APIClient.getClient();
        List<String> usernames = new ArrayList<String>();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<List<Profile>> call = requestApi.getAllProfiles();
        call.enqueue(new Callback<List<Profile>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                Log.d("qcpTag", "getAllProfileCode = " + response.code());
                profiles = response.body();
                for (Profile p : profiles) {
                    usernames.add(p.getProfileName());
                }
                if (usernames.contains(inputEdtUserName.getText().toString())) {
                    showAlertDialog("Profile Name already register? Try another one !");
                } else {
                    register();
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                showAlertDialog("RegisterActivity.validateUsername():Can't get data.");
            }
        });
    }

    private void register() {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<Profile> call = requestApi.createUser(profile);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.d("qcpTag", "Register code= " + response.code());
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                Toast.makeText(getApplicationContext(), "Registered Successfully! Please log in.", Toast.LENGTH_SHORT).show();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                showAlertDialog("Error Happened ! Try again later!");
            }
        });
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(RegisterActivity.this)
                .setTitle("Warning")
                .setMessage(message)
                .setCancelable(true)
                .show();
        Log.d("qcpTag", message + " ");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setProfile() {
        profile = new Profile();
        profile.setFirstName(inputEdtFirstName.getText().toString());
        profile.setLastName(inputEdtLastName.getText().toString());
        profile.setProfileName(inputEdtUserName.getText().toString());
        profile.setEmail(inputEdtEmail.getText().toString());
        profile.setPhoneNumber(inputEdtPhoneNumber.getText().toString());
        profile.setAddress(inputEdtAddress.getText().toString());
        profile.setAnswer(inputEdtAnswer.getText().toString());
        profile.setAddress(inputEdtAddress.getText().toString());
        profile.setPassword(inputEdtPassword.getText().toString());
        profile.setQuestion(spnQuestion.getSelectedItem().toString());
    }

    private void setQuestion() {
        ArrayAdapter<CharSequence> spnQuestionAdapter = ArrayAdapter.createFromResource(this, R.array.question_array, R.layout.support_simple_spinner_dropdown_item);
        spnQuestionAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnQuestion.setAdapter(spnQuestionAdapter);
    }

    private void findViewById() {
        tvCreateAccount = findViewById(R.id.tv_create_account);
        inputEdtFirstName = findViewById(R.id.input_edt_first_name);
        inputEdtLastName = findViewById(R.id.input_edt_last_name);
        inputEdtUserName = findViewById(R.id.input_edt_user_name);
        inputEdtAddress = findViewById(R.id.input_edt_address);
        inputEdtEmail = findViewById(R.id.input_edt_email);
        inputEdtPhoneNumber = findViewById(R.id.input_edt_phone);
        inputEdtPassword = findViewById(R.id.input_edt_password);
        inputEdtConfirmPassword = findViewById(R.id.input_edt_confirm_password);
        inputEdtAnswer = findViewById(R.id.input_edt_answer);
        spnQuestion = findViewById(R.id.spn_questions);
        btnRegister = findViewById(R.id.btn_register);
    }
    private void setTypeface() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), FONT_PATH);
        tvCreateAccount.setTypeface(typeface);
    }

}