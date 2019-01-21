package com.united_creation.visha.boxoffice.utils;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;

public class AppAnimation {

    public static void animateRatingBar(ProgressBar averageVoteBar, int progress) {


        ObjectAnimator progressBarAnimator = ObjectAnimator.ofInt(averageVoteBar, "progress", progress);
        progressBarAnimator.setDuration(1000);
        progressBarAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressBarAnimator.start();

    }

    public static void animateGenreView(final RecyclerView genreRecyclerView) {

        genreRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                genreRecyclerView.setVisibility(View.VISIBLE);
            }
        },200);

    }

    public static void showButton(Button trailerButton) {
        trailerButton.animate().translationYBy(-trailerButton.getLayoutParams().height-30).setStartDelay(200).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public static void hideButton(Button trailerButton) {
        trailerButton.animate().translationYBy(trailerButton.getLayoutParams().height+30).setStartDelay(200).setInterpolator(new AccelerateInterpolator());
    }

}
