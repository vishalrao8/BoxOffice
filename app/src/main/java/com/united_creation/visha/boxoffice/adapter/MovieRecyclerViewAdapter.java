package com.united_creation.visha.boxoffice.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.united_creation.visha.boxoffice.R;
import com.united_creation.visha.boxoffice.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.MovieTileViewHolder> {

    final private onClickListener mOnClickListener;
    private List<Movie> movies;
    private final Context context;

    private static final String POSTER_URL = "https://image.tmdb.org/t/p/w342";

    public interface onClickListener {
        void onViewClicked(int position);
    }

    public MovieRecyclerViewAdapter(Context context, List<Movie> movies, onClickListener listener) {

        this.context = context;
        mOnClickListener = listener;
        this.movies = movies;

    }

    public void setNewList (List<Movie> movies) {

        this.movies = movies;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MovieTileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForMovieItem = R.layout.item_tile;

        View view = inflater.inflate(layoutIdForMovieItem, parent, false);
        return new MovieTileViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieTileViewHolder holder, int position) {

        Picasso.get()
                .load(POSTER_URL + movies.get(position).getPosterPath())
                .placeholder(Objects.requireNonNull(context.getDrawable(R.color.colorWhite)))
                .into(holder.movieTile);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieTileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.movie_tile)
        ImageView movieTile;

        private MovieTileViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            mOnClickListener.onViewClicked(getAdapterPosition());

        }
    }
}
