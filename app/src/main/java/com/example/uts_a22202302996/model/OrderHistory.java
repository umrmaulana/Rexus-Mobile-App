package com.example.uts_a22202302996.model;

public class OrderHistory {
    private int id;
    private String orderNumber;
    private String orderDate;
    private String orderStatus;
    private String paymentStatus;
    private  String shippingInfo;
    private  String estimatedDelivery;
    private double totalAmount;

    public OrderHistory(int id, String orderNumber, String orderDate, String orderStatus, String paymentStatus, String shippingInfo, String estimatedDelivery, double totalAmount) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.shippingInfo = shippingInfo;
        this.estimatedDelivery = estimatedDelivery;
        this.totalAmount = totalAmount;
    }

    // Getters
    public int getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getOrderDate() { return orderDate; }
    public String getOrderStatus() { return orderStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getShippingInfo() { return shippingInfo; }
    public String getEstimatedDelivery() { return estimatedDelivery; }
    public double getTotalAmount() { return totalAmount; }
}