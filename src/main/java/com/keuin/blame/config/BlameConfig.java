package com.keuin.blame.config;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class BlameConfig {

    @SerializedName("database")
    private final DatabaseConfig databaseConfig;


    public BlameConfig(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public DatabaseConfig getMongoConfig() {
        return databaseConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlameConfig that = (BlameConfig) o;
        return Objects.equals(databaseConfig, that.databaseConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseConfig);
    }

    @Override
    public String toString() {
        return "BlameConfig{" +
                "databaseConfig=" + databaseConfig +
                '}';
    }
}
