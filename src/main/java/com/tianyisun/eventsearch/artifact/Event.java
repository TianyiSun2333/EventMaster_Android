package com.tianyisun.eventsearch.artifact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {

    private int index = -1;
    private String url;
    private String name;
    private String date;
    private String time;
    private String genre;
    private String segment;
    private String venue;
    private String id;
    private List<String> teams;
    private boolean isFaved;

    private Event(EventBuilder builder) {
        this.index = builder.index;
        this.url = builder.url;
        this.name = builder.name;
        this.date = builder.date;
        this.time = builder.time;
        this.genre = builder.genre;
        this.segment = builder.segment;
        this.venue = builder.venue;
        this.id = builder.id;
        this.teams = builder.teams;
        this.isFaved = builder.isFaved;
    }

    public int getIndex() {
        return index;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getGenre() {
        return genre;
    }

    public String getSegment() {
        return segment;
    }

    public String getVenue() {
        return venue;
    }

    public String getId() {
        return id;
    }

    public List<String> getTeams() {
        return teams;
    }

    public boolean isFaved() {
        return isFaved;
    }

    public void fav(boolean fav) {
        this.isFaved = fav;
    }

    public static class EventBuilder {
        private int index;
        private String url;
        private String name;
        private String date;
        private String time;
        private String genre;
        private String segment;
        private String venue;
        private String id;
        private List<String> teams = new ArrayList<>();
        private boolean isFaved;

        public EventBuilder setIndex(int index) {
            this.index = index;
            return this;
        }

        public EventBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public EventBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public EventBuilder setDate(String date) {
            this.date = date;
            return this;
        }

        public EventBuilder setTime(String time) {
            this.time = time;
            return this;
        }

        public EventBuilder setGenre(String genre) {
            this.genre = genre;
            return this;
        }

        public EventBuilder setVenue(String venue) {
            this.venue = venue;
            return this;
        }

        public EventBuilder setTeams(List<String> teams) {
            this.teams = teams;
            return this;
        }

        public EventBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public EventBuilder setSegment(String segment) {
            this.segment = segment;
            return this;
        }

        public EventBuilder faved(boolean faved) {
            this.isFaved = faved;
            return this;
        }

        public Event build() {
            return new Event(this);
        }

        public EventBuilder clear() {
            this.index = -1;
            this.url = null;
            this.name = null;
            this.date = null;
            this.time = null;
            this.genre = null;
            this.segment = null;
            this.venue = null;
            this.id = null;
            this.teams = new ArrayList<>();
            this.isFaved = false;
            return this;
        }
    }
}
