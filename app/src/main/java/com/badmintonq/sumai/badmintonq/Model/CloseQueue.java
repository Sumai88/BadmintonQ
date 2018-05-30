package com.badmintonq.sumai.badmintonq.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sumai on 5/24/2016.
 */
public class CloseQueue {
    @SerializedName("QueueID")
    @Expose
    private Integer queueID;
    @SerializedName("Score")
    @Expose
    private Integer score;
    @SerializedName("Won")
    @Expose
    private Boolean won;

    /**
     *
     * @return
     * The queueID
     */
    public Integer getQueueID() {
        return queueID;
    }

    /**
     *
     * @param queueID
     * The QueueID
     */
    public void setQueueID(Integer queueID) {
        this.queueID = queueID;
    }

    /**
     *
     * @return
     * The score
     */
    public Integer getScore() {
        return score;
    }

    /**
     *
     * @param score
     * The Score
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     *
     * @return
     * The won
     */
    public Boolean getWon() {
        return won;
    }

    /**
     *
     * @param won
     * The Won
     */
    public void setWon(Boolean won) {
        this.won = won;
    }


}
