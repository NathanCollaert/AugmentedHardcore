package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.guis.AbstractPaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class ClickActionNextPage extends AbstractClickAction {
    private final AbstractPaginatedGui paginatedGui;

    public ClickActionNextPage(AbstractPaginatedGui paginatedGui) {
        this.paginatedGui = paginatedGui;
    }

    @Override
    public void execute(Player player) throws ExecutionException, InterruptedException {
        this.paginatedGui.nextPage();
        Bukkit.getScheduler().runTask(this.plugin, () -> player.openInventory(this.paginatedGui.getInventory()));
    }
}
