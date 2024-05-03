package com.example.myproject.ui;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.example.myproject.LogInActivity;
import com.example.myproject.PutData;
import com.example.myproject.R;
import com.example.myproject.databinding.FragmentProfileBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");

        if (userId.isEmpty()) {
            navigateToLogin();
        }

        setFieldsEnabled(false);
        setFieldsVisibility(false);
        getUserInfo(userId);

        binding.imageLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Вы уверены, что хотите выйти?")
                    .setPositiveButton("Да", (dialog, id) -> navigateToLogin())
                    .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        binding.buttonEdit.setOnClickListener(v -> {
            setFieldsEnabled(true);
            setFieldsVisibility(true);
        });

        binding.buttonCancel.setOnClickListener(v -> {
            binding.inputFullname.setText(currentFullName);
            binding.inputEmail.setText(currentEmail);
            binding.inputLogin.setText(currentLogin);
            setFieldsEnabled(false);
            setFieldsVisibility(false);
        });

        binding.buttonSave.setOnClickListener(v -> {
            String fullname, email, login, password, confirmPassword;
            fullname = binding.inputFullname.getText().toString();
            email = binding.inputEmail.getText().toString();
            login = binding.inputLogin.getText().toString();
            password = binding.inputPassword.getText().toString();
            confirmPassword = binding.inputConfirmPassword.getText().toString();

            validateInputs(fullname, login, password, confirmPassword, email);

            if (!login.isEmpty() && login.length() >= 4 && !password.isEmpty() && !fullname.isEmpty() && !confirmPassword.isEmpty()) {
                if (!email.isEmpty()) {
                    if (email.matches(("^[A-Za-z0-9+_.-]+@(.+)$"))) {
                        updateUserInfo(userId, fullname, email, login, password);
                        setFieldsEnabled(false);
                        setFieldsVisibility(false);
                    }
                } else {
                    updateUserInfo(userId, fullname, email, login, password);
                    setFieldsEnabled(false);
                    setFieldsVisibility(false);
                }
            }
        });
    }

    private void validateInputs(String fullname, String login, String password, String confirmPassword, String email) {
        if (fullname.isEmpty()) {
            binding.inputLayoutFullname.setError("Заполните поле");
        } else {
            binding.inputLayoutFullname.setError(null);
        }

        if (login.isEmpty()) {
            binding.inputLayoutLogin.setError("Заполните поле");
        } else if (login.length() < 4) {
            binding.inputLayoutLogin.setError("Логин должен содержать не менее 4 символов");
        } else {
            binding.inputLayoutLogin.setError(null);
        }

        if (password.isEmpty()) {
            binding.inputLayoutPassword.setError("Заполните поле");
        } else {
            binding.inputLayoutPassword.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            binding.inputLayoutConfirmPassword.setError("Пароль не совпадает");
        } else {
            binding.inputLayoutConfirmPassword.setError(null);
        }

        if (!email.isEmpty()) {
            if (!email.matches(("^[A-Za-z0-9+_.-]+@(.+)$"))) {
                binding.inputLayoutEmail.setError("Неверный формат электронной почты");
            } else {
                binding.inputLayoutEmail.setError(null);
            }
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
                            binding.inputFullname.setText(userInfo.getString("Fullname"));
                            binding.inputEmail.setText(userInfo.getString("Email"));
                            binding.inputLogin.setText(userInfo.getString("Login"));

                            currentFullName = binding.inputFullname.getText().toString();
                            currentEmail = binding.inputEmail.getText().toString();
                            currentLogin = binding.inputLogin.getText().toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Данные сохранены", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Неправильный логин или пароль", Toast.LENGTH_SHORT).show();
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
        binding.inputFullname.setEnabled(enabled);
        binding.inputEmail.setEnabled(enabled);
        binding.inputLogin.setEnabled(enabled);
        binding.inputPassword.setEnabled(enabled);
        binding.inputConfirmPassword.setEnabled(enabled);
    }

    private void setFieldsVisibility(boolean visibility) {
        if (visibility){
            binding.inputLayoutLogin.setError(null);
            binding.inputPassword.setText("");
            binding.inputConfirmPassword.setText("");
            binding.inputPassword.setVisibility(View.VISIBLE);
            binding.inputLayoutPassword.setVisibility(View.VISIBLE);
            binding.inputLayoutPassword.setError(null);
            binding.inputConfirmPassword.setVisibility(View.VISIBLE);
            binding.inputLayoutConfirmPassword.setVisibility(View.VISIBLE);
            binding.inputLayoutConfirmPassword.setError(null);
            binding.buttonCancel.setVisibility(View.VISIBLE);
            binding.buttonSave.setVisibility(View.VISIBLE);
            binding.buttonEdit.setVisibility(View.GONE);
        } else {
            binding.inputLayoutLogin.setError(null);
            binding.inputPassword.setText("");
            binding.inputConfirmPassword.setText("");
            binding.inputPassword.setVisibility(View.GONE);
            binding.inputLayoutPassword.setVisibility(View.GONE);
            binding.inputLayoutPassword.setError(null);
            binding.inputConfirmPassword.setVisibility(View.GONE);
            binding.inputLayoutConfirmPassword.setVisibility(View.GONE);
            binding.inputLayoutConfirmPassword.setError(null);
            binding.buttonCancel.setVisibility(View.GONE);
            binding.buttonSave.setVisibility(View.GONE);
            binding.buttonEdit.setVisibility(View.VISIBLE);
        }
    }
}