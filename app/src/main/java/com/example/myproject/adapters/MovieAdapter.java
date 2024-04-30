package com.example.myproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.MovieDetailsActivity;
import com.example.myproject.R;
import com.example.myproject.databinding.MovieItemBinding;
import com.example.myproject.models.Movie;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    Context context;
    private ArrayList<Movie> movies;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(MovieItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.binding.textMovieTitle.setText(movie.getTitle());

        Glide.with(holder.binding.imageMovie.getContext())
                .load(movie.getImage())
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_24)
                .into(holder.binding.imageMovie);

        holder.binding.cardMovie.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra("movieId", movie.getMovieId());
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("releaseYear", movie.getReleaseYear());
            intent.putExtra("duration", movie.getDuration());
            intent.putExtra("description", movie.getDescription());
            intent.putExtra("image", movie.getImage());
            intent.putExtra("directors", movie.getDirectors());
            intent.putExtra("genres", movie.getGenres());
            intent.putExtra("countries", movie.getCountries());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        MovieItemBinding binding;

        public MovieViewHolder(MovieItemBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

}
