package com.tianyisun.eventsearch.artifact;

import java.io.Serializable;

public class Artist implements Serializable {

    private int followers;
    private int popularity;
    private String url;
    private String name;

    private Artist(ArtistBuilder builder) {
        this.followers = builder.followers;
        this.url = builder.url;
        this.name = builder.name;
        this.popularity = builder.popularity;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public int getFollowers() {
        return followers;
    }

    public int getPopularity() {
        return popularity;
    }

    public static class ArtistBuilder {
        private int popularity = -1;
        private int followers = -1;
        private String url;
        private String name;

        public ArtistBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public ArtistBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ArtistBuilder setPopularity(int popularity) {
            this.popularity = popularity;
            return this;
        }

        public ArtistBuilder setFollowers(int followers) {
            this.followers = followers;
            return this;
        }

        public Artist build() {
            return new Artist(this);
        }

        public ArtistBuilder clear() {
            this.popularity = -1;
            this.url = null;
            this.name = null;
            this.followers = -1;
            return this;
        }
    }
}
