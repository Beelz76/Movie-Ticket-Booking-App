package com.example.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.databinding.ActivityScreeningsBinding;
import com.example.myproject.models.Screening;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class ScreeningsActivity extends AppCompatActivity {
    private ActivityScreeningsBinding binding;
    private MaterialToolbar toolBar;
    private ArrayList<Screening> screenings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScreeningsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolBar = findViewById(R.id.topAppBar);
        toolBar.setTitle("Сеансы");
        setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(v -> finish());
        toolBar.inflateMenu(R.menu.close_menu);
        toolBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_close) {
                navigateToMainActivity();
                return true;
            }
            return false;
        });

        loadScreenings();

        binding.buttonChooseSeat.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SeatsActivity.class);
            intent.putExtra("screeningId", (Integer) binding.buttonChooseSeat.getTag());
            //intent.putExtra("hallName", );
            startActivity(intent);
            //finish();
        });
    }

    private void loadScreenings() {
        String movieId = String.valueOf(getIntent().getIntExtra("movieId", 0));
        loadScreenings(movieId, s -> {
            screenings = s;
            HashSet<String> uniqueDates = new HashSet<>();
            for (Screening screening : screenings) {
                if (!uniqueDates.contains(screening.getDate())) {
                    uniqueDates.add(screening.getDate());
                    addDateChip(screening.getDate());
                }
            }
            if (!uniqueDates.isEmpty()) {
                ((CompoundButton) binding.chipGroupDate.getChildAt(0)).setChecked(true);
            }
        });
    }

    private void addDateChip(String date) {
        Chip dateChip = (Chip) getLayoutInflater().inflate(R.layout.chip_item, binding.chipGroupDate, false);
        dateChip.setText(date);
        dateChip.setOnCheckedChangeListener((buttonView, isChecked) ->  {
            binding.chipGroupTime.removeAllViews();
            for (Screening screening : screenings) {
                if (screening.getDate().equals(buttonView.getText().toString())) {
                    addTimeChip(screening);
                }
            }
            if (binding.chipGroupTime.getChildCount() > 0) {
                ((CompoundButton) binding.chipGroupTime.getChildAt(0)).setChecked(true);
            }
        });
        binding.chipGroupDate.addView(dateChip);
    }

    private void addTimeChip(Screening screening) {
        Chip timeChip = (Chip) getLayoutInflater().inflate(R.layout.chip_item, binding.chipGroupTime, false);
        timeChip.setText(screening.getStartTime() + " - " + screening.getEndTime());
        timeChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //binding.textHallName.setText(screening.getHallName());
            binding.buttonChooseSeat.setTag(screening.getScreeningId());
        });
        binding.chipGroupTime.addView(timeChip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close_menu, menu);
        return true;
    }

    public interface OnLoadScreeningsListener {
        void onLoadScreenings(ArrayList<Screening> screenings);
    }

    private void loadScreenings(String movieId, OnLoadScreeningsListener listener) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"MovieId"};
            String[] data = {movieId};
            PutData putData = new PutData(
                    "http://192.168.1.75/scripts/Screening/get_movie_screenings.php",
                    "POST", field, data
            );

            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    JSONArray jsonArray = null;
                    ArrayList<Screening> screenings = new ArrayList<>();
                    try {
                        jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                Screening screening = new Screening();
                                screening.setScreeningId(jsonObject.getInt("ScreeningId"));
                                screening.setStartTime(jsonObject.getString("StartTime").substring(0, 5));
                                screening.setEndTime(jsonObject.getString("EndTime").substring(0, 5));

                                String date = jsonObject.getString("Date");
                                String[] dateParts = date.split("-");
                                String newDate = dateParts[2] + "." + dateParts[1] + "." + dateParts[0];
                                screening.setDate(newDate);

                                screening.setHallName(jsonObject.getString("HallName"));
                                screening.setPrice(jsonObject.getInt("Price"));

                                screenings.add(screening);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    listener.onLoadScreenings(screenings);
                }
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}