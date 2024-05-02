package com.example.myproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;

public class SeatsActivity extends AppCompatActivity {
    private ActivitySeatsBinding binding;
    private MaterialToolbar toolBar;
    private ArrayList<Seat> seats;
    private TreeSet<Seat> selectedSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");

        toolBar = findViewById(R.id.topAppBar);
        toolBar.setTitle(getIntent().getStringExtra("hallName"));
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

        seats = new ArrayList<>();
        selectedSeats = new TreeSet<>();

        loadScreeningSeats(String.valueOf(getIntent().getIntExtra("screeningId", 0)), s -> {
            seats = s;
            loadSeats(seats);
        });

        binding.buttonBuyTicket.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Выберите место", Toast.LENGTH_SHORT).show();
            } else {
                String screeningInfo =
                        "Название: " +  getIntent().getStringExtra("movieTitle") + "\n" +
                        "Дата: " + getIntent().getStringExtra("date") + "\n" +
                        "Время: " + getIntent().getStringExtra("time") + "\n\n";

                int pricePerSeat = getIntent().getIntExtra("price", 0);
                int totalPrice = pricePerSeat * selectedSeats.size();

                StringBuilder selectedSeatsInfo = new StringBuilder();
                for (Seat  seat: selectedSeats) {
                    if (seat != null) {
                        selectedSeatsInfo.append("Ряд: ").append(seat.getRow()).append(", Место: ").append(seat.getNumber()).append("\n");
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Подтвердите покупку");
                builder.setMessage(screeningInfo + selectedSeatsInfo + "\nСтоимость: " + totalPrice)
                        .setPositiveButton("Купить билет", (dialog, id) -> {
                            for (Seat seat : selectedSeats) {
                                buyTicket(userId, String.valueOf(getIntent().getIntExtra("screeningId", 0)), String.valueOf(seat.getSeatId()));
                            }
                            loadScreeningSeats(String.valueOf(getIntent().getIntExtra("screeningId", 0)), s -> {
                                seats = s;
                                loadSeats(seats);
                            });
                            selectedSeats.clear();
                        })
                        .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close_menu, menu);
        return true;
    }

    public void loadSeats(ArrayList<Seat> seats) {
        binding.layout.removeAllViews();
        int rows = 0;
        int[] seatsInRow = null;

        if (seats.size() == 28) {
            rows = 5;
            seatsInRow = new int[]{4, 6, 6, 6, 6};
        } else if (seats.size() == 46) {
            rows = 6;
            seatsInRow = new int[]{6, 8, 8, 8, 8, 8};
        }

        for (int i = 0; i < rows; i++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);

            for (int j = 0; j < seatsInRow[i]; j++) {
                int index =  i * seatsInRow[i] + j;

                Seat seat = seats.get(i == 0 ? index : index - 2);
                Button seatButton = (Button) getLayoutInflater().inflate(R.layout.seat_item, binding.layout, false);
                if (seat.getIsTaken() == 1) {
                    seatButton.setEnabled(false);
                    seatButton.setBackgroundColor(Color.parseColor("#5C5D5F"));
                } else {
                    seatButton.setBackgroundColor(Color.parseColor("#C6CACB"));
                }
                seatButton.setTag(seat);
                seatButton.setOnClickListener(v -> {
                    Seat s = (Seat) v.getTag();
                    if (selectedSeats.contains(s)) {
                        v.setBackgroundColor(Color.parseColor("#C6CACB"));
                        selectedSeats.remove(s);
                    } else {
                        v.setBackgroundColor(Color.parseColor("#6D65A0"));
                        selectedSeats.add(s);
                    }                    });
                rowLayout.addView(seatButton);
            }
            binding.layout.addView(rowLayout);
        }
    }

    public interface OnLoadScreeningSeatsListener {
        void onLoadScreeningSeats(ArrayList<Seat> seats);
    }

    private void loadScreeningSeats(String screeningId, OnLoadScreeningSeatsListener listener) {
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
                    //listener.onBuyTicketComplete();
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
