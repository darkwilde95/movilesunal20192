package com.example.mapapp;

public class PlaceInfo {

    public Geometry geometry;
    public String name;
    public String[] types;

    public PlaceInfo(Geometry geometry, String name, String[] types) {
        this.geometry = geometry;
        this.name = name;
        this.types = types;
    }

    public boolean valid() {
        for (String type : types) {
            if (type.equalsIgnoreCase("point_of_interest")) return true;
        }
        return false;
    }

    public class Geometry {

        public Location location;

        public Geometry(Location location) {
            this.location = location;
        }

        public class Location {
            public double lat;
            public double lng;

            public Location(double lat, double lng) {
                this.lat = lat;
                this.lng = lng;
            }
        }
    }
}
