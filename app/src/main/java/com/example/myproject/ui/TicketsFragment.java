package com.example.myproject.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myproject.LogInActivity;
import com.example.myproject.PutData;
import com.example.myproject.R;
import com.example.myproject.adapters.MovieAdapter;
import com.example.myproject.adapters.TicketAdapter;
import com.example.myproject.models.Movie;
import com.example.myproject.models.Ticket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TicketsFragment extends Fragment {
    private ArrayList<Ticket> tickets;
    private RecyclerView recyclerView;
    private TicketAdapter ticketAdapter;

    public TicketsFragment() {
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
        return inflater.inflate(R.layout.fragment_tickets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");

        if (userId.isEmpty()) {
            navigateToLogin();
        }

        recyclerView = view.findViewById(R.id.recyclerViewTicket);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tickets = new ArrayList<>();
        loadTickets(userId);
    }

    private void loadTickets(String userId) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"UserId"};
            String[] data = {userId};
            PutData putData = new PutData(
                    "http://192.168.1.75/scripts/Ticket/get_user_tickets.php",
                    "POST", field, data
            );

            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                Ticket ticket = new Ticket();
                                ticket.setTicketId(jsonObject.getInt("TicketId"));
                                ticket.setMovieTitle(jsonObject.getString("MovieTitle"));
                                ticket.setMovieImage(jsonObject.getString("MovieImage"));

                                String date = jsonObject.getString("Date");
                                String[] dateParts = date.split("-");
                                String newDate = dateParts[2] + "." + dateParts[1] + "." + dateParts[0];
                                ticket.setDate(newDate);

                                ticket.setStartTime(jsonObject.getString("StartTime").substring(0, 5));
                                ticket.setEndTime(jsonObject.getString("EndTime").substring(0, 5));
                                ticket.setPrice(jsonObject.getInt("Price"));
                                ticket.setHallName(jsonObject.getString("HallName"));
                                ticket.setRow(jsonObject.getInt("SeatRow"));
                                ticket.setNumber(jsonObject.getInt("SeatNumber"));

                                tickets.add(ticket);
                            }
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    ticketAdapter = new TicketAdapter(getContext(), tickets);
                    recyclerView.setAdapter(ticketAdapter);
                }
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}