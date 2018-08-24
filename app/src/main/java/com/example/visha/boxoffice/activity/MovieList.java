package com.example.visha.boxoffice.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visha.boxoffice.R;
import com.example.visha.boxoffice.adapter.MovieRecyclerViewAdapter;
import com.example.visha.boxoffice.api.ApiClient;
import com.example.visha.boxoffice.api.ApiInterface;
import com.example.visha.boxoffice.utils.UserPreferences;
import com.example.visha.boxoffice.view.GridRecyclerView;
import com.example.visha.boxoffice.model.Movie;
import com.example.visha.boxoffice.model.MoviesResponse;
import com.example.visha.boxoffice.viewModel.FavouriteMoviesViewModel;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static com.example.visha.boxoffice.BuildConfig.API_KEY;
import static com.example.visha.boxoffice.activity.MovieDetail.OFFLINE;
import static com.example.visha.boxoffice.activity.MovieDetail.ONLINE;
import static com.example.visha.boxoffice.activity.SplashScreen.language;
import static com.example.visha.boxoffice.activity.SplashScreen.movies;
import static com.example.visha.boxoffice.utils.NetworkState.isConnected;
import static com.example.visha.boxoffice.utils.UserPreferences.FAVOURITE;
import static com.example.visha.boxoffice.utils.UserPreferences.POPULAR;
import static com.example.visha.boxoffice.utils.UserPreferences.TOP_RATED;
import static com.example.visha.boxoffice.utils.UserPreferences.getSavedState;
import static com.example.visha.boxoffice.utils.View.createSnack;
import static com.example.visha.boxoffice.utils.View.hideSnack;
import static com.example.visha.boxoffice.utils.View.hideText;
import static com.example.visha.boxoffice.utils.View.setUpTabLayoutTitle;
import static com.example.visha.boxoffice.utils.View.showText;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class MovieList extends AppCompatActivity implements MovieRecyclerViewAdapter.onClickListener {

    public static final String POSITION = "position";
    public static final String CONNECTION_FLAG = "flag";
    private List<Movie> favMovies;

    @BindView(R.id.parent_layout)
    CoordinatorLayout parentLayout;

    @BindView(R.id.movie_list_rv)
    GridRecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.empty_tv)
    TextView emptyNotifier;

    // An variable holding status of current connection
    public static boolean lostConnection;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private NetworkRequest networkRequest;

    private final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
    private Call<MoviesResponse> call;

    private MovieRecyclerViewAdapter recyclerViewAdapter;

    public final static String appendToRequest = "videos,reviews";

    private void setUpNetworkCallback (){

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkRequest = new NetworkRequest.Builder().build();
        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {

                if (lostConnection){

                    hideSnack();
                    lostConnection = false;

                }

            }

            @Override
            public void onLost(Network network) {

                lostConnection = true;
                createSnack(parentLayout, getString(R.string.snack_bar_error_text), LENGTH_INDEFINITE);

            }
        };
    }

    private void setUpMovieTiles(){

        int spanCount;

        // Span count based on orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            spanCount = 3;
        else
            spanCount = 5;

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new MovieRecyclerViewAdapter(this, movies, this);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    private void populateUIWithData(List<Movie> newMovieList){

        // Updating Recycler view and Tab layout with new data
        if (newMovieList != null && !newMovieList.isEmpty()) {

            recyclerViewAdapter.setNewList(newMovieList);
            recyclerView.scheduleLayoutAnimation();
            hideText(recyclerView, emptyNotifier);

        } else {
            showText(recyclerView, emptyNotifier);
        }
        setUpTabLayoutTitle(this, tabLayout);

    }

    private void makeNetworkRequest() {
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {

                movies = Objects.requireNonNull(response.body()).getResults();
                populateUIWithData(movies);
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {

                if (isConnected(connectivityManager))
                    makeNetworkRequest();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        // Updating corresponding item to be checked based on user's preferences
        switch (UserPreferences.getSavedState(getApplicationContext())) {

            case TOP_RATED :
                menu.findItem(R.id.top_rated).setChecked(true);
                break;

            case POPULAR :
                menu.findItem(R.id.most_popular).setChecked(true);
                break;

            default:
                menu.findItem(R.id.favourite).setChecked(true);
                break;

        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /* Updating user's preferences based on "Sort by" order selected */
        switch (item.getItemId()) {

            case R.id.top_rated:

                if (UserPreferences.getSavedState(getApplicationContext()) != TOP_RATED && isConnected(connectivityManager)) {
                    call = apiInterface.getTopRatedMovies(API_KEY, language);
                    makeNetworkRequest();
                    toggleOrder(TOP_RATED, item);

                    return true;
                } else
                    return false;

            case R.id.most_popular:

                if (UserPreferences.getSavedState(getApplicationContext()) != POPULAR && isConnected(connectivityManager)) {
                    call = apiInterface.getPopularMovies(API_KEY, language);
                    makeNetworkRequest();
                    toggleOrder(POPULAR, item);

                    return true;
                } else
                    return false;

            case R.id.favourite:

                if (getSavedState(getApplicationContext()) != FAVOURITE) {
                    toggleOrder(FAVOURITE, item);
                    movies = favMovies;
                    populateUIWithData(favMovies);

                    return true;
                } else
                    return false;

            default:
                return false;
        }
    }

    private void toggleOrder (int order, MenuItem item) {

        UserPreferences.updateUserPreferences(getApplicationContext(), order);
        item.setChecked(true);

    }

    private void moveToMovieDetails(int position, int flag) {

        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra(POSITION, position);
        intent.putExtra(CONNECTION_FLAG, flag);
        startActivity(intent);

    }

    private void setUpFavMoviesViewModel () {

        FavouriteMoviesViewModel favouriteMoviesViewModel = ViewModelProviders.of(this).get(FavouriteMoviesViewModel.class);

        favouriteMoviesViewModel.getFavMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> newFavMovies) {
                favMovies = newFavMovies;
                if (getSavedState(getApplicationContext()) == FAVOURITE) {

                    movies = favMovies;
                    populateUIWithData(movies);

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        checkIfEmpty();

        // Setting up required attributes on creating activity
        setUpMovieTiles();
        setUpTabLayoutTitle(this, tabLayout);
        setUpNetworkCallback();
        setUpFavMoviesViewModel();
    }

    private void checkIfEmpty() {

        if (movies == null || movies.isEmpty()) {
            showText(recyclerView, emptyNotifier);
        } else {
            hideText(recyclerView, emptyNotifier);
        }
    }

    @Override
    public void onViewClicked(final int position) {

        if (isConnected(connectivityManager)) {

            // Fetching additional details related to the currently selected movie tile
            Call<Movie> call = apiInterface.getMovieDetail(movies.get(position).getId(), API_KEY, appendToRequest, language);

            //noinspection NullableProblems
            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(@NonNull Call<Movie> call, Response<Movie> response) {

                    movies.get(position).setGenres(Objects.requireNonNull(response.body()).getGenres());
                    movies.get(position).setVideoCollection(Objects.requireNonNull(response.body()).getVideoCollection());
                    movies.get(position).setReviews(Objects.requireNonNull(response.body()).getReviews());

                    moveToMovieDetails(position, ONLINE);

                }

                @Override
                public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {

                    Toast.makeText(MovieList.this, "Please try again later!", Toast.LENGTH_SHORT).show();

                }
            });

        } else if (UserPreferences.getSavedState(getApplicationContext()) == FAVOURITE) {

            moveToMovieDetails(position, OFFLINE);

        }
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
        if (!isConnected(connectivityManager)){

            lostConnection = true;
            createSnack(parentLayout, getString(R.string.snack_bar_error_text), LENGTH_INDEFINITE);

        } else {

            lostConnection = false;
            hideSnack();

        }
    }
}
