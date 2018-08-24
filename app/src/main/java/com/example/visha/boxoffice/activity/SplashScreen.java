package com.example.visha.boxoffice.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.visha.boxoffice.R;
import com.example.visha.boxoffice.api.ApiClient;
import com.example.visha.boxoffice.api.ApiInterface;
import com.example.visha.boxoffice.model.Movie;
import com.example.visha.boxoffice.model.MoviesResponse;
import com.example.visha.boxoffice.utils.UserPreferences;
import com.example.visha.boxoffice.viewModel.FavouriteMoviesViewModel;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.visha.boxoffice.BuildConfig.API_KEY;
import static com.example.visha.boxoffice.utils.UserPreferences.POPULAR;
import static com.example.visha.boxoffice.utils.UserPreferences.TOP_RATED;

public class SplashScreen extends AppCompatActivity {

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

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call;

        // Fetching data via API on the basis of user's preferences
        switch (UserPreferences.getSavedState(getApplicationContext())){

            case TOP_RATED :
                call = apiInterface.getTopRatedMovies(API_KEY, language);
                makeNetworkRequest(call);
                break;

            case POPULAR :
                call = apiInterface.getPopularMovies(API_KEY, language);
                makeNetworkRequest(call);
                break;

            default:
                loadFavouriteMovies();
                break;
        }
    }

    private void loadFavouriteMovies () {

        FavouriteMoviesViewModel favouriteMoviesViewModel = ViewModelProviders.of(this).get(FavouriteMoviesViewModel.class);
        final LiveData<List<Movie>> listLiveData = favouriteMoviesViewModel.getFavMovies();

        listLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> favMovies) {
                listLiveData.removeObserver(this);
                movies = favMovies;
                moveToMovieList();
            }
        });
    }

    private void makeNetworkRequest(Call<MoviesResponse> call) {

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
