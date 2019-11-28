package com.example.mapapp;

public class ApiResponse {
    public PlaceInfo[] candidates;
    public String status;

    public ApiResponse(PlaceInfo[] candidates, String status) {
        this.candidates = candidates;
        this.status = status;
    }
}
