package com.united_creation.visha.boxoffice.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Videos {

    @SerializedName("results")
    private List<VideoResults> videoResultsList;

    public List<VideoResults> getVideoResultsList() {
        return videoResultsList;
    }

    @SuppressWarnings("unused")
    public class VideoResults {

        @SerializedName("key")
        private String key;

        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }
    }
}
