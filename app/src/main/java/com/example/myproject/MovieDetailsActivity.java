package com.example.myproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.myproject.databinding.ActivityMovieDetailsBinding;
import com.example.myproject.ui.MoviesFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class MovieDetailsActivity extends AppCompatActivity {
    private ActivityMovieDetailsBinding binding;
    MaterialToolbar toolBar;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolBar = findViewById(R.id.topAppBar);
        toolBar.setTitle("Подробности фильма");
        setSupportActionBar(toolBar);
        toolBar.setNavigationOnClickListener(v -> onBackPressed());
        toolBar.inflateMenu(R.menu.close_menu);
        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_close) {
                    Intent intent = new Intent(getApplicationContext(), MoviesFragment.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        setupMovieDetails();

        binding.buttonChooseScreening.setOnClickListener(v -> {

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
        String title = intent.getStringExtra("title");
        int releaseYear = intent.getIntExtra("releaseYear", 0);
        String duration = intent.getStringExtra("duration");
        String description = intent.getStringExtra("description");
        String image = intent.getStringExtra("image");
        String directors = intent.getStringExtra("directors");
        String genres = intent.getStringExtra("genres");
        String countries = intent.getStringExtra("countries");

        binding.textMovieTitle.setText(title);
        binding.textMovieReleaseYear.setText("Год выпуска: " + String.valueOf(releaseYear));
        binding.textMovieDuration.setText("Время: " + duration);
        binding.textMovieDescription.setText(description);
        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_24)
                .into(binding.imageMovie);
        binding.textMovieDirectors.setText("Режиссер: " + directors);
        binding.textMovieGenres.setText("Жанры: " + genres);
        binding.textMovieCountries.setText("Страны: " + countries);
    }

}
