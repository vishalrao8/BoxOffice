package com.example.visha.boxoffice.utils;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.visha.boxoffice.R;
import com.example.visha.boxoffice.database.MovieDatabase;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.Objects;

import static com.example.visha.boxoffice.activity.SplashScreen.movies;
import static com.example.visha.boxoffice.utils.UserPreferences.POPULAR;
import static com.example.visha.boxoffice.utils.UserPreferences.TOP_RATED;
import static com.example.visha.boxoffice.utils.UserPreferences.getSavedState;

public class View {

    private static Snackbar snackbar;

    public static void createSnack (ViewGroup parentLayout, String message, int length) {

        snackbar = Snackbar.make(parentLayout, message, length);
        android.view.View snackView = snackbar.getView();

        // Aligning text of SnackBar to the centre
        TextView snackBarText = snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackBarText.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);

        snackbar.show();

    }

    public static void hideSnack () {

        if (snackbar != null)
            snackbar.dismiss();

    }

    public static void hideText(RecyclerView recyclerView, TextView emptyNotifier) {
        recyclerView.setVisibility(android.view.View.VISIBLE);
        emptyNotifier.setVisibility(android.view.View.GONE);
    }

    public static void showText(RecyclerView recyclerView, TextView emptyNotifier) {
        recyclerView.setVisibility(android.view.View.GONE);
        emptyNotifier.setVisibility(android.view.View.VISIBLE);
    }

    public static void setUpTabLayoutTitle(Context context, TabLayout tabLayout){

        // Updating Tab layout's text based on user's preferences, showing currently sorted order
        if (UserPreferences.getSavedState(context.getApplicationContext()) == TOP_RATED)
            Objects.requireNonNull(tabLayout.getTabAt(0)).setText(context.getString(R.string.sorted_by_rating));
        else if (UserPreferences.getSavedState(context.getApplicationContext()) == POPULAR)
            Objects.requireNonNull(tabLayout.getTabAt(0)).setText(context.getString(R.string.sorted_by_popularity));
        else
            Objects.requireNonNull(tabLayout.getTabAt(0)).setText(context.getString(R.string.sorted_by_favourite));

    }

    public static void setUpCollapsingToolbarTitle (final Context context, AppBarLayout appBarLayout, final Toolbar toolbar) {

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                    Log.e("SCROLL RANGE", String.valueOf(scrollRange));
                }
                if (verticalOffset + scrollRange <= scrollRange/2) {

                    toolbar.setTitle(getTitle(context));
                    isShow = true;

                } else if(isShow) {

                    toolbar.setTitle(" ");
                    isShow = false;

                }
            }
        });
    }

    private static String getTitle (Context context) {

        switch (getSavedState(context.getApplicationContext())) {

            case 0 :
                return context.getString(R.string.sorted_by_rating);

            case 1 :
                return context.getString(R.string.sorted_by_popularity);

            case 2 :
                return context.getString(R.string.sorted_by_favourite);

            default :
                return null;

        }
    }

    public static void setUpLikeButton(final MovieDatabase mDb, SparkButton likeButton, final int position) {
        likeButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (buttonState) {

                    AppExecutor.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().addMovie(movies.get(position));
                        }
                    });

                } else {

                    AppExecutor.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().removeMovie(movies.get(position));
                        }
                    });
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
    }
}
