package com.united_creation.visha.boxoffice.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.united_creation.visha.boxoffice.R;
import com.united_creation.visha.boxoffice.api.ApiClient;
import com.united_creation.visha.boxoffice.api.ApiInterface;
import com.united_creation.visha.boxoffice.model.Movie;
import com.united_creation.visha.boxoffice.model.MoviesResponse;
import com.united_creation.visha.boxoffice.utils.UserPreferences;
import com.united_creation.visha.boxoffice.viewModel.FavouriteMoviesViewModel;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.united_creation.visha.boxoffice.BuildConfig.API_KEY;
import static com.united_creation.visha.boxoffice.utils.UserPreferences.POPULAR;
import static com.united_creation.visha.boxoffice.utils.UserPreferences.TOP_RATED;

public class SplashActivity extends AppCompatActivity {

    public static final String language = "en-US";

    public static List<Movie> movies;

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

    private void intentToHome() {

        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();

    }

    private void intentToError() {

        Intent intent = new Intent(SplashActivity.this, ErrorActivity.class);
        intent.putExtra(getString(R.string.message_tag_to_error_screen), SplashActivity.class);
        startActivity(intent);
        finish();

    }

    private void loadFavouriteMovies () {

        FavouriteMoviesViewModel favouriteMoviesViewModel = ViewModelProviders.of(this).get(FavouriteMoviesViewModel.class);
        final LiveData<List<Movie>> listLiveData = favouriteMoviesViewModel.getFavMovies();

        listLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> favMovies) {
                listLiveData.removeObserver(this);
                movies = favMovies;
                intentToHome();
            }
        });
    }

    private void makeNetworkRequest(Call<MoviesResponse> call) {

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {

                movies = Objects.requireNonNull(response.body()).getResults();
                intentToHome();

            }
            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                intentToError();
            }
        });
    }
}
