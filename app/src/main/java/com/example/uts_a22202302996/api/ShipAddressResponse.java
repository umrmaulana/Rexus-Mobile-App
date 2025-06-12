package com.example.uts_a22202302996.api;

import com.example.uts_a22202302996.model.ShipAddress;

public class ShipAddressResponse {
    private boolean success;
    private String message;
    private ShipAddress address;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ShipAddress getAddress() { return address; }
}

