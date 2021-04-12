package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.guis.AbstractPaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NextPageClickAction extends AbstractClickAction {
    private final AbstractPaginatedGui paginatedGui;

    public NextPageClickAction(AbstractPaginatedGui paginatedGui) {
        this.paginatedGui = paginatedGui;
    }

    @Override
    public void execute(Player player) {
        this.paginatedGui.nextPage();
        Bukkit.getScheduler().runTask(this.plugin, () -> player.openInventory(this.paginatedGui.getInventory()));
    }
}
