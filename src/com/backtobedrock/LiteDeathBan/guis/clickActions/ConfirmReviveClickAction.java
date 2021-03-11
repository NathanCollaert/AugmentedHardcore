package com.backtobedrock.LiteDeathBan.guis.clickActions;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ConfirmReviveClickAction extends AbstractClickAction {
    private final Player reviver;
    private final OfflinePlayer reviving;

    public ConfirmReviveClickAction(Player reviver, OfflinePlayer reviving) {
        this.reviver = reviver;
        this.reviving = reviving;
    }

    @Override
    public void execute(Player player) {
        this.plugin.getPlayerRepository().getByPlayer(player, data -> {
            data.onReviving(this.reviver, this.reviving);
            player.closeInventory();
        });
    }
}
