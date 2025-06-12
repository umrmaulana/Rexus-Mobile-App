package com.example.uts_a22202302996.model;

public class Order {
    private int id;
    private String orderNumber;
    private int userId;
    private int shipAddressId;
    private double subTotal;
    private double shippingCost;
    private double finalPrice;
    private String courier;
    private String courierService;
    private String estimatedDay;
    private double totalWeight;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String proofTransfer;
    private String createdAt;

    public int getOrderId() {
        return id;
    }
    public void setOrderId(int id) {
        this.id = id;
    }
    public String getOrderNumber() {
        return orderNumber;
    }
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getShipAddressId() {
        return shipAddressId;
    }
    public void setShipAddressId(int shipAddressId) {
        this.shipAddressId = shipAddressId;
    }
    public double getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
    public double getShippingCost() {
        return shippingCost;
    }
    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }
    public double getFinalPrice() {
        return finalPrice;
    }
    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }
    public String getCourier() {
        return courier;
    }
    public void setCourier(String courier) {
        this.courier = courier;
    }
    public String getCourierService() {
        return courierService;
    }
    public void setCourierService(String courierService) {
        this.courierService = courierService;
    }
    public String getEstimatedDay() {
        return estimatedDay;
    }
    public void setEstimatedDay(String estimatedDay) {
        this.estimatedDay = estimatedDay;
    }
    public double getTotalWeight() {
        return totalWeight;
    }
    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public String getProofTransfer() {
        return proofTransfer;
    }
    public void setProofTransfer(String proofTransfer) {
        this.proofTransfer = proofTransfer;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
