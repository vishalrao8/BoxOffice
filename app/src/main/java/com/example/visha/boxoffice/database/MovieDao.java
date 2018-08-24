package com.example.visha.boxoffice.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.visha.boxoffice.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> loadAllFavourites();

    @Query("SELECT * FROM movies WHERE id=:id")
    LiveData<Movie> loadMovieByID(int id);

    @Insert
    void addMovie(Movie movie);

// --Commented out by Inspection START (17 Aug 2018 12:31):
//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    void updateMovie(Movie movie);
// --Commented out by Inspection STOP (17 Aug 2018 12:31)

    @Delete
    void removeMovie(Movie movie);
}
