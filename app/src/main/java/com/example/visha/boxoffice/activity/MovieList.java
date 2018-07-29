package com.example.visha.boxoffice.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.visha.boxoffice.R;
import com.example.visha.boxoffice.adapter.MovieRecyclerViewAdapter;
import com.example.visha.boxoffice.api.ApiClient;
import com.example.visha.boxoffice.api.ApiInterface;
import com.example.visha.boxoffice.view.GridRecyclerView;
import com.example.visha.boxoffice.model.Movie;
import com.example.visha.boxoffice.model.MoviesResponse;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static com.example.visha.boxoffice.BuildConfig.API_KEY;
import static com.example.visha.boxoffice.activity.SplashScreen.language;
import static com.example.visha.boxoffice.activity.SplashScreen.movies;
import static com.example.visha.boxoffice.activity.SplashScreen.sortedByRating;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class MovieList extends AppCompatActivity implements MovieRecyclerViewAdapter.onClickListener {

    @BindView(R.id.parent_layout)
    CoordinatorLayout parentLayout;

    @BindView(R.id.movie_list_rv)
    GridRecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    private SharedPreferences sharedPreferences;

    // An variable holding status of current connection
    public static boolean lostConnection = false;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private NetworkRequest networkRequest;

    private final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

    private MovieRecyclerViewAdapter recyclerViewAdapter;

    private final static String appendToRequest = "videos,reviews";

    private void setUpNetworkCallback (){

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkRequest = new NetworkRequest.Builder().build();
        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {

                if (lostConnection){

                    createSnack(getString(R.string.snack_bar_welcome_text), LENGTH_SHORT, ContextCompat.getColor(getApplicationContext(), R.color.snackBarSuccess));
                    lostConnection = false;

                }

            }

            @Override
            public void onLost(Network network) {

                lostConnection = true;
                createSnack(getString(R.string.snack_bar_error_text), LENGTH_INDEFINITE);

            }
        };
    }

    private void setUpRecyclerView (){

        int spanCount;

        // Span count based on orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            spanCount = 3;
        else
            spanCount = 5;

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new MovieRecyclerViewAdapter(movies, this);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    private void setUpTabLayoutTitle(){

        // Updating Tab layout's text based on user's preferences, showing currently sorted order
        if (sortedByRating)
            Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.sorted_by_rating));
        else
            Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.sorted_by_popularity));

    }

    private void createSnack (String message, int length, int backgroundRes) {

        Snackbar snackbar = Snackbar.make(parentLayout, message, length);
        View snackView = snackbar.getView();

        // SnackBar with custom background
        snackView.setBackgroundColor(backgroundRes);

        // Aligning text of SnackBar to the centre
        TextView snackBarText = snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackBarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        snackbar.show();

    }

    private void createSnack(String message, int length) {

        Snackbar snackbar = Snackbar.make(parentLayout, message, length);
        View snackView = snackbar.getView();

        // Aligning text of SnackBar to the centre
        TextView snackBarText = snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackBarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        snackbar.show();

    }

    private void populateUIWithData(){

        // Updating Recycler view and Tab layout with new data
        recyclerViewAdapter.setNewList(movies);
        recyclerView.scheduleLayoutAnimation();
        setUpTabLayoutTitle();

    }

    private void toggleSortBy(){

        Call<MoviesResponse> call;

        if (sortedByRating)
            call = apiInterface.getTopRatedMovies(API_KEY, language);
        else
            call = apiInterface.getPopularMovies(API_KEY, language);

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {

                movies = Objects.requireNonNull(response.body()).getResults();
                populateUIWithData();
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {

                Log.e("Failed", t.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        // Updating corresponding item to be checked based on user's preferences
        if (sortedByRating)
            menu.findItem(R.id.top_rated).setChecked(true);
        else
            menu.findItem(R.id.most_popular).setChecked(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (isConnected()) {

            /* Updating user's preferences based on "Sort by" order selected */
            switch (item.getItemId()) {
                case R.id.top_rated:

                    if (!sortedByRating) {

                        sortedByRating = true;
                        sharedPreferences.edit().putBoolean(getString(R.string.preferences_saved_tag), sortedByRating).apply();
                        item.setChecked(true);
                        // Fetching corresponding data via API after "Sort by" order toggled
                        toggleSortBy();
                        return true;

                    } else
                        return false;

                case R.id.most_popular:

                    if (sortedByRating) {

                        sortedByRating = false;
                        sharedPreferences.edit().putBoolean(getString(R.string.preferences_saved_tag), sortedByRating).apply();
                        item.setChecked(true);
                        // Fetching corresponding data via API after "Sort by" order toggled
                        toggleSortBy();
                        return true;

                    } else
                        return false;

                default:
                    return false;
            }

        } else {

            createSnack(getString(R.string.snack_bar_error_text_forced), LENGTH_INDEFINITE);
            return false;

        }

    }

    private void moveToMovieDetails(int position) {

        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra("position", position);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Accessing user's saved preferences to be edited
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        sortedByRating = sharedPreferences.getBoolean(getString(R.string.preferences_saved_tag), getResources().getBoolean(R.bool.sorted_by_rating_default));

        // Setting up required attributes on creating activity
        setUpRecyclerView();
        setUpTabLayoutTitle();
        setUpNetworkCallback();
    }

    @Override
    public void onViewClicked(final int position) {

        if (isConnected()) {

            // Fetching additional details related to the currently selected movie tile
            Call<Movie> call = apiInterface.getMovieDetail(movies.get(position).getId(), API_KEY, appendToRequest, language);

            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {

                    movies.get(position).setGenres(Objects.requireNonNull(response.body()).getGenres());
                    movies.get(position).setVideoCollection(Objects.requireNonNull(response.body()).getVideoCollection());
                    movies.get(position).setReviews(Objects.requireNonNull(response.body()).getReviews());

                    moveToMovieDetails(position);

                }

                @Override
                public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {

                    Log.e("Failed", t.toString());

                }
            });

        } else
            createSnack(getString(R.string.snack_bar_error_text_forced), LENGTH_INDEFINITE);
    }

    /* A method returning current network state */
    private boolean isConnected(){

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

    @Override
    protected void onPause() {
        super.onPause();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        if (!isConnected()){

            lostConnection = true;
            createSnack(getString(R.string.snack_bar_error_text), LENGTH_INDEFINITE);

        }
    }
}
