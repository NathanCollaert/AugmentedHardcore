package com.backtobedrock.LiteDeathBan.guis.clickActions;

import org.bukkit.entity.Player;

public class CloseInventoryClickAction extends AbstractClickAction {
    @Override
    public void execute(Player player) {
        player.closeInventory();
    }
}
