package com.example.visha.boxoffice.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.example.visha.boxoffice.database.DataConverter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "movies")
public class Movie {

    @PrimaryKey
    @SerializedName("id")
    private Integer id;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("adult")
    private boolean adult;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("genres")
    @TypeConverters(DataConverter.class)
    private List<Genres> genres;

    @SerializedName("title")
    private String title;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("vote_count")
    private Integer voteCount;

    @Ignore
    @SerializedName("videos")
    private Videos videoCollection;

    @Ignore
    @SerializedName("reviews")
    private Reviews reviews;

    @SerializedName("vote_average")
    private Double voteAverage;

    public Movie (Integer id, String posterPath, boolean adult, String overview, String releaseDate, String title, String backdropPath,
                  Integer voteCount, Double voteAverage) {

        this.id = id;
        this.posterPath = posterPath;
        this. adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.title = title;
        this.backdropPath = backdropPath;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Genres> getGenres() {
        return genres;
    }

    public void setGenres(List<Genres> genres) {
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Videos getVideoCollection() {
        return videoCollection;
    }

    public void setVideoCollection(Videos videoCollection) {
        this.videoCollection = videoCollection;
    }

    public Reviews getReviews() {
        return reviews;
    }

    public void setReviews(Reviews reviews) {
        this.reviews = reviews;
    }

    public Double getVoteAverage() {
        return voteAverage*10;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }
}