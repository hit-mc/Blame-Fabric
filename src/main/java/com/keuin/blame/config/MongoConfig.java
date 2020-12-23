package com.keuin.blame.config;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class MongoConfig {

    private final String address;
    private final String username;
    private final String password;
    @SerializedName("database")
    private final String databaseName;
    @SerializedName("collection")
    private final String logCollectionName;

    public MongoConfig(String address, String username, String password, String databaseName, String logCollectionName) {
        this.address = address;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
        this.logCollectionName = logCollectionName;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getLogCollectionName() {
        return logCollectionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoConfig that = (MongoConfig) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(databaseName, that.databaseName) &&
                Objects.equals(logCollectionName, that.logCollectionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, username, password, databaseName, logCollectionName);
    }

    @Override
    public String toString() {
        return "MongoConfig{" +
                "address='" + address + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", logCollectionName='" + logCollectionName + '\'' +
                '}';
    }
}
