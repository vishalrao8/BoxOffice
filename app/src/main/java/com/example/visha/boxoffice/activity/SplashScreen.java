package com.example.visha.boxoffice.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.visha.boxoffice.R;
import com.example.visha.boxoffice.api.ApiClient;
import com.example.visha.boxoffice.api.ApiInterface;
import com.example.visha.boxoffice.model.Movie;
import com.example.visha.boxoffice.model.MoviesResponse;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.visha.boxoffice.BuildConfig.API_KEY;

public class SplashScreen extends AppCompatActivity {

    public static boolean sortedByRating;
    public static final String language = "en-US";

    public static List<Movie> movies;

    private void moveToMovieList() {

        Intent intent = new Intent(SplashScreen.this, MovieList.class);
        startActivity(intent);
        finish();

    }

    private void moveToErrorScreen() {

        Intent intent = new Intent(SplashScreen.this, ErrorScreen.class);
        intent.putExtra(getString(R.string.message_tag_to_error_screen),SplashScreen.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Accessing user's saved preferences
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        sortedByRating = sharedPreferences.getBoolean(getString(R.string.preferences_saved_tag), getResources().getBoolean(R.bool.sorted_by_rating_default));

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call;

        // Fetching data via API on the basis of user's preferences
        if (sortedByRating)
            call = apiInterface.getTopRatedMovies(API_KEY, language);
        else
            call = apiInterface.getPopularMovies(API_KEY, language);

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {

                movies = Objects.requireNonNull(response.body()).getResults();
                moveToMovieList();

            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {

                moveToErrorScreen();

            }
        });
    }
}
