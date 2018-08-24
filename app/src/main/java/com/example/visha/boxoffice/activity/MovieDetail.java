package com.example.visha.boxoffice.activity;

import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.visha.boxoffice.R;
import com.example.visha.boxoffice.adapter.GenreRecyclerViewAdapter;
import com.example.visha.boxoffice.adapter.ReviewRecyclerViewAdapter;
import com.example.visha.boxoffice.api.ApiClient;
import com.example.visha.boxoffice.api.ApiInterface;
import com.example.visha.boxoffice.database.MovieDatabase;
import com.example.visha.boxoffice.model.Movie;
import com.example.visha.boxoffice.utils.UserPreferences;
import com.example.visha.boxoffice.viewModel.MovieViewModeFactory;
import com.example.visha.boxoffice.viewModel.MovieViewModel;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.visha.boxoffice.BuildConfig.API_KEY;
import static com.example.visha.boxoffice.activity.MovieList.CONNECTION_FLAG;
import static com.example.visha.boxoffice.activity.MovieList.POSITION;
import static com.example.visha.boxoffice.activity.MovieList.appendToRequest;
import static com.example.visha.boxoffice.activity.MovieList.lostConnection;
import static com.example.visha.boxoffice.activity.ReviewDetail.MOVIE_POSITION;
import static com.example.visha.boxoffice.activity.ReviewDetail.REVIEW_POSITION;
import static com.example.visha.boxoffice.activity.SplashScreen.language;
import static com.example.visha.boxoffice.activity.SplashScreen.movies;
import static com.example.visha.boxoffice.utils.AppAnimation.animateGenreView;
import static com.example.visha.boxoffice.utils.AppAnimation.animateRatingBar;
import static com.example.visha.boxoffice.utils.AppAnimation.hideButton;
import static com.example.visha.boxoffice.utils.AppAnimation.showButton;
import static com.example.visha.boxoffice.utils.DateUtils.getFormattedDate;
import static com.example.visha.boxoffice.utils.NetworkState.isConnected;
import static com.example.visha.boxoffice.utils.View.setUpCollapsingToolbarTitle;
import static com.example.visha.boxoffice.utils.View.setUpLikeButton;

@SuppressWarnings("WeakerAccess")
public class MovieDetail extends AppCompatActivity implements ReviewRecyclerViewAdapter.onClickListener{

    private int position;
    private int flag;

    public static final int ONLINE = 1;
    public static final int OFFLINE = 2;

    private static final String YOUTUBE_APP_URL = "vnd.youtube:";
    private static final String YOUTUBE_WEB_URL = "http://www.youtube.com/watch?v=";
    private static final String BACKDROP_URL = "https://image.tmdb.org/t/p/w780";

    private static final String NULL = "null";

    private static final int DEFAULT_POSITION = -1;

    private MovieDatabase mDb;

    @BindView(R.id.heart_button)
    SparkButton likeButton;

    @BindView(R.id.certificate_tv)
    TextView certificate;

    @BindView(R.id.review_header)
    TextView reviewHeader;

    @BindView(R.id.genre_rv)
    RecyclerView genreRecyclerView;

    @BindView(R.id.parent_layout)
    CoordinatorLayout parentLayout;

    @BindView(R.id.review_rv)
    RecyclerView reviewsRecyclerView;

    @BindView(R.id.movie_title_toolbar)
    Toolbar toolbar;

    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    @BindView(R.id.release_date_tv)
    TextView releaseDate;

    @BindView(R.id.description_tv)
    TextView description;

    @BindView(R.id.movie_title_tv)
    TextView movieTitle;

    @BindView(R.id.backdrop)
    ImageView backdrop;

    @BindView(R.id.percentage_tv)
    TextView ratingAverageTv;

    @BindView(R.id.progressBar)
    ProgressBar averageVoteBar;

    @BindView(R.id.likes_count_tv)
    TextView ratingCount;

    @BindView(R.id.trailer_button)
    Button trailerButton;

    @OnClick(R.id.trailer_button)
    public void TrailerButton () {
        redirectToYoutube();
    }

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private NetworkRequest networkRequest;

    private void redirectToYoutube() {

        if (fetchOfficialVideoID() != null && !fetchOfficialVideoID().equals(NULL)) {

            Intent toYouTube = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_URL + fetchOfficialVideoID()));
            Intent toWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_WEB_URL + fetchOfficialVideoID()));

            try {
                startActivity(toYouTube);
            } catch (ActivityNotFoundException e) {
                startActivity(toWeb);
            }

        }
    }

    private String fetchOfficialVideoID () {

        String ID = null;
        int count = 0;

        if (movies.get(position).getVideoCollection().getVideoResultsList() != null) {

            int collectionSize = movies.get(position).getVideoCollection().getVideoResultsList().size();

            if (collectionSize == 0) {
                return NULL;
            }

            for (int i = 0; i < collectionSize; i++) {

                if (movies.get(position).getVideoCollection().getVideoResultsList().get(i).getName().contains("Official")) {

                    ID = movies.get(position).getVideoCollection().getVideoResultsList().get(i).getKey();
                    break;

                } else {
                    count++;

                    if (count == collectionSize)
                        ID = movies.get(position).getVideoCollection().getVideoResultsList().get(0).getKey();

                }
            }
        }
        return ID;
    }

    private void setUpRecyclerView () {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        genreRecyclerView.setLayoutManager(linearLayoutManager);

        GenreRecyclerViewAdapter genreAdapter = new GenreRecyclerViewAdapter(movies.get(position).getGenres());
        genreRecyclerView.setAdapter(genreAdapter);

        if (flag == ONLINE) {

            reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            ReviewRecyclerViewAdapter reviewAdapter = new ReviewRecyclerViewAdapter(movies.get(position).getReviews().getReviewResults(), this);
            reviewsRecyclerView.setAdapter(reviewAdapter);
        }
    }

    private void setUpNetworkCallback(){

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkRequest = new NetworkRequest.Builder().build();
        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {

                if (lostConnection) {

                    lostConnection = false;

                    if (flag == OFFLINE)
                        makeNetworkRequest();
                    else
                        showButton(trailerButton);

                }
            }

            @Override
            public void onLost(Network network) {

                lostConnection = true;
                hideButton(trailerButton);

            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){

            onBackPressed();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void makeNetworkRequest () {

        final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Movie> call = apiInterface.getMovieDetail(movies.get(position).getId(), API_KEY, appendToRequest, language);

        //noinspection NullableProblems
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, Response<Movie> response) {

                flag = ONLINE;

                movies.get(position).setVideoCollection(Objects.requireNonNull(response.body()).getVideoCollection());
                movies.get(position).setReviews(Objects.requireNonNull(response.body()).getReviews());

                if (movies.get(position).getVideoCollection().getVideoResultsList().isEmpty())
                    runOnUiThread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void run() {

                            trailerButton.setForeground(getDrawable(R.drawable.trailer_button_dark));
                            showButton(trailerButton);
                        }
                    });
                else
                    showButton(trailerButton);

                setUpRecyclerView();
                showReviews();
                Picasso.get()
                        .load(BACKDROP_URL + movies.get(position).getBackdropPath())
                        .placeholder(Objects.requireNonNull(getDrawable(R.color.colorBlack)))
                        .into(backdrop);

            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {

                if (isConnected(connectivityManager)) {
                    makeNetworkRequest();
                } else
                    flag = OFFLINE;

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        mDb = MovieDatabase.getInstance(getApplicationContext());

        setUpNetworkCallback();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setUpCollapsingToolbarTitle(this, appBarLayout, toolbar);

        position = getIntent().getIntExtra(POSITION, DEFAULT_POSITION);
        flag = getIntent().getIntExtra(CONNECTION_FLAG, ONLINE);

        // Setting up required attributes on creating activity
        if (position != DEFAULT_POSITION) {

            populateUI();
            setUpRecyclerView();

        }
    }

    private void showReviews() {

        if (!movies.get(position).getReviews().getReviewResults().isEmpty()) {

            reviewHeader.setVisibility(View.VISIBLE);
            reviewsRecyclerView.setVisibility(View.VISIBLE);

        }
    }

    private void checkIfFavourite () {

        int movieId = movies.get(position).getId();
        final MovieViewModel movieViewModel = new MovieViewModeFactory(mDb, movieId).create(MovieViewModel.class);
        movieViewModel.getMovieLiveData().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                movieViewModel.getMovieLiveData().removeObserver(this);

                if (movie != null)
                    likeButton.setChecked(true);
            }
        });
    }

    private void moveToReviewDetail (int reviewPosition) {

        Intent intent = new Intent(this, ReviewDetail.class);
        intent.putExtra(REVIEW_POSITION, reviewPosition);
        intent.putExtra(MOVIE_POSITION, position);
        startActivity(intent);

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void populateUI() {

        if (flag == ONLINE) {

            if (fetchOfficialVideoID() != null && !fetchOfficialVideoID().equals(NULL))
                showButton(trailerButton);
            else {
                trailerButton.setForeground(getDrawable(R.drawable.trailer_button_dark));
                showButton(trailerButton);
            }

            Picasso.get()
                    .load(BACKDROP_URL + movies.get(position).getBackdropPath())
                    .placeholder(Objects.requireNonNull(getDrawable(R.color.colorBlack)))
                    .into(backdrop);

            showReviews();

        }

        int ratingAverage;
        if (UserPreferences.getSavedState(getApplicationContext()) == UserPreferences.FAVOURITE)
            ratingAverage = movies.get(position).getVoteAverage().intValue()/10;
        else
            ratingAverage = movies.get(position).getVoteAverage().intValue();

        String ratingAverageString = ratingAverage + getString(R.string.percentage_sign);

        movieTitle.setText(movies.get(position).getTitle());
        movieTitle.setSelected(true);
        releaseDate.setText(getFormattedDate(position));
        description.setText(movies.get(position).getOverview());
        ratingCount.setText(String.valueOf(movies.get(position).getVoteCount()));
        ratingAverageTv.setText(ratingAverageString);

        if (movies.get(position).isAdult())
            certificate.setText("A");

        checkIfFavourite();
        animateGenreView(genreRecyclerView);
        setUpLikeButton(mDb, likeButton, position);
        animateRatingBar(averageVoteBar, ratingAverage);
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
    }

    @Override
    public void onViewClicked(int reviewPosition) {
        moveToReviewDetail(reviewPosition);
    }
}
