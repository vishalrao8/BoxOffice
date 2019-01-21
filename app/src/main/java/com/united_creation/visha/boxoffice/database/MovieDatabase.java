package com.united_creation.visha.boxoffice.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.united_creation.visha.boxoffice.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase sInstance;
    private static final Object LOCK = new Object();
    private static final String databaseName = "favouritemovies";

    public static MovieDatabase getInstance (Context context) {

        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, databaseName)
                        .build();
            }
        }
        return sInstance;

    }

    public abstract MovieDao movieDao();

}
