package com.example.abarno.popularmoviesapp_1;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class MovieDetailsAcitvity extends AppCompatActivity {

    private ImageView movieImage;
    private TextView movieTitle;
    private TextView movieVote;
    private TextView movieDate;
    private TextView movieOverview;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_acitvity);

        movieImage = findViewById(R.id.movie_image_details);
        movieTitle = findViewById(R.id.movie_title_details);
        movieVote = findViewById(R.id.movie_vote);
        movieDate = findViewById(R.id.movie_date);
        movieOverview = findViewById(R.id.movie_overview);

        MovieDetails details = (MovieDetails) Objects.requireNonNull(getIntent().getExtras()).getSerializable("MOVIE_DETAILS");

        if (details != null){
            Glide.with(this).load("https://image.tmdb.org/t/p/w500/"+details.getMoviePoster()).into(movieImage);
            movieTitle.setText("TITLE : "+details.getMovieTitle());
            movieVote.setText("RATING : "+Double.toString(details.getVoteAverage()));
            movieDate.setText("RELEASE : "+details.getReleaseDate());
            movieOverview.setText("OVERVIEW : "+details.getMovieOverview());

        }
    }
}
