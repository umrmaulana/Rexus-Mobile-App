package com.example.uts_a22202302996.api;

public class CourierService {
    public String courierName;
    public String service;
    public String etd;
    public int cost;

    public CourierService(String courierName, String service, String etd, int cost) {
        this.courierName = courierName;
        this.service = service;
        this.etd = etd;
        this.cost = cost;
    }
}


