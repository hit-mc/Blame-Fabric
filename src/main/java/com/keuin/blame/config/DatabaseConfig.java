package com.keuin.blame.config;

public record DatabaseConfig(
        String address,
        int port,
        String database,
        String table,
        String username,
        String password
) {
}
