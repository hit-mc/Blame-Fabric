package com.keuin.blame.config;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class BlameConfig {

    @SerializedName("database")
    private final MongoConfig mongoConfig;


    public BlameConfig(MongoConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
    }

    public MongoConfig getMongoConfig() {
        return mongoConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlameConfig that = (BlameConfig) o;
        return Objects.equals(mongoConfig, that.mongoConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mongoConfig);
    }

    @Override
    public String toString() {
        return "BlameConfig{" +
                "mongoConfig=" + mongoConfig +
                '}';
    }
}
