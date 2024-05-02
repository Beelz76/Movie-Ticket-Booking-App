package com.example.myproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myproject.databinding.ActivityMovieDetailsBinding;
import com.google.android.material.appbar.MaterialToolbar;

public class MovieDetailsActivity extends AppCompatActivity {
    private ActivityMovieDetailsBinding binding;
    private MaterialToolbar toolBar;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolBar = findViewById(R.id.topAppBar);
        toolBar.setTitle("Подробности фильма");
        setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(v -> finish());
        toolBar.inflateMenu(R.menu.close_menu);
        toolBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_close) {
                finish();
                return true;
            }
            return false;
        });

        setupMovieDetails();

        binding.buttonChooseScreening.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ScreeningsActivity.class);
            intent.putExtra("movieId", getIntent().getIntExtra("movieId", 0));
            intent.putExtra("movieTitle", getIntent().getStringExtra("title"));
            startActivity(intent);
            //finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close_menu, menu);
        return true;
    }

    public void setupMovieDetails() {
        Intent intent = getIntent();
        int movieId = intent.getIntExtra("movieId", 0);
        binding.textMovieTitle.setText(intent.getStringExtra("title"));
        binding.textMovieReleaseYear.setText("Год выпуска: " + intent.getIntExtra("releaseYear", 0));
        binding.textMovieDuration.setText("Время: " + intent.getStringExtra("duration"));
        binding.textMovieDescription.setText(intent.getStringExtra("description"));
        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_24)
                .into(binding.imageMovie);
        binding.textMovieDirectors.setText("Режиссер: " + intent.getStringExtra("directors"));
        binding.textMovieGenres.setText("Жанры: " + intent.getStringExtra("genres"));
        binding.textMovieCountries.setText("Страны: " + intent.getStringExtra("countries"));
    }

}
