package com.example.uts_a22202302996.model;

public class OrderProduct {
    private String productId;
    private String productName;
    private int qty;
    private double price;
    private double subTotal;
    private String imageUrl; // New field for image URL

    public OrderProduct() {
        this.productId = "";
        this.productName = "";
        this.qty = 0;
        this.price = 0.0;
        this.subTotal = 0.0;
        this.imageUrl = "";
    }

    public OrderProduct(String productId, String productName, int qty, double price, double subTotal, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.qty = qty;
        this.price = price;
        this.subTotal = subTotal;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getSubTotal() { return subTotal; }
    public void setSubTotal(double subTotal) { this.subTotal = subTotal; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
