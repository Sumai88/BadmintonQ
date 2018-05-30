package com.badmintonq.sumai.badmintonq.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sumai on 5/24/2016.
 */
public class Queue {
    @SerializedName("QueueID")
    @Expose
    private Integer queueID;
    @SerializedName("ClubID")
    @Expose
    private Integer clubID;
    @SerializedName("PlayerID")
    @Expose
    private Integer playerID;
    @SerializedName("SkillsetID")
    @Expose
    private Integer skillsetID;
    @SerializedName("Score")
    @Expose
    private Integer score;
    @SerializedName("PlayDateTime")
    @Expose
    private String playDateTime;
    @SerializedName("QStatusID")
    @Expose
    private Integer qStatusID;
    @SerializedName("Won")
    @Expose
    private Boolean won;
    @SerializedName("Club")
    @Expose
    private Club club;
    @SerializedName("Player")
    @Expose
    private Player player;
    @SerializedName("Skillset")
    @Expose
    private Skillset skillset;
    @SerializedName("QStatus")
    @Expose
    private QStatus qStatus;

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

    /**
     *
     * @return
     * The club
     */
    public Club getClub() {
        return club;
    }

    /**
     *
     * @param club
     * The Club
     */
    public void setClub(Club club) {
        this.club = club;
    }

    /**
     *
     * @return
     * The player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     *
     * @param player
     * The Player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     *
     * @return
     * The skillset
     */
    public Skillset getSkillset() {
        return skillset;
    }

    /**
     *
     * @param skillset
     * The Skillset
     */
    public void setSkillset(Skillset skillset) {
        this.skillset = skillset;
    }

    /**
     *
     * @return
     * The qStatus
     */
    public QStatus getQStatus() {
        return qStatus;
    }

    /**
     *
     * @param qStatus
     * The QStatus
     */
    public void setQStatus(QStatus qStatus) {
        this.qStatus = qStatus;
    }


}
