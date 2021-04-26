package com.backtobedrock.augmentedhardcore.mappers.server;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import javafx.util.Pair;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import java.util.concurrent.CompletableFuture;

public interface IServerMapper {
    //Create
    void insertServerDataAsync(ServerData data);

    void insertServerDataSync(ServerData data);

    //Read
    CompletableFuture<ServerData> getServerData(Server server);

    //Update
    void updateServerData(ServerData data);

    //Delete
    void deleteServerData();

    void deleteBanFromServerData(OfflinePlayer player, Pair<Integer, Ban> ban);
}
