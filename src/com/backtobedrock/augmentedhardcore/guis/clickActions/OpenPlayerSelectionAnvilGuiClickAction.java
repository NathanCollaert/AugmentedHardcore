package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.commands.ReviveCommand;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

public class OpenPlayerSelectionAnvilGuiClickAction extends AbstractClickAction {

    @Override
    public void execute(Player player) {
        new AnvilGUI.Builder()
                .onComplete((playerComplete, text) -> {
                    new ReviveCommand(playerComplete, new String[]{text}).run();
                    return AnvilGUI.Response.text("");
                })
                .text("Player Name")
                .title("Who needs a revive?")
                .plugin(this.plugin)
                .open(player);
    }
}
