package com.example.visha.boxoffice.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.content.Context;

import com.example.visha.boxoffice.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase sInstance;
    private static final Object LOCK = new Object();
    private static final String databaseName = "favouritemovies";

    public static MovieDatabase getInstance (Context context) {

        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(),
                    MovieDatabase.class, databaseName)
                    .build();
        }
        return sInstance;

    }

    public abstract MovieDao movieDao();

}
