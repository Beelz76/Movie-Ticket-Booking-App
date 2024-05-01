package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textLogin.setOnClickListener(v -> navigateToLogin());

        binding.buttonSignup.setOnClickListener(v -> {
            String fullname = binding.inputFullname.getText().toString();
            String email = binding.inputEmail.getText().toString();
            String login = binding.inputLogin.getText().toString();
            String password = binding.inputPassword.getText().toString();

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
            binding.inputLayoutFullname.setError("Заполните поле");
        } else {
            binding.inputLayoutFullname.setError(null);
        }

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
