package com.backtobedrock.LiteDeathBan.domain.callbacks;

import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;

public interface IPlayerDataCallback {
    void onQueryDonePlayerData(PlayerData data);
}
