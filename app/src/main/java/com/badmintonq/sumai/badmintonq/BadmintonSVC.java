package com.badmintonq.sumai.badmintonq;

import com.badmintonq.sumai.badmintonq.API.ClubAPI;
import com.badmintonq.sumai.badmintonq.API.PlayerAPI;
import com.badmintonq.sumai.badmintonq.API.QueueAPI;
import com.badmintonq.sumai.badmintonq.API.SkillAPI;
import com.badmintonq.sumai.badmintonq.API.StatusAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sumai on 6/21/2016.
 */
public class BadmintonSVC {
    //private static final String API_URL = "http://ec2-54-163-138-67.compute-1.amazonaws.com/BadminQTest/";
    //private static final String API_URL = "http://ec2-54-163-138-67.compute-1.amazonaws.com/BadminQProd/";
    private static final String API_URL =  "http://BadmintonQPRod-1950243207.us-east-1.elb.amazonaws.com/BadminQProd/";
    private Retrofit retrofit;
    private ClubAPI clubAPI;
    private PlayerAPI playerAPI;
    private QueueAPI queueAPI;

    public BadmintonSVC()
    {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public ClubAPI clubService()
    {
        clubAPI = retrofit.create(ClubAPI.class);
        return clubAPI;
    }

    public PlayerAPI playerService()
    {
        playerAPI = retrofit.create(PlayerAPI.class);
        return playerAPI;
    }

    public QueueAPI queueService()
    {
        queueAPI = retrofit.create(QueueAPI.class);
        return queueAPI;
    }
}
