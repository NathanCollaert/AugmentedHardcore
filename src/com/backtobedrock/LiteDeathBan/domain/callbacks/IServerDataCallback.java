package com.backtobedrock.LiteDeathBan.domain.callbacks;

import com.backtobedrock.LiteDeathBan.domain.data.ServerData;

public interface IServerDataCallback {
    void onQueryDoneServerData(ServerData data);
}
