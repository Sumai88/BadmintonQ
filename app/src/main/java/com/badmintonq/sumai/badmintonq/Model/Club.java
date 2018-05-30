package com.badmintonq.sumai.badmintonq.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Club {
    @SerializedName("ClubID")
    @Expose
    private Integer clubID;
    @SerializedName("ClubName")
    @Expose
    private String clubName;
    @SerializedName("NoOfCourts")
    @Expose
    private Integer noOfCourts;
    @SerializedName("Organizer")
    @Expose
    private String organizer;
    @SerializedName("ClubEmail")
    @Expose
    private String clubEmail;
    @SerializedName("StreetName")
    @Expose
    private String streetName;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("Zipcode")
    @Expose
    private Integer zipcode;
    @SerializedName("SkillPredefined")
    @Expose
    private Boolean SkillPredefined;

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
     * The clubName
     */
    public String getClubName() {
        return clubName;
    }

    /**
     *
     * @param clubName
     * The ClubName
     */
    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    /**
     *
     * @return
     * The noOfCourts
     */
    public Integer getNoOfCourts() {
        return noOfCourts;
    }

    /**
     *
     * @param noOfCourts
     * The NoOfCourts
     */
    public void setNoOfCourts(Integer noOfCourts) {
        this.noOfCourts = noOfCourts;
    }

    /**
     *
     * @return
     * The organizer
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     *
     * @param organizer
     * The Organizer
     */
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    /**
     *
     * @return
     * The clubEmail
     */
    public String getClubEmail() {
        return clubEmail;
    }

    /**
     *
     * @param clubEmail
     * The ClubEmail
     */
    public void setClubEmail(String clubEmail) {
        this.clubEmail = clubEmail;
    }

    /**
     *
     * @return
     * The streetName
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     *
     * @param streetName
     * The StreetName
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     *
     * @return
     * The city
     */
    public String getCity() {
        return city;
    }

    /**
     *
     * @param city
     * The City
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The State
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return
     * The zipcode
     */
    public Integer getZipcode() {
        return zipcode;
    }

    /**
     *
     * @param zipcode
     * The Zipcode
     */
    public void setZipcode(Integer zipcode) {
        this.zipcode = zipcode;
    }

    public Boolean getSkillPredefined() {
        return SkillPredefined;
    }

    public void setSkillPredefined(Boolean SkillPredefined) {
        this.SkillPredefined = SkillPredefined;
    }

    @Override
    public String toString() {
        return clubName;
    }



}
