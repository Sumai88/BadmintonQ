package com.badmintonq.sumai.badmintonq.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sumai on 5/24/2016.
 */

public class Player {

    @SerializedName("PlayerID")
    @Expose
    private Integer playerID;
    @SerializedName("PlayerName")
    @Expose
    private String playerName;
    @SerializedName("PlayerEmail")
    @Expose
    private String playerEmail;
    @SerializedName("Phone")
    @Expose
    private Long phone;
    @SerializedName("Preference")
    @Expose
    private String preference;
    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("LoginType")
    @Expose
    private String loginType;
    @SerializedName("DeviceID")
    @Expose
    private String deviceID;
    @SerializedName("SkillsetID")
    @Expose
    private Integer skillsetID;
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
     * The playerEmail
     */
    public String getPlayerEmail() {
        return playerEmail;
    }

    /**
     *
     * @param playerEmail
     * The PlayerEmail
     */
    public void setPlayerEmail(String playerEmail) {
        this.playerEmail = playerEmail;
    }

    /**
     *
     * @return
     * The phone
     */
    public Long getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     * The Phone
     */
    public void setPhone(Long phone) {
        this.phone = phone;
    }

    /**
     *
     * @return
     * The preference
     */
    public String getPreference() {
        return preference;
    }

    /**
     *
     * @param preference
     * The Preference
     */
    public void setPreference(String preference) {
        this.preference = preference;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The Username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     * The Password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     * The loginType
     */
    public String getLoginType() {
        return loginType;
    }

    /**
     *
     * @param loginType
     * The LoginType
     */
    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    /**
     *
     * @return
     * The deviceID
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     *
     * @param deviceID
     * The DeviceID
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
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

    @Override
    public String toString() {
        return playerName;
    }
}

