package com.keuin.blame.util;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import com.clickhouse.client.config.ClickHouseClientOption;
import com.keuin.blame.Blame;
import com.keuin.blame.config.DatabaseConfig;

import static com.clickhouse.client.ClickHouseCredentials.fromUserAndPassword;

public class DatabaseUtil {

    public static final DatabaseConfig DB_CONFIG = Blame.config.getMongoConfig();

    public static ClickHouseNode getServer() {
        final var config = DatabaseUtil.DB_CONFIG;
        return ClickHouseNode.builder()
                .host(config.getAddress())
                .port(ClickHouseProtocol.HTTP, config.getPort())
                // .port(ClickHouseProtocol.GRPC, Integer.getInteger("chPort", 9100))
                // .port(ClickHouseProtocol.TCP, Integer.getInteger("chPort", 9000))
                .database(config.getDatabase())
                .credentials(fromUserAndPassword(config.getUsername(), config.getPassword()))
                .addOption(ClickHouseClientOption.COMPRESS.getKey(), "false")
                .build();
    }

    public static ClickHouseClient getClient() {
        return getClient(getServer());
    }

    public static ClickHouseClient getClient(ClickHouseNode server) {
        return ClickHouseClient.newInstance(server.getProtocol());
    }
}
