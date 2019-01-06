package com.tianyisun.eventsearch.artifact;

import java.io.Serializable;

public class User implements Serializable {

    private String keyword;
    private String category;
    private int distance;
    private String unit;
    private boolean isHere;
    private String address;
    private double lat, lng;

    private User(UserBuilder builder) {
        this.keyword = builder.keyword;
        this.category = builder.category;
        this.distance = builder.distance;
        this.unit = builder.unit;
        this.isHere = builder.isHere;
        this.address = builder.address;
        this.lat = builder.lat;
        this.lng = builder.lng;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getCategory() {
        return category;
    }

    public int getDistance() {
        return distance;
    }

    public String getUnit() {
        return unit;
    }

    public boolean isHere() {
        return isHere;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public static class UserBuilder {
        private String keyword;
        private String category;
        private int distance;
        private String unit;
        private boolean isHere;
        private String address;
        private double lat, lng;

        public UserBuilder setKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public UserBuilder setCategory(String category) {
            this.category = category;
            return this;
        }

        public UserBuilder setDistance(int distance) {
            this.distance = distance;
            return this;
        }

        public UserBuilder setUnit(String unit) {
            this.unit = unit.toLowerCase();
            return this;
        }

        public UserBuilder setHere(boolean here) {
            isHere = here;
            return this;
        }

        public UserBuilder setAddress(String address) {
            this.address = address;
            return this;
        }

        public UserBuilder setLat(double lat) {
            this.lat = lat;
            return this;
        }

        public UserBuilder setLng(double lng) {
            this.lng = lng;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
