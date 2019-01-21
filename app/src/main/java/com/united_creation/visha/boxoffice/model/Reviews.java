package com.united_creation.visha.boxoffice.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Reviews {

    @SerializedName("results")
    private List <ReviewResults> reviewResults;

    public List<ReviewResults> getReviewResults() {
        return reviewResults;
    }

    @SuppressWarnings("unused")
    public class ReviewResults{

        @SerializedName("author")
        private String author;

        @SerializedName("content")
        private String content;

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }
    }
}
