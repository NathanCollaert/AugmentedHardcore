package com.backtobedrock.LiteDeathBan.utils;

import com.backtobedrock.LiteDeathBan.domain.enums.Command;
import org.bukkit.command.CommandSender;

public class CommandUtils {
    public static Command getCommand(String command) {
        try {
            return Command.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static int getPositiveNumberFromString(CommandSender sender, String number) {
        try {
            int convertedNumber = Integer.parseInt(number);
            if (convertedNumber < 1) {
                sender.sendMessage(String.format("§c%s is not a valid number between 1 and %d.", number, Integer.MAX_VALUE));
                return -1;
            }
            return convertedNumber;
        } catch (NumberFormatException e) {
            sender.sendMessage(String.format("§c%s is not a valid number between 1 and %d.", number, Integer.MAX_VALUE));
            return -1;
        }
    }
}
