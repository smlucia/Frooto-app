package com.example.frootsapp.Model;

import java.util.ArrayList;

public class OrderReview {
    private ArrayList<Order> orderList;
    private String userId;
    private String totalAmount;
    private boolean ready;

    public OrderReview(ArrayList<Order> orders, String userId, String totalAmount, boolean ready) {
        this.orderList = orders;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.ready = ready;
    }

    public OrderReview() {

    }

    public ArrayList<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<Order> orders) {
        this.orderList = orders;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
