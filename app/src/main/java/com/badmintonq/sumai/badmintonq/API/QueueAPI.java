package com.badmintonq.sumai.badmintonq.API;

import com.badmintonq.sumai.badmintonq.Model.CloseQueue;
import com.badmintonq.sumai.badmintonq.Model.Queue;
import com.badmintonq.sumai.badmintonq.Model.QueueData;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sumai on 5/24/2016.
 */
public interface QueueAPI {
    @GET("/api/queue")
    Call<List<QueueData>> getQueue();

    @GET("/api/queue/{id}")
    Call<Queue> getQueue(@Path("id") int id);

    @GET("/BadminQProd/api/queueBySkills/{skillID}")
    Call<List<QueueData>> getQueueBySkills(@Path("skillID") int skillID, @Query("clubID") int clubID);

    @PUT("/api/queue/{id}")
    Call<Queue> putQueue(@Path("id") int id, @Body Queue queue);

    @POST("/BadminQProd/api/SkipPlayer/{queueID}")
    Call<Queue> skipPlayer(@Path("queueID") int queueID);

    @POST("/BadminQProd/api/SwapPlayer/{FrQID}/{ToQID}")
    Call<Queue> swapPlayer(@Path("FrQID") int FrQID, @Path("ToQID") int ToQID);
    // public void swapPlayer(@Query("FrQID") int FrQID, @Query("ToQID") int ToQID, Callback<Queue> response);
    // check which one works

    @POST("/BadminQProd/api/DeleteQueue/{queueID}")
    Call<Queue> deleteQueue(@Path("queueID") int queueID);

    @POST("/BadminQProd/api/GameClose")
    Call<Map<String,CloseQueue>> gameClose(@Body Map<String,CloseQueue> closeQ);

    @POST("/BadminQProd/api/queue")
    Call<Queue> postQueue(@Body Queue queue);

    @GET("/api/queueMe/{clubID}")
    Call<List<QueueData>> getQueueMe(@Path("clubID") int clubID);

    @GET("/BadminQProd/api/queuePlaying/{clubID}")
    Call<List<QueueData>> getQueuePlaying(@Path("clubID") int clubID);

    @GET("/BadminQProd/api/QueueNext/{clubID}")
    Call<List<QueueData>> getQueueNext(@Path("clubID") int clubID);


}
