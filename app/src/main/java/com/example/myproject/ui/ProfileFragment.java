package com.example.myproject.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myproject.LogInActivity;
import com.example.myproject.PutData;
import com.example.myproject.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {
    private TextInputLayout fullnameLayout, emailLayout, loginLayout, passwordLayout, confirmPasswordLayout;
    private TextInputEditText fullnameInput, emailInput, loginInput, passwordInput, confirmPasswordInput;
    private Button editButton, saveButton, cancelButton;
    private ImageView logoutImage;
    private String currentFullName, currentEmail, currentLogin, currentPassword, currentConfirmPassword;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");

        if (userId.isEmpty()) {
            hideAllViews(getView());
            navigateToLogin();
        }

        fullnameInput = view.findViewById(R.id.inputFullnameProfile);
        emailInput = view.findViewById(R.id.inputEmailProfile);
        loginInput = view.findViewById(R.id.inputLoginProfile);
        passwordInput = view.findViewById(R.id.inputPasswordProfile);
        confirmPasswordInput = view.findViewById(R.id.inputConfirmPasswordProfile);
        editButton = view.findViewById(R.id.buttonEdit);
        cancelButton = view.findViewById(R.id.buttonCancel);
        saveButton = view.findViewById(R.id.buttonSave);
        logoutImage = view.findViewById(R.id.imageLogout);
        fullnameLayout = view.findViewById(R.id.inputLayoutFullnameProfile);
        emailLayout = view.findViewById(R.id.inputLayoutEmailProfile);
        loginLayout = view.findViewById(R.id.inputLayoutLoginProfile);
        passwordLayout = view.findViewById(R.id.inputLayoutPasswordProfile);
        confirmPasswordLayout = view.findViewById(R.id.inputLayoutConfirmPasswordProfile);

        setFieldsEnabled(false);
        setFieldsVisibility(false);
        getUserInfo(userId);

        logoutImage.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Вы уверены, что хотите выйти?")
                    .setPositiveButton("Да", (dialog, id) -> navigateToLogin())
                    .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        editButton.setOnClickListener(v -> {
            setFieldsEnabled(true);
            setFieldsVisibility(true);
        });

        cancelButton.setOnClickListener(v -> {
            fullnameInput.setText(currentFullName);
            emailInput.setText(currentEmail);
            loginInput.setText(currentLogin);
            setFieldsEnabled(false);
            setFieldsVisibility(false);
        });

        saveButton.setOnClickListener(v -> {
            String fullname, email, login, password, confirmPassword;
            fullname = fullnameInput.getText().toString();
            email = emailInput.getText().toString();
            login = loginInput.getText().toString();
            password = passwordInput.getText().toString();
            confirmPassword = confirmPasswordInput.getText().toString();

            validateInputs(fullname, login, password, confirmPassword);

            if (!login.isEmpty() && !password.isEmpty() && !fullname.isEmpty() && !confirmPassword.isEmpty()) {
                updateUserInfo(userId, fullname, email, login, password);
                setFieldsEnabled(false);
                setFieldsVisibility(false);
            }
        });
    }

    private void validateInputs(String fullname, String login, String password, String confirmPassword) {
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

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Пароль не совпадает");
        } else {
            confirmPasswordLayout.setError(null);
        }
    }

    private void getUserInfo(String userId) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"UserId"};
            String[] data = {userId};
            PutData putData = new PutData(
                    "http://192.168.1.75/scripts/User/get_info.php",
                    "POST", field, data
            );

            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    if (result.contains("Get User Info Success")) {
                        try {
                            JSONObject userInfo = new JSONObject(result);
                            fullnameInput.setText(userInfo.getString("Fullname"));
                            emailInput.setText(userInfo.getString("Email"));
                            loginInput.setText(userInfo.getString("Login"));

                            currentFullName = fullnameInput.getText().toString();
                            currentEmail = emailInput.getText().toString();
                            currentLogin = loginInput.getText().toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateUserInfo(String userId, String fullname, String email, String login, String password) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"UserId", "Fullname", "Email", "Login", "Password"};
            String[] data = {userId, fullname, email, login, password};
            PutData putData = new PutData(
                    "http://192.168.1.75/scripts/User/update_user.php",
                    "POST", field, data
            );

            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    if (result.contains("User Update Success")) {
                        currentFullName = fullname;
                        currentEmail = email;
                        currentLogin = login;
                        Toast.makeText(getActivity().getApplicationContext(), "Данные сохранены", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void setFieldsEnabled(boolean enabled) {
        fullnameInput.setEnabled(enabled);
        emailInput.setEnabled(enabled);
        loginInput.setEnabled(enabled);
        passwordInput.setEnabled(enabled);
        confirmPasswordInput.setEnabled(enabled);
    }

    private void setFieldsVisibility(boolean visibility) {
        if (visibility){
            passwordInput.setText("");
            confirmPasswordInput.setText("");
            passwordInput.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
            passwordLayout.setError(null);
            confirmPasswordInput.setVisibility(View.VISIBLE);
            confirmPasswordLayout.setVisibility(View.VISIBLE);
            confirmPasswordLayout.setError(null);
            cancelButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        } else {
            passwordInput.setText("");
            confirmPasswordInput.setText("");
            passwordInput.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.GONE);
            passwordLayout.setError(null);
            confirmPasswordInput.setVisibility(View.GONE);
            confirmPasswordLayout.setVisibility(View.GONE);
            confirmPasswordLayout.setError(null);
            cancelButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideAllViews(@NonNull View view) {
        view.setVisibility(View.INVISIBLE);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                hideAllViews(child);
            }
        }
    }
}