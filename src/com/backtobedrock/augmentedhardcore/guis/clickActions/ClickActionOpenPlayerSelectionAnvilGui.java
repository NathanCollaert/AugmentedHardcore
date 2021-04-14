package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.commands.CommandRevive;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class ClickActionOpenPlayerSelectionAnvilGui extends AbstractClickAction {

    @Override
    public void execute(Player player) {
        new AnvilGUI.Builder()
                .onComplete((playerComplete, text) -> {
                    try {
                        new CommandRevive(playerComplete, new String[]{text}).run();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return AnvilGUI.Response.text("");
                })
                .text("Player Name")
                .title("Who needs a revive?")
                .plugin(this.plugin)
                .open(player);
    }
}
