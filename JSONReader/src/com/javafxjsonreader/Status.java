/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafxjsonreader;

/**
 * POJO of the Status
 * @author jens.papenhagen
 */
public class Status {

    private Integer id;

    private String stamp;

    private String internalCarparkId;

    private Integer freeSlots;

    private Integer maxSlots;

    private Integer reservedSlots;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public String getInternalCarparkId() {
        return internalCarparkId;
    }

    public void setInternalCarparkId(String internalCarparkId) {
        this.internalCarparkId = internalCarparkId;
    }

    public Integer getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(Integer freeSlots) {
        this.freeSlots = freeSlots;
    }

    public Integer getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(Integer maxSlots) {
        this.maxSlots = maxSlots;
    }

    public Integer getReservedSlots() {
        return maxSlots - freeSlots;
    }

    public void setReservedSlots(Integer reservedSlots) {
        this.reservedSlots = reservedSlots;
    }

}
