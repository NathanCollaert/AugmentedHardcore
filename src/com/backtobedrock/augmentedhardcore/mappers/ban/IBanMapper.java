package com.backtobedrock.augmentedhardcore.mappers.ban;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import javafx.util.Pair;
import org.bukkit.Server;

import java.util.UUID;

public interface IBanMapper {
    void insertBan(Server server, UUID uuid, Pair<Integer, Ban> ban);

    void updateBan(Server server, UUID uuid, Pair<Integer, Ban> ban);

    void deleteBan(UUID uuid, Integer id);
}
