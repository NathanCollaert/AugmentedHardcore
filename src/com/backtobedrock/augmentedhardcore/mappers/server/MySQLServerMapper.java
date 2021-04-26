package com.backtobedrock.augmentedhardcore.mappers.server;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.mappers.AbstractMapper;
import com.backtobedrock.augmentedhardcore.mappers.ban.MySQLBanMapper;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLServerMapper extends AbstractMapper implements IServerMapper {

    private static MySQLServerMapper instance;

    public static MySQLServerMapper getInstance() {
        if (instance == null) {
            instance = new MySQLServerMapper();
        }
        return instance;
    }

    @Override
    public void insertServerDataAsync(ServerData serverData) {
        CompletableFuture.runAsync(() -> this.updateServerData(serverData)).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public void insertServerDataSync(ServerData serverData) {
        Bukkit.getScheduler().runTask(this.plugin, () -> this.updateServerData(serverData));
    }

    @Override
    public CompletableFuture<ServerData> getServerData(Server server) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * "
                    + "FROM ah_ban AS b "
                    + "RIGHT OUTER JOIN ah_server as s ON b.server_ip = s.server_ip AND b.server_port = s.server_port "
                    + "WHERE s.server_ip = ? AND s.server_port = ?;";

            try (Connection connection = this.database.getDataSource().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, InetAddress.getLocalHost().getHostAddress());
                preparedStatement.setInt(2, server.getPort());
                ResultSet resultSet = preparedStatement.executeQuery();
                Map<UUID, Pair<Integer, Ban>> deathBans = new HashMap<>();
                ServerData serverData = null;
                while (resultSet.next()) {
                    if (serverData == null) {
                        serverData = new ServerData(resultSet.getInt("total_death_bans"), deathBans);
                    }
                    String uuidString = resultSet.getString("player_uuid");
                    if (uuidString != null && !uuidString.isEmpty()) {
                        Pair<Integer, Ban> banPair = MySQLBanMapper.getInstance().getBanFromResultSetSync(resultSet);
                        if (banPair != null) {
                            deathBans.put(UUID.fromString(uuidString), banPair);
                        }
                    }
                }
                return serverData;
            } catch (SQLException | UnknownHostException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public void updateServerData(ServerData data) {
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO ah_server (`server_ip`, `server_port`, `total_death_bans`)"
                    + "VALUES(?, ?, ?)"
                    + "ON DUPLICATE KEY UPDATE `total_death_bans` = ?;";

            try (Connection connection = this.database.getDataSource().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, InetAddress.getLocalHost().getHostAddress());
                preparedStatement.setInt(2, this.plugin.getServer().getPort());
                preparedStatement.setInt(3, data.getTotalBans());
                preparedStatement.setInt(4, data.getTotalBans());
                preparedStatement.execute();
            } catch (SQLException | UnknownHostException e) {
                e.printStackTrace();
                return;
            }
            data.getOngoingBans().forEach((key, value) -> MySQLBanMapper.getInstance().updateBan(this.plugin.getServer(), key, value));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public void deleteServerData() {
        CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM ah_server" +
                    "WHERE server_ip = ? AND server_port = ?;";

            try (Connection connection = this.database.getDataSource().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, InetAddress.getLocalHost().getHostAddress());
                preparedStatement.setInt(2, this.plugin.getServer().getPort());
                preparedStatement.execute();
            } catch (SQLException | UnknownHostException e) {
                e.printStackTrace();
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public void deleteBanFromServerData(OfflinePlayer player, Pair<Integer, Ban> ban) {
        MySQLBanMapper.getInstance().updateBan(null, player.getUniqueId(), ban);
    }
}
