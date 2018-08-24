package com.example.visha.boxoffice.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.visha.boxoffice.database.MovieDatabase;

public class MovieViewModeFactory extends ViewModelProvider.NewInstanceFactory {

    private final MovieDatabase movieDatabase;
    private final int id;

    public MovieViewModeFactory (MovieDatabase movieDatabase, int id) {

        this.movieDatabase = movieDatabase;
        this.id = id;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieViewModel(movieDatabase, id);
    }
}


