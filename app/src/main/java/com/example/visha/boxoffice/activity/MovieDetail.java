package com.example.visha.boxoffice.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static android.view.View.GONE;
import static com.example.visha.boxoffice.activity.MovieList.lostConnection;
import static com.example.visha.boxoffice.activity.SplashScreen.movies;

@SuppressWarnings("WeakerAccess")
public class MovieDetail extends AppCompatActivity {

    private int position;
    private String videoID;

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

    @BindView(R.id.movie_detail_layout)
    ConstraintLayout detailLayout;

    @BindView(R.id.movie_title_toolbar)
    Toolbar toolbar;

    @BindView(R.id.collapse_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

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

    private void showTrailerButton() {

        trailerButton.animate().translationYBy(-trailerButton.getLayoutParams().height-30).setDuration(650);

    }

    private void redirectToYoutube() {

        if (videoID != null) {
            Intent toYouTube = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + fetchOfficialVideoID()));
            Intent toWeb = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + fetchOfficialVideoID()));

            try {
                startActivity(toYouTube);
            } catch (ActivityNotFoundException e) {
                startActivity(toWeb);
            }
        } else
            createSnack("No trailer available", LENGTH_SHORT);
    }

    private String fetchOfficialVideoID() {

        String ID = null;
        int count = 0;
        int collectionSize = movies.get(position).getVideoCollection().getVideoResultsList().size();

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
        return ID;
    }

    @SuppressWarnings("SameParameterValue")
    private void createSnack(String message, int length, int backgroundRes) {

        Snackbar snackbar = Snackbar.make(parentLayout, message, length);
        View snackView = snackbar.getView();

        snackView.setBackgroundColor(backgroundRes);

        TextView snackBarText = snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackBarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        snackbar.show();

    }

    private void createSnack(String message, int length) {

        Snackbar snackbar = Snackbar.make(parentLayout, message, length);
        View snackView = snackbar.getView();

        TextView snackBarText = snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackBarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        snackbar.show();

    }

    private void setUpRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        genreRecyclerView.setLayoutManager(linearLayoutManager);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ReviewRecyclerViewAdapter reviewAdapter = new ReviewRecyclerViewAdapter(movies.get(position).getReviews().getReviewResults());
        reviewsRecyclerView.setAdapter(reviewAdapter);

        GenreRecyclerViewAdapter genreAdapter = new GenreRecyclerViewAdapter(movies.get(position).getGenres());
        genreRecyclerView.setAdapter(genreAdapter);

    }

    private void setUpNetworkCallback(){

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){

            onBackPressed();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        showTrailerButton();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        position = getIntent().getIntExtra("position", -1);

        // Setting up required attributes on creating activity
        if (position != -1) {

            populateUI();
            setUpRecyclerView();
            setUpNetworkCallback();

        }
    }

    private void animateRatingBar(int progress) {


        ObjectAnimator progressBarAnimator = ObjectAnimator.ofInt(averageVoteBar, "progress", progress);
        progressBarAnimator.setDuration(1500);
        //progressBarAnimator.setInterpolator(new DecelerateInterpolator());
        progressBarAnimator.start();

    }

    private void animateGenreView() {


        genreRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                genreRecyclerView.setVisibility(View.VISIBLE);
            }
        },200);

    }

    private void hideOnNoReviews() {

        if (movies.get(position).getReviews().getReviewResults().size() == 0) {

            reviewHeader.setVisibility(GONE);
            reviewsRecyclerView.setVisibility(GONE);

        }
    }

    private String getFormattedDate() {

        String rawReleaseString = movies.get(position).getReleaseDate();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
        Date rawReleaseDate = new Date();

        try {
            rawReleaseDate = dateParser.parse(rawReleaseString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
        return dateFormatter.format(rawReleaseDate);

    }

    private void populateUI() {

        videoID = fetchOfficialVideoID();
        int ratingAverage = movies.get(position).getVoteAverage().intValue();
        String ratingAverageString = ratingAverage + getString(R.string.percentage_sign);

        movieTitle.setText(movies.get(position).getTitle());
        movieTitle.setSelected(true);
        releaseDate.setText(getFormattedDate());
        description.setText(movies.get(position).getOverview());
        ratingCount.setText(String.valueOf(movies.get(position).getVoteCount()));
        ratingAverageTv.setText(ratingAverageString);

        if (movies.get(position).isAdult())
            certificate.setText("A");

        Picasso.get()
                .load("https://image.tmdb.org/t/p/w780" + movies.get(position).getBackdropPath())
                .placeholder(Objects.requireNonNull(getDrawable(R.color.colorBlack)))
                .into(backdrop);

        hideOnNoReviews();
        animateGenreView();
        animateRatingBar(ratingAverage);
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
