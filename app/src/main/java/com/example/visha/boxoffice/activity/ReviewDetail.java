package com.example.visha.boxoffice.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.visha.boxoffice.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.visha.boxoffice.activity.SplashScreen.movies;

public class ReviewDetail extends AppCompatActivity {

    private int reviewPosition;
    private int moviePosition;

    public static final String REVIEW_POSITION = "review_position";
    public static final String MOVIE_POSITION = "movie_position";

    private static final int DEFAULT_POSITION = -1;

    @BindView(R.id.review_tv)
    TextView reviewTv;

    @BindView(R.id.review_author_tv)
    TextView authorTv;

    @BindView(R.id.avatar_iv)
    ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ButterKnife.bind(this);

        reviewPosition = getIntent().getIntExtra(REVIEW_POSITION, DEFAULT_POSITION);
        moviePosition = getIntent().getIntExtra(MOVIE_POSITION, DEFAULT_POSITION);

        if (reviewPosition != -1 && moviePosition != -1)
            populateUI();

    }

    private void populateUI() {

        String authorName = movies.get(moviePosition).getReviews().getReviewResults().get(reviewPosition).getAuthor();

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(reviewPosition);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(authorName.substring(0,1), color);

        avatar.setImageDrawable(drawable);
        reviewTv.setText(movies.get(moviePosition).getReviews().getReviewResults().get(reviewPosition).getContent());
        authorTv.setText(authorName);

    }
}
