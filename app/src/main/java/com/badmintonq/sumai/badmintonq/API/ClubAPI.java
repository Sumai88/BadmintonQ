package com.badmintonq.sumai.badmintonq.API;

import com.badmintonq.sumai.badmintonq.Model.Club;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sumai on 5/24/2016.
 */
public interface ClubAPI {
    @GET("api/club")
    Call<List<Club>> getClubs();

    @GET("/BadminQProd/api/club/Info")
    Call<Club> getClubInfo(@Query("strEmail") String strEmail);

    @GET("/api/Club/{id}")
    Call<Club> getClub(@Path("id") int id);

    @DELETE("/api/Club/{id}")
    Call<Club> deleteClub(@Path("id") int id);

    @PUT("/BadminQProd/api/club/{id}")
    Call<Club> putClub(@Path("id") int id, @Body Club club);

    @POST("/BadminQProd/api/club")
    Call<Club> postClub(@Body Club club);
}
