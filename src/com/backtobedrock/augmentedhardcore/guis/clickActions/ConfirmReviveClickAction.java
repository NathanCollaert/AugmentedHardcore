package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;

public class ConfirmReviveClickAction extends AbstractClickAction {
    private final Player reviver;
    private final PlayerData revivingData;

    public ConfirmReviveClickAction(Player reviver, PlayerData revivingData) {
        this.reviver = reviver;
        this.revivingData = revivingData;
    }

    @Override
    public void execute(Player player) {
        this.plugin.getPlayerRepository().getByPlayer(this.reviver).thenAcceptAsync(playerData -> playerData.onReviving(this.revivingData));
        player.closeInventory();
    }
}
