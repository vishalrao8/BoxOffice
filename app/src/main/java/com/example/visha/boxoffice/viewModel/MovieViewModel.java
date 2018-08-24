package com.example.visha.boxoffice.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.visha.boxoffice.database.MovieDatabase;
import com.example.visha.boxoffice.model.Movie;

public class MovieViewModel extends ViewModel {

    private final LiveData<Movie> movieLiveData;

    public MovieViewModel (MovieDatabase movieDatabase, int id) {

        movieLiveData = movieDatabase.movieDao().loadMovieByID(id);

    }

    public LiveData<Movie> getMovieLiveData() {
        return movieLiveData;
    }
}
