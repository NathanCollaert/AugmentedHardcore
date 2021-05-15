package com.backtobedrock.augmentedhardcore.mappers.ban;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.Location;
import com.backtobedrock.augmentedhardcore.mappers.AbstractMapper;
import com.backtobedrock.augmentedhardcore.utilities.ConfigUtils;
import org.bukkit.Server;
import org.javatuples.Pair;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLBanMapper extends AbstractMapper implements IBanMapper {
    private static MySQLBanMapper instance;

    public static MySQLBanMapper getInstance() {
        if (instance == null) {
            instance = new MySQLBanMapper();
        }
        return instance;
    }

    public Pair<Integer, Ban> getBanFromResultSetSync(ResultSet resultSet) {
        try {
            if (resultSet.getObject("ban_id") != null) {
                return new Pair<>(resultSet.getInt("ban_id"),
                        new Ban(
                                resultSet.getTimestamp("start_date").toLocalDateTime(),
                                resultSet.getTimestamp("expiration_date").toLocalDateTime(),
                                resultSet.getInt("ban_time"),
                                ConfigUtils.getDamageCause(resultSet.getString("damage_cause")),
                                ConfigUtils.getDamageCauseType(resultSet.getString("damage_cause_type")),
                                new Location(resultSet.getString("world"), resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z")),
                                resultSet.getBoolean("has_killer") ? new Killer(resultSet.getString("killer_name"), resultSet.getString("killer_display_name"), ConfigUtils.getEntityType(resultSet.getString("killer_entity_type"))) : null,
                                resultSet.getBoolean("in_combat") ? new Killer(resultSet.getString("in_combat_with_name"), resultSet.getString("in_combat_with_display_name"), ConfigUtils.getEntityType(resultSet.getString("in_combat_with_entity_type"))) : null,
                                resultSet.getString("death_message"),
                                resultSet.getLong("time_since_previous_death_ban"),
                                resultSet.getLong("time_since_previous_death")
                        )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insertBan(Server server, UUID uuid, Pair<Integer, Ban> ban) {
        this.updateBan(server, uuid, ban);
    }

    @Override
    public void updateBan(Server server, UUID uuid, Pair<Integer, Ban> ban) {
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO ah_ban (`ban_id`,`player_uuid`,`server_ip`,`server_port`,`start_date`,`expiration_date`,`ban_time`,`damage_cause`,`damage_cause_type`,`world`,`x`,`y`,`z`,`has_killer`,`killer_name`,`killer_display_name`,`killer_entity_type`,`in_combat`,`in_combat_with_name`,`in_combat_with_display_name`,`in_combat_with_entity_type`,`death_message`,`time_since_previous_death_ban`,`time_since_previous_death`)"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    + "ON DUPLICATE KEY UPDATE "
                    + "`server_ip` = ?,"
                    + "`server_port` = ?,"
                    + "`start_date` = ?,"
                    + "`expiration_date` = ?,"
                    + "`ban_time` = ?,"
                    + "`damage_cause` = ?,"
                    + "`damage_cause_type` = ?,"
                    + "`world` = ?,"
                    + "`x` = ?,"
                    + "`y` = ?,"
                    + "`z` = ?,"
                    + "`has_killer`= ?,"
                    + "`killer_name`= ?,"
                    + "`killer_display_name`= ?,"
                    + "`killer_entity_type`= ?,"
                    + "`in_combat`= ?,"
                    + "`in_combat_with_name`= ?,"
                    + "`in_combat_with_display_name`= ?,"
                    + "`in_combat_with_entity_type`= ?,"
                    + "`death_message` = ?,"
                    + "`time_since_previous_death_ban` = ?,"
                    + "`time_since_previous_death` = ?;";

            try (Connection connection = this.database.getDataSource().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, ban.getValue0());
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.setString(3, server != null ? InetAddress.getLocalHost().getHostAddress() : null);
                preparedStatement.setObject(4, server != null ? this.plugin.getServer().getPort() : null);
                preparedStatement.setTimestamp(5, Timestamp.valueOf(ban.getValue1().getStartDate()));
                preparedStatement.setTimestamp(6, Timestamp.valueOf(ban.getValue1().getExpirationDate()));
                preparedStatement.setInt(7, ban.getValue1().getBanTime());
                preparedStatement.setString(8, ban.getValue1().getDamageCause().name());
                preparedStatement.setString(9, ban.getValue1().getDamageCauseType().name());
                preparedStatement.setString(10, ban.getValue1().getLocation().getWorld());
                preparedStatement.setDouble(11, ban.getValue1().getLocation().getX());
                preparedStatement.setDouble(12, ban.getValue1().getLocation().getY());
                preparedStatement.setDouble(13, ban.getValue1().getLocation().getZ());
                preparedStatement.setBoolean(14, ban.getValue1().getKiller() != null);
                preparedStatement.setString(15, ban.getValue1().getKiller() == null ? null : ban.getValue1().getKiller().getName());
                preparedStatement.setString(16, ban.getValue1().getKiller() == null ? null : ban.getValue1().getKiller().getDisplayName());
                preparedStatement.setString(17, ban.getValue1().getKiller() == null ? null : ban.getValue1().getKiller().getType().name());
                preparedStatement.setBoolean(18, ban.getValue1().getInCombatWith() != null);
                preparedStatement.setString(19, ban.getValue1().getInCombatWith() == null ? null : ban.getValue1().getInCombatWith().getName());
                preparedStatement.setString(20, ban.getValue1().getInCombatWith() == null ? null : ban.getValue1().getInCombatWith().getDisplayName());
                preparedStatement.setString(21, ban.getValue1().getInCombatWith() == null ? null : ban.getValue1().getInCombatWith().getType().name());
                preparedStatement.setString(22, ban.getValue1().getDeathMessage());
                preparedStatement.setLong(23, ban.getValue1().getTimeSincePreviousDeathBan());
                preparedStatement.setLong(24, ban.getValue1().getTimeSincePreviousDeath());
                preparedStatement.setString(25, server != null ? InetAddress.getLocalHost().getHostAddress() : null);
                preparedStatement.setObject(26, server != null ? this.plugin.getServer().getPort() : null);
                preparedStatement.setTimestamp(27, Timestamp.valueOf(ban.getValue1().getStartDate()));
                preparedStatement.setTimestamp(28, Timestamp.valueOf(ban.getValue1().getExpirationDate()));
                preparedStatement.setInt(29, ban.getValue1().getBanTime());
                preparedStatement.setString(30, ban.getValue1().getDamageCause().name());
                preparedStatement.setString(31, ban.getValue1().getDamageCauseType().name());
                preparedStatement.setString(32, ban.getValue1().getLocation().getWorld());
                preparedStatement.setDouble(33, ban.getValue1().getLocation().getX());
                preparedStatement.setDouble(34, ban.getValue1().getLocation().getY());
                preparedStatement.setDouble(35, ban.getValue1().getLocation().getZ());
                preparedStatement.setBoolean(36, ban.getValue1().getKiller() != null);
                preparedStatement.setString(37, ban.getValue1().getKiller() == null ? null : ban.getValue1().getKiller().getName());
                preparedStatement.setString(38, ban.getValue1().getKiller() == null ? null : ban.getValue1().getKiller().getDisplayName());
                preparedStatement.setString(39, ban.getValue1().getKiller() == null ? null : ban.getValue1().getKiller().getType().name());
                preparedStatement.setBoolean(40, ban.getValue1().getInCombatWith() != null);
                preparedStatement.setString(41, ban.getValue1().getInCombatWith() == null ? null : ban.getValue1().getInCombatWith().getName());
                preparedStatement.setString(42, ban.getValue1().getInCombatWith() == null ? null : ban.getValue1().getInCombatWith().getDisplayName());
                preparedStatement.setString(43, ban.getValue1().getInCombatWith() == null ? null : ban.getValue1().getInCombatWith().getType().name());
                preparedStatement.setString(44, ban.getValue1().getDeathMessage());
                preparedStatement.setLong(45, ban.getValue1().getTimeSincePreviousDeathBan());
                preparedStatement.setLong(46, ban.getValue1().getTimeSincePreviousDeath());
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
    public void deleteBan(UUID uuid, Integer id) {
        CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM ah_ban " +
                    "WHERE ban_id = ? AND player_uuid = ?;";

            try (Connection connection = this.database.getDataSource().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, id.toString());
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}
