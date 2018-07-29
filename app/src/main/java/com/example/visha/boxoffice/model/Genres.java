package com.example.visha.boxoffice.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Genres {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}
