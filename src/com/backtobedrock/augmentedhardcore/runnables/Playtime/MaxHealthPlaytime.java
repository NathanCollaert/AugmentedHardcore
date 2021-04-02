package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;

public class MaxHealthPlaytime extends AbstractPlaytime {
    public MaxHealthPlaytime(Player player, PlayerData playerData) {
        super(player, playerData);
    }

    @Override
    protected void timerTask() {
        this.data.decreaseTimeTillNextMaxHealth(this.player, this.period);
    }
}
