package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;

public class PlaytimeMaxHealth extends AbstractPlaytime {
    public PlaytimeMaxHealth(PlayerData playerData, Player player) {
        super(playerData, player);
    }

    @Override
    protected void timerTask() {
        this.playerData.decreaseTimeTillNextMaxHealth(this.period, this.player);
    }
}
