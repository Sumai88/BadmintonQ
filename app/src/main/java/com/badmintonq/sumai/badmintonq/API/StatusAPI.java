package com.badmintonq.sumai.badmintonq.API;

import com.badmintonq.sumai.badmintonq.Model.QStatus;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by sumai on 5/24/2016.
 */
public interface StatusAPI {
    @GET("/api/QStatus")
    Call<List<QStatus>> getQStatus();

    @GET("/api/QStatus/{id}")
    Call<QStatus> getQStatus(@Path("id") int id);
}
