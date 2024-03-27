package com.datn.client.models;

import java.util.List;

public class OrdersDetail {
    private List<ProductOrder> waitingList;
    private List<ProductOrder> prepareList;
    private List<ProductOrder> inTransitList;
    private List<ProductOrder> paidList;
    private List<ProductOrder> cancelList;

    public List<ProductOrder> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(List<ProductOrder> waitingList) {
        this.waitingList = waitingList;
    }

    public List<ProductOrder> getPrepareList() {
        return prepareList;
    }

    public void setPrepareList(List<ProductOrder> prepareList) {
        this.prepareList = prepareList;
    }

    public List<ProductOrder> getInTransitList() {
        return inTransitList;
    }

    public void setInTransitList(List<ProductOrder> inTransitList) {
        this.inTransitList = inTransitList;
    }

    public List<ProductOrder> getPaidList() {
        return paidList;
    }

    public void setPaidList(List<ProductOrder> paidList) {
        this.paidList = paidList;
    }

    public List<ProductOrder> getCancelList() {
        return cancelList;
    }

    public void setCancelList(List<ProductOrder> cancelList) {
        this.cancelList = cancelList;
    }
}
