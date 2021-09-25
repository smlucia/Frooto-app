package com.example.frootsapp.Model;

public class ShopOrder {
    private String userName;
    private String orderId;
    private String totalAmount;
    private String userEmail;
    private boolean ready;

    public ShopOrder(String userName, String orderId, String totalAmount, boolean ready) {
        this.userName = userName;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.ready = ready;
    }

    public ShopOrder() {

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
