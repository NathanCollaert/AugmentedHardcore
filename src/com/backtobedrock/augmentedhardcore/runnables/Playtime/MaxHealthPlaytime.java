package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;

public class MaxHealthPlaytime extends AbstractPlaytime {
    public MaxHealthPlaytime(PlayerData playerData) {
        super(playerData);
    }

    @Override
    protected void timerTask() {
        this.data.decreaseTimeTillNextMaxHealth(this.period);
    }
}
