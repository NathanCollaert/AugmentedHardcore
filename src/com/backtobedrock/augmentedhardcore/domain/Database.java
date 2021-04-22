package com.backtobedrock.augmentedhardcore.domain;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Database {
    private final String hostname;
    private final String port;
    private final String databaseName;
    private final String username;
    private final String password;

    private HikariDataSource dataSource;

    public Database(String hostname, String port, String databaseName, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    public static Database deserialize(ConfigurationSection section) {
        String cHostname = section.getString("Hostname");
        String cPort = section.getString("Port", "3306");
        String cDatabase = section.getString("Database");
        String cUsername = section.getString("Username");
        String cPassword = section.getString("Password");

        if (cHostname == null) {
            JavaPlugin.getPlugin(AugmentedHardcore.class).getLogger().log(Level.SEVERE, "Data Connection: Hostname was empty.");
            return null;

        }
        if (cDatabase == null) {
            JavaPlugin.getPlugin(AugmentedHardcore.class).getLogger().log(Level.SEVERE, "Data Connection: Database was empty.");
            return null;

        }
        if (cUsername == null) {
            JavaPlugin.getPlugin(AugmentedHardcore.class).getLogger().log(Level.SEVERE, "Data Connection: Username was empty.");
            return null;

        }
        if (cPassword == null) {
            JavaPlugin.getPlugin(AugmentedHardcore.class).getLogger().log(Level.SEVERE, "Data Connection: Password was empty.");
            return null;
        }

        return new Database(cHostname, cPort, cDatabase, cUsername, cPassword);
    }

    public String getHostname() {
        return hostname;
    }

    public String getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public HikariDataSource getDataSource() {
        if (this.dataSource == null) {
            this.dataSource = new HikariDataSource();
            this.dataSource.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", this.getHostname(), this.getPort(), this.getDatabaseName()));
            this.dataSource.setUsername(this.getUsername());
            this.dataSource.setPassword(this.getPassword());
//            this.dataSource.addDataSourceProperty("autoReconnect", "true");
            this.dataSource.addDataSourceProperty("useSSL", "false");
            this.dataSource.addDataSourceProperty("cachePrepStmts", "true");
            this.dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
            this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            this.dataSource.addDataSourceProperty("useServerPrepStmts", "true");
        }
        return this.dataSource;
    }
}
