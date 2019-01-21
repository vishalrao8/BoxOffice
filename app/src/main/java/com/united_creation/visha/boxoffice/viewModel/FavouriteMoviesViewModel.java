package com.united_creation.visha.boxoffice.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.united_creation.visha.boxoffice.database.MovieDatabase;
import com.united_creation.visha.boxoffice.model.Movie;

import java.util.List;

public class FavouriteMoviesViewModel extends AndroidViewModel {

    private final LiveData<List<Movie>> favMovies;

    public FavouriteMoviesViewModel(@NonNull Application application) {
        super(application);
        this.favMovies = MovieDatabase.getInstance(application.getApplicationContext()).movieDao().loadAllFavourites();
    }

    public LiveData<List<Movie>> getFavMovies() {
        return favMovies;
    }
}
