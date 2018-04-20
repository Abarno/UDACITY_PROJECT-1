package com.example.abarno.popularmoviesapp_1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private TextView networkMessage;
    private Context currentContext;
    private GridView gridView;
    private MovieAdapter movieAdapter;
    private ArrayList<MovieDetails> movieDetailsArrayLists = new ArrayList<>();
    private String mostpopular = "https://api.themoviedb.org/3/movie/popular?api_key='your API key'&language=en-US&page=1";
    private String toprated = "https://api.themoviedb.org/3/movie/top_rated?api_key='your API key'&language=en-US&page=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkMessage = findViewById(R.id.network_message);
        currentContext = getApplicationContext();

        if (connectionAvailability()) {

            gridView = findViewById(R.id.movie_view);
            gridView.setOnItemClickListener(this);

            new FetchMovies(currentContext).execute(mostpopular);
        }
        else {
            networkMessage.setVisibility(View.VISIBLE);
        }

    }

    public boolean connectionAvailability(){
        ConnectivityManager cm =
                (ConnectivityManager)currentContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(connectionAvailability()) {
            networkMessage.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, MovieDetailsAcitvity.class);
            intent.putExtra("MOVIE_DETAILS", (Serializable) adapterView.getItemAtPosition(position));
            startActivity(intent);
        }
        else {
            networkMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.mostpopular_button){
            if (connectionAvailability()) {
                networkMessage.setVisibility(View.INVISIBLE);
                movieDetailsArrayLists.clear();
                new FetchMovies(currentContext).execute(mostpopular);
                movieAdapter.notifyDataSetChanged();
                return true;
            }
            else {
                networkMessage.setVisibility(View.VISIBLE);
            }
        }
        else if(id == R.id.highrated_button){
            if (connectionAvailability()) {
                networkMessage.setVisibility(View.INVISIBLE);
                movieDetailsArrayLists.clear();
                new FetchMovies(currentContext).execute(toprated);
                movieAdapter.notifyDataSetChanged();
                return true;
            }
            else {
                networkMessage.setVisibility(View.VISIBLE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchMovies extends AsyncTask<String, Void, String>{
        public FetchMovies(Context context) {
            currentContext = context;
        }

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
