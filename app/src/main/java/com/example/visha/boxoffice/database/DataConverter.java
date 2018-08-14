package com.example.visha.boxoffice.database;

import android.arch.persistence.room.TypeConverter;

import com.example.visha.boxoffice.model.Genres;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class DataConverter {

    @TypeConverter
    public String fromGenresList(List<Genres> genres) {
        if (genres == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Genres>>() {
        }.getType();
        String genresJson = gson.toJson(genres, type);
        return genresJson;
    }

    @TypeConverter
    public List<Genres> toGenresList(String genresJson) {
        if (genresJson == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Genres>>() {
        }.getType();
        List<Genres> genres = gson.fromJson(genresJson, type);
        return genres;
    }
}
