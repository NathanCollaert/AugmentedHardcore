package com.backtobedrock.LiteDeathBan.mappers.server;

import com.backtobedrock.LiteDeathBan.domain.ServerData;
import com.backtobedrock.LiteDeathBan.domain.callbacks.ServerDataCallback;

public interface IServerMapper {
    //Create
    void insertServerDataAsync(ServerData data);

    void insertServerDataSync(ServerData data);

    //Read
    void getServerData(ServerDataCallback callback);

    //Update
    void updateServerData(ServerData data);

    //Delete
    void deleteServerData();
}
