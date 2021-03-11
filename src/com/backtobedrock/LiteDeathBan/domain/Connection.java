package com.backtobedrock.LiteDeathBan.domain;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Connection {
    private final String hostname;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    private final String prefix;

    public Connection(String hostname, String port, String database, String username, String password, String prefix) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.prefix = prefix;
    }

    public static Connection deserialize(ConfigurationSection section) {
        String cHostname = section.getString("Hostname");
        String cPort = section.getString("Port", "3306");
        String cDatabase = section.getString("Database");
        String cUsername = section.getString("Username");
        String cPassword = section.getString("Password");
        String cPrefix = section.getString("Prefix", "q_");

        if (cHostname == null) {
            JavaPlugin.getPlugin(LiteDeathBan.class).getLogger().log(Level.SEVERE, "Data Connection: Hostname was empty.");
            return null;

        }
        if (cDatabase == null) {
            JavaPlugin.getPlugin(LiteDeathBan.class).getLogger().log(Level.SEVERE, "Data Connection: Database was empty.");
            return null;

        }
        if (cUsername == null) {
            JavaPlugin.getPlugin(LiteDeathBan.class).getLogger().log(Level.SEVERE, "Data Connection: Username was empty.");
            return null;

        }
        if (cPassword == null) {
            JavaPlugin.getPlugin(LiteDeathBan.class).getLogger().log(Level.SEVERE, "Data Connection: Password was empty.");
            return null;
        }

        return new Connection(cHostname, cPort, cDatabase, cUsername, cPassword, cPrefix);
    }

    public String getHostname() {
        return hostname;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getConnectionString() {
        return "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true&useSSL=false";
    }
}
