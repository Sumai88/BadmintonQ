package com.badmintonq.sumai.badmintonq.API;

import com.badmintonq.sumai.badmintonq.Model.Player;

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
public interface PlayerAPI {
    @GET("api/player")
    Call<List<Player>> getPlayers();

    @GET("/api/Player/{id}")
    Call<Player> getPlayer(@Path("id") int id);

    @GET("/BadminQProd/api/Player/Info")
    Call<Player> getPlayerInfo(@Query("strEmail") String strEmail);

    @DELETE("/api/Player/{id}")
    Call<Player> deletePlayer(@Path("id") int id);

    @PUT("/BadminQProd/api/Player/{id}")
    Call<Player> putPlayer(@Path("id") int id, @Body Player player);

    @POST("/BadminQProd/api/LoginDirect")
    Call<Player> loginDirect(@Query("email") String playerEmail,@Query("pwd") String password);

    @POST("api/LoginFromFB")
    Call<Player> loginFB(@Query("email") String email, @Query("user") String userName , @Query("name") String playerName);

    @POST("/BadminQProd/api/Player")
    Call<Player> postPlayer(@Body Player player);
}
