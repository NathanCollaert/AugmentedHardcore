package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.commands.CommandRevive;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ClickActionOpenPlayerSelectionAnvilGui extends AbstractClickAction {
    @Override
    public void execute(Player player) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    new CommandRevive(stateSnapshot.getPlayer(), new String[]{stateSnapshot.getText()}).run();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                })
                .text("Player Name")
                .title("Who needs a revive?")
                .plugin(this.plugin)
                .open(player);
    }
}
