package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;

public class PlaytimeRevive extends AbstractPlaytime {
    public PlaytimeRevive(PlayerData playerData, Player player) {
        super(playerData, player);
    }

    @Override
    protected void timerTask() {
        this.playerData.decreaseTimeTillNextRevive(this.period);
    }
}