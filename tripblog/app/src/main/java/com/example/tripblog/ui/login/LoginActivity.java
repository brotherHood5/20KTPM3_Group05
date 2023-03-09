package com.example.tripblog.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripblog.R;
import com.example.tripblog.api.RetrofitClient;
import com.example.tripblog.api.services.AuthService;
import com.example.tripblog.databinding.ActivityLoginBinding;
import com.example.tripblog.model.AuthResponse;
import com.example.tripblog.ui.signup.SignupActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputLayout editEmailLayout, editPasswordLayout;
    TextInputEditText editEmail, editPassword;
    Button loginBtn;
    TextView forgotPasswordTxt, signupTxt;
    private final Retrofit retrofitClient = RetrofitClient.getInstance();

    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editEmailLayout = (TextInputLayout) findViewById(R.id.editEmailLayout);
        editEmail = (TextInputEditText) findViewById(R.id.editEmail);

        editPasswordLayout = (TextInputLayout) findViewById(R.id.editPasswordLayout);
        editPassword = (TextInputEditText) findViewById(R.id.editPassword);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(LoginActivity.this);

        forgotPasswordTxt = (TextView) findViewById(R.id.forgotPasswordTxt);
        forgotPasswordTxt.setOnClickListener(LoginActivity.this);
        signupTxt = (TextView) findViewById(R.id.signupTxt);
        signupTxt.setOnClickListener(LoginActivity.this);

        editEmail.addTextChangedListener(new ValidationTextWatcher(editEmail));
        editPassword.addTextChangedListener(new ValidationTextWatcher(editPassword));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LOGIN", "Login onResume");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signupTxt) {
            goToSignup();
        }
        else if (view.getId() == R.id.forgotPasswordTxt) {
            goToForgotPassword();
        }
        else if (view.getId() == R.id.loginBtn) {
            if (!validateEmail()) return;
            if (!validatePassword()) return;
            login();
        }
    }
    private void requestFocus(View v) {
        if  (v.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateEmail() {
        String email = editEmail.getText().toString();
        if (email.trim().isEmpty()) {
            editEmailLayout.setError(null);
        }
        else {
            boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if (isValidEmail) {
                editEmailLayout.setError(null);
            }
            else {
                editEmailLayout.setError("Invalid E-mail address");
                requestFocus(editEmail);
                return false;
            }
        }

        return true;
    }

    private boolean validatePassword() {
        String password = editPassword.getText().toString();
        if (password.trim().isEmpty()) {
            editPasswordLayout.setError(null);
        }else if(password.length() < 6){
            editPasswordLayout.setError("Password can't be less than 6 characters");
            requestFocus(editPassword);
            return false;
        }
        else {
            editPasswordLayout.setError(null);
        }
        return true;
    }

    private void login() {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        if (email.isEmpty()) {
            editEmailLayout.setError("Email is required");
            requestFocus(editEmail);
            return;
        }

        if (password.isEmpty()) {
            editPasswordLayout.setError("Password is required");
            requestFocus(editPassword);
            return;
        }

        AuthService authService = retrofitClient.create(AuthService.class);
        authService.login(email, password).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                AuthResponse body = response.body();
                if (body.getStatus().equals( "failure")){
                    Log.d("LOGIN", body.getStatus());
                    editEmailLayout.setError(body.getError().getAsString());
                    return;
                }
                else if (body.getStatus().equals( "error")) {
                }

                // TODO: Need to save jwt token
                Toast.makeText(LoginActivity.this, body.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("LOGIN", t.toString());
            }
        });

    }

    private void goToForgotPassword() {
        // TODO: Implement go to forgot password activity
    }

    private void goToSignup() {
        // TODO: Implement go to signup activity
        Intent signup = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(signup);
    }

    private class ValidationTextWatcher implements TextWatcher {
        private View view;
        private ValidationTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.editPassword:
                    editPasswordLayout.setError(null);
                    break;
                case R.id.editEmail:
                    editEmailLayout.setError(null);
                    break;
            }
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editPassword:
                    validatePassword();
                    break;
                case R.id.editEmail:
                    validateEmail();
                    break;
            }
        }
    }
}