package com.example.abarno.popularmoviesapp_1;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {


    private GridView gridView;
    private MovieAdapter movieAdapter;
    private ArrayList<MovieDetails> movieDetailsArrayLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.movie_view);
        new FetchMovies().execute("https://api.themoviedb.org/3/movie/popular?api_key=d9ac31413f412f924a99457ae2b87cf7&language=en-US&page=1");
    }

    private class FetchMovies extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {

            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String response = bufferedReader.readLine();
                bufferedReader.close();

                return response;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i<jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    MovieDetails movieDetails = new MovieDetails();
                    movieDetails.setMovieTitle(object.getString("original_title"));
                    movieDetails.setMovieOverview(object.getString("overview"));
                    movieDetails.setVoteAverage(object.getDouble("vote_average"));
                    movieDetails.setReleaseDate(object.getString("release_date"));
                    movieDetails.setMoviePoster(object.getString("poster_path"));
                    movieDetailsArrayLists.add(movieDetails);
                }
               movieAdapter = new MovieAdapter(MainActivity.this,movieDetailsArrayLists);
                gridView.setAdapter(movieAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
