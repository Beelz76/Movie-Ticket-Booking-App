package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myproject.databinding.ActivityScreeningsBinding;
import com.example.myproject.databinding.ActivitySeatsBinding;
import com.example.myproject.models.Seat;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class SeatsActivity extends AppCompatActivity {
    private ActivitySeatsBinding binding;
    private MaterialToolbar toolBar;
    private ArrayList<Seat> seats = new ArrayList<>();
    private HashSet<Integer> selectedSeats = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");

        toolBar = findViewById(R.id.topAppBar);
        toolBar.setTitle("Зал");
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

        loadScreeningSeats(String.valueOf(getIntent().getIntExtra("screeningId", 0)), s -> {
            seats = s;
            binding.layout.removeAllViews();
            for (Seat seat : seats) {
                Button seatButton = (Button) getLayoutInflater().inflate(R.layout.seat_item, binding.layout, false);
                seatButton.setTag(seat.getSeatId());
                seatButton.setText(seat.getRow() + " " + seat.getNumber());
                seatButton.setBackgroundColor(seat.getIsTaken() == 1 ? Color.GRAY : Color.WHITE);
                seatButton.setOnClickListener(v -> {
                    int seatId = (int) v.getTag();
                    if (selectedSeats.contains(seatId)) {
                        v.setBackgroundColor(Color.WHITE);
                        selectedSeats.remove(seatId);
                    } else {
                        v.setBackgroundColor(Color.YELLOW);
                        selectedSeats.add(seatId);
                    }
                });
                binding.layout.addView(seatButton);
            }
        });

        binding.buttonBuyTicket.setOnClickListener(v -> {
            for (Integer seatId : selectedSeats) {
                buyTicket(userId, String.valueOf(getIntent().getIntExtra("screeningId", 0)), String.valueOf(seatId));
            }
            selectedSeats.clear();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close_menu, menu);
        return true;
    }

    public interface OnLoadScreeningSeatsListener {
        void onLoadScreeningSeats(ArrayList<Seat> seats);
    }

    private void loadScreeningSeats(String screeningId, SeatsActivity.OnLoadScreeningSeatsListener listener) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"ScreeningId"};
            String[] data = {screeningId};
            PutData putData = new PutData(
                    "http://192.168.1.75/scripts/Seat/get_screening_seats.php",
                    "POST", field, data
            );

            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    JSONArray jsonArray = null;
                    ArrayList<Seat> seats = new ArrayList<>();
                    try {
                        jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                Seat seat = new Seat();
                                seat.setSeatId(jsonObject.getInt("SeatId"));
                                seat.setRow(jsonObject.getInt("Row"));
                                seat.setNumber(jsonObject.getInt("Number"));
                                seat.setTaken(jsonObject.getInt("IsTaken"));

                                seats.add(seat);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    listener.onLoadScreeningSeats(seats);
                }
            }
        });
    }

    private void buyTicket(String userId, String screeningId, String seatId) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"UserId", "ScreeningId", "SeatId"};
            String[] data = {userId, screeningId, seatId};
            PutData putData = new PutData(
                    "http://192.168.1.75/scripts/Ticket/create_ticket.php",
                    "POST", field, data
            );

            if (putData.startPut() && putData.onComplete()) {
                String result = putData.getResult();
                if (result.equals("Create Ticket Success")) {
                    Toast.makeText(getApplicationContext(), "Вы купили билет", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
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