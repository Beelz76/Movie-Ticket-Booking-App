package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LogInActivity extends AppCompatActivity {
    private TextInputEditText loginInput, passwordInput;
    private Button loginButton;
    private TextView signupText;
    private TextInputLayout loginLayout, passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();

        loginInput = findViewById(R.id.inputLogin);
        passwordInput = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.buttonLogin);
        signupText = findViewById(R.id.textSignup);
        loginLayout = findViewById(R.id.inputLayoutLogin);
        passwordLayout = findViewById(R.id.inputLayoutPassword);

        signupText.setOnClickListener(v -> navigateToSignUp());

        loginButton.setOnClickListener(v -> {
            String login = loginInput.getText().toString();
            String password = passwordInput.getText().toString();

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

            if (!login.isEmpty() && !password.isEmpty()) {
                attemptLogin(login, password);
            }
        });
    }

    private void attemptLogin(String login, String password) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"Login", "Password"};
            String[] data = {login, password};
            PutData putData = new PutData(
                    "http://192.168.1.75/scripts/User/login.php",
                    "POST", field, data
            );

            if (putData.startPut() && putData.onComplete()) {
                String result = putData.getResult();
                if (result.contains("Login Success")) {
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

    private void navigateToSignUp() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}

