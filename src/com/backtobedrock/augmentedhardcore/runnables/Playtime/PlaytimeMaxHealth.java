package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;

public class PlaytimeMaxHealth extends AbstractPlaytime {
    public PlaytimeMaxHealth(PlayerData playerData) {
        super(playerData);
    }

    @Override
    protected void timerTask() {
        this.playerData.decreaseTimeTillNextMaxHealth(this.period);
    }
}
