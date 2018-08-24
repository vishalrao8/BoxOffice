package com.example.visha.boxoffice.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.visha.boxoffice.R;
import com.example.visha.boxoffice.model.Reviews;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ReviewViewHolder>{

    private final List<Reviews.ReviewResults> reviews;
    private final onClickListener mOnClickListener;

    public ReviewRecyclerViewAdapter (List<Reviews.ReviewResults> reviews, onClickListener onClickListener) {

        mOnClickListener = onClickListener;
        this.reviews = reviews;

    }

    public interface onClickListener {
        void onViewClicked(int reviewPosition);
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForMovieItem = R.layout.review_card_view;

        View view = inflater.inflate(layoutIdForMovieItem, parent, false);
        return new ReviewViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewViewHolder holder, int position) {

        String authorName = reviews.get(position).getAuthor();

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(position);

        TextDrawable drawable = TextDrawable.builder()
                                .buildRound(authorName.substring(0,1), color);

        holder.avatar.setImageDrawable(drawable);
        holder.reviewTv.setText(reviews.get(position).getContent());
        holder.authorTv.setText(authorName);

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.review_tv)
        TextView reviewTv;

        @BindView(R.id.review_author_tv)
        TextView authorTv;

        @BindView(R.id.avatar_iv)
        ImageView avatar;

        private ReviewViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onViewClicked(getAdapterPosition());
        }
    }
}
