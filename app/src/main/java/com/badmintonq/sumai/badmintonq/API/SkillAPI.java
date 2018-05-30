package com.badmintonq.sumai.badmintonq.API;
import com.badmintonq.sumai.badmintonq.Model.Skillset;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by sumai on 5/24/2016.
 */
public interface SkillAPI {
    @GET("/api/skillset")
    Call<List<Skillset>> getSkillsets();

    @GET("/api/skillset/{id}")
    Call<Skillset> getSkillset(@Path("id") int id);
}
