package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.guis.ReviveGui;
import com.backtobedrock.augmentedhardcore.utils.PlayerUtils;
import org.bukkit.command.CommandSender;

public class ReviveCommand extends AbstractCommand {
    public ReviveCommand(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.REVIVE;

        if (!this.hasPermission(command)) {
            return;
        }

        if (!this.isPlayer()) {
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }

        this.hasPlayedBefore(this.args[0]).thenAcceptAsync(bool -> {
            //check if reviving is enabled
            if (!this.plugin.getConfigurations().getReviveConfiguration().isUseRevive()) {
                this.sender.sendMessage("§cReviving is not enabled on the server.");
                return;
            }

            //check if not same player
            if (this.sender.getUniqueId().equals(this.target.getUniqueId())) {
                this.sender.sendMessage("§cYou cannot revive yourself, that would break the space-time continuum!");
                return;
            }

            //check if in disabled world
            if (this.plugin.getConfigurations().getReviveConfiguration().getDisableReviveInWorlds().contains(this.sender.getWorld().getName().toLowerCase())) {
                this.sender.sendMessage(String.format("§cYou cannot revive while in this world (%s).", this.sender.getWorld().getName()));
                return;
            }

            PlayerUtils.openInventory(this.sender, new ReviveGui(this.sender, this.target).getInventory());
        });
    }
}
