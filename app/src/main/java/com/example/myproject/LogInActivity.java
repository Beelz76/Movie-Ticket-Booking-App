package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.databinding.ActivityLogInBinding;

public class LogInActivity extends AppCompatActivity {
    private ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();

        binding.textSignup.setOnClickListener(v -> navigateToSignUp());

        binding.buttonLogin.setOnClickListener(v -> {
            String login = binding.inputLogin.getText().toString();
            String password = binding.inputPassword.getText().toString();

            if (login.isEmpty()) {
                binding.inputLayoutLogin.setError("Заполните поле");
            } else {
                binding.inputLayoutLogin.setError(null);
            }

            if (password.isEmpty()) {
                binding.inputLayoutPassword.setError("Заполните поле");
            } else {
                binding.inputLayoutPassword.setError(null);
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
                    Toast.makeText(getApplicationContext(), "Неправильный логин или пароль", Toast.LENGTH_SHORT).show();
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

