package com.badmintonq.sumai.badmintonq.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sumai on 5/24/2016.
 */
public class QStatus {
    @SerializedName("QStatusID")
    @Expose
    private Integer qStatusID;
    @SerializedName("StatusName")
    @Expose
    private String statusName;


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
     * The statusName
     */
    public String getStatusName() {
        return statusName;
    }

    /**
     *
     * @param statusName
     * The StatusName
     */
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

}
