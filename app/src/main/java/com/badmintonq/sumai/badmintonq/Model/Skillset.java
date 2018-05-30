package com.badmintonq.sumai.badmintonq.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sumai on 5/24/2016.
 */
public class Skillset {
    @SerializedName("SkillsetID")
    @Expose
    private Integer skillsetID;
    @SerializedName("SkillsetName")
    @Expose
    private String skillsetName;
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
     * The skillsetName
     */
    public String getSkillsetName() {
        return skillsetName;
    }

    /**
     *
     * @param skillsetName
     * The SkillsetName
     */
    public void setSkillsetName(String skillsetName) {
        this.skillsetName = skillsetName;
    }

}
