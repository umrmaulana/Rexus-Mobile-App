package com.example.uts_a22202302996.model;

public class OrderHistory {
    private int id;
    private String orderNumber;
    private String orderDate;
    private String orderStatus;
    private String paymentStatus;
    private double totalAmount;

    public OrderHistory(int id, String orderNumber, String orderDate, String orderStatus, String paymentStatus, double totalAmount) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
    }

    // Getters
    public int getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getOrderDate() { return orderDate; }
    public String getOrderStatus() { return orderStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public double getTotalAmount() { return totalAmount; }
}