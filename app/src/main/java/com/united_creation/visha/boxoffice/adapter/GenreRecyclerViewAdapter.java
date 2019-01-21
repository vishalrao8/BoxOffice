package com.united_creation.visha.boxoffice.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.united_creation.visha.boxoffice.R;
import com.united_creation.visha.boxoffice.model.Genres;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenreRecyclerViewAdapter extends RecyclerView.Adapter<GenreRecyclerViewAdapter.GenreViewHolder> {

    private final List<Genres> genres;

    public GenreRecyclerViewAdapter (List<Genres> genres) {

        this.genres = genres;

    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutResource = R.layout.item_genre;

        View view = LayoutInflater.from(context).inflate(layoutResource, parent, false);
        return new GenreViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {

        holder.genreText.setText(genres.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public class GenreViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.genre_tv)
        TextView genreText;

        private GenreViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
