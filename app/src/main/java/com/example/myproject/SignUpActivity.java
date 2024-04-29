package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText fullnameInput, emailInput, loginInput, passwordInput;
    private Button signupButton;
    private TextView loginText;
    private TextInputLayout fullnameLayout, emailLayout, loginLayout, passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullnameInput = findViewById(R.id.inputFullname);
        emailInput = findViewById(R.id.inputEmail);
        loginInput = findViewById(R.id.inputLoginSignup);
        passwordInput = findViewById(R.id.inputPasswordSignup);
        signupButton = findViewById(R.id.buttonSignup);
        loginText = findViewById(R.id.textLoginSignup);
        fullnameLayout = findViewById(R.id.inputLayoutFullname);
        emailLayout = findViewById(R.id.inputLayoutEmail);
        loginLayout = findViewById(R.id.inputLayoutLoginSignup);
        passwordLayout = findViewById(R.id.inputLayoutPasswordSignup);

        loginText.setOnClickListener(v -> navigateToLogin());

        signupButton.setOnClickListener(v -> {
            String fullname = fullnameInput.getText().toString();
            String email = emailInput.getText().toString();
            String login = loginInput.getText().toString();
            String password = passwordInput.getText().toString();

            validateInputs(fullname, login, password);

            if (!fullname.isEmpty() && !login.isEmpty() && !password.isEmpty()) {
                attemptSignUp(login, fullname, email, password);
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(intent);
        finish();
    }

    private void validateInputs(String fullname, String login, String password) {
        if (fullname.isEmpty()) {
            fullnameLayout.setError("Заполните поле");
        } else {
            fullnameLayout.setError(null);
        }

        if (login.isEmpty()) {
            loginLayout.setError("Заполните поле");
        } else {
            loginLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Заполните поле");
        } else {
            passwordLayout.setError(null);
        }
    }

    private void attemptSignUp(String login, String fullname, String email, String password) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"Login", "Fullname", "Email", "Password"};
            String[] data = {login, fullname, email, password};
            PutData putData = new PutData(
                    "http://192.168.1.75/scripts/User/signup.php",
                    "POST", field, data
            );

            if (putData.startPut() && putData.onComplete()) {
                String result = putData.getResult();
                if (result.contains("Sign Up Success")) {
                    String userId = result.split(" ")[0];
                    saveUserId(userId);
                    navigateToMainActivity();
                } else {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserId(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", userId);
        editor.apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
