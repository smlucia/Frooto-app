package com.example.frootsapp.Model;


import java.io.Serializable;

public class Order implements Serializable {
    private String userId;
    private String orderId;
    private int totAmount;
    private String name;
    private int price;
    private int qty;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getTotAmount() {
        return totAmount;
    }

    public void setTotAmount(int totAmount) {
        this.totAmount = totAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Order() {

    }

    public Order(String name, int totAmount, int price, int qty, String userId, String orderId) {
        this.totAmount = totAmount;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.userId = userId;
        this.orderId = orderId;
    }
}
