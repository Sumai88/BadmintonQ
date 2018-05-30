package com.badmintonq.sumai.badmintonq.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sumai on 5/24/2016.
 */
public class QueueData implements Comparable<QueueData> {
    @SerializedName("ClubID")
    @Expose
    private Integer clubID;
    @SerializedName("PlayDateTime")
    @Expose
    private String playDateTime;
    @SerializedName("PlayerID")
    @Expose
    private Integer playerID;
    @SerializedName("PlayerName")
    @Expose
    private String playerName;
    @SerializedName("QStatusID")
    @Expose
    private Integer qStatusID;
    @SerializedName("QueueID")
    @Expose
    private Integer queueID;
    @SerializedName("Score")
    @Expose
    private Integer score;
    @SerializedName("SkillsetID")
    @Expose
    private Integer skillsetID;
    @SerializedName("QueueOrder")
    @Expose
    private Integer queueOrder;
    @SerializedName("QueueTempID")
    @Expose
    private Integer queueTempID;

    /**
     *
     * @return
     * The clubID
     */
    public Integer getClubID() {
        return clubID;
    }

    /**
     *
     * @param clubID
     * The ClubID
     */
    public void setClubID(Integer clubID) {
        this.clubID = clubID;
    }

    /**
     *
     * @return
     * The playDateTime
     */
    public String getPlayDateTime() {
        return playDateTime;
    }

    /**
     *
     * @param playDateTime
     * The PlayDateTime
     */
    public void setPlayDateTime(String playDateTime) {
        this.playDateTime = playDateTime;
    }

    /**
     *
     * @return
     * The playerID
     */
    public Integer getPlayerID() {
        return playerID;
    }

    /**
     *
     * @param playerID
     * The PlayerID
     */
    public void setPlayerID(Integer playerID) {
        this.playerID = playerID;
    }

    /**
     *
     * @return
     * The playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     *
     * @param playerName
     * The PlayerName
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     *
     * @return
     * The qStatusID
     */
    public Integer getQStatusID() {
        return qStatusID;
    }

    /**
     *
     * @param qStatusID
     * The QStatusID
     */
    public void setQStatusID(Integer qStatusID) {
        this.qStatusID = qStatusID;
    }

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
     * The skillsetID
     */
    public Integer getSkillsetID() {
        return skillsetID;
    }

    /**
     *
     * @param skillsetID
     * The SkillsetID
     */
    public void setSkillsetID(Integer skillsetID) {
        this.skillsetID = skillsetID;
    }

    /**
     *
     * @return
     * The queueOrder
     */
    public Integer getQueueOrder() {
        return queueOrder;
    }

    /**
     *
     * @param queueOrder
     * The QueueOrder
     */
    public void setQueueOrder(Integer queueOrder) {
        this.queueOrder = queueOrder;
    }

    /**
     *
     * @return
     * The queueTempID
     */
    public Integer getQueueTempID() {
        return queueTempID;
    }

    /**
     *
     * @param queueTempID
     * The QueueTempID
     */
    public void setQueueTempID(Integer queueTempID) {
        this.queueTempID = queueTempID;
    }

    @Override
    public int compareTo(QueueData queueData) {

        if (queueTempID > queueData.getQueueTempID()) {
            return 1;
        }
        else if (queueTempID <  queueData.getQueueTempID()) {
            return -1;
        }
        else {
            return 0;
        }

    }

}
