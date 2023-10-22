package com.keuin.blame.config;

import java.util.Objects;

@SuppressWarnings("FieldMayBeFinal")
public class DatabaseConfig {
    private String address;
    private Integer port;
    private String database;
    private String table;
    private String username;
    private String password;

    public DatabaseConfig(String address, Integer port, String database, String table, String username, String password) {
        this.address = address;
        this.port = port;
        this.database = database;
        this.table = table;
        this.username = username;
        this.password = password;
    }

    public String getAddress() {
        return this.address;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getTable() {
        return this.table;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseConfig that = (DatabaseConfig) o;
        return Objects.equals(address, that.address) && Objects.equals(port, that.port) && Objects.equals(database, that.database) && Objects.equals(table, that.table) && Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port, database, table, username, password);
    }

    @Override
    public String toString() {
        return "DatabaseConfig{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", database='" + database + '\'' +
                ", table='" + table + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}


