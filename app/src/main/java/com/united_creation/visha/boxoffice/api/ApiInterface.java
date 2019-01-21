package com.united_creation.visha.boxoffice.api;

import com.united_creation.visha.boxoffice.model.Movie;
import com.united_creation.visha.boxoffice.model.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/{id}")
    Call<Movie> getMovieDetail(@Path("id") int id, @Query("api_key") String apiKey, @Query("append_to_response") String response, @Query("language") String language);

}
