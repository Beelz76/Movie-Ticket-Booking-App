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
import com.example.myproject.databinding.FragmentMoviesBinding;
import com.example.myproject.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MoviesFragment extends Fragment {
    private FragmentMoviesBinding binding;
    private ArrayList<Movie> movies;
    private MovieAdapter movieAdapter;

    public MoviesFragment() {
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
        binding = FragmentMoviesBinding.inflate(inflater, container, false);
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

        binding.recyclerViewMovie.setLayoutManager(new GridLayoutManager(getContext(), 2));
        movies = new ArrayList<>();
        loadMovies();
    }

    @SuppressLint("DefaultLocale")
    private void loadMovies() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            PutData putData = new PutData(
                    "http://192.168.1.75/scripts/Movie/get_movies.php",
                    "GET", new String[0], new String[0]
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

                                Movie movie = new Movie();
                                movie.setMovieId(jsonObject.getInt("MovieId"));
                                movie.setTitle(jsonObject.getString("Title"));
                                movie.setReleaseYear( jsonObject.getInt("ReleaseYear"));

                                int totalMinutes = jsonObject.getInt("Duration");
                                int hours = totalMinutes / 60;
                                int minutes = totalMinutes % 60;
                                movie.setDuration(String.format("%dч %dмин", hours, minutes));

                                movie.setDescription(jsonObject.getString("Description"));
                                movie.setImage(jsonObject.getString("Image"));
                                movie.setDirectors(jsonObject.getString("Directors"));
                                movie.setGenres(jsonObject.getString("Genres"));
                                movie.setCountries(jsonObject.getString("Countries"));

                                movies.add(movie);
                            }
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    movieAdapter = new MovieAdapter(getContext(), movies);
                    binding.recyclerViewMovie.setAdapter(movieAdapter);
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