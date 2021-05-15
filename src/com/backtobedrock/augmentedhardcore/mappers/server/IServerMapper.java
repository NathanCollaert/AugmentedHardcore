package com.backtobedrock.augmentedhardcore.mappers.server;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import org.bukkit.Server;
import org.javatuples.Pair;

import java.util.UUID;
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

    void deleteBanFromServerData(UUID uuid, Pair<Integer, Ban> ban);
}
