package com.backtobedrock.LiteDeathBan.commands;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Commands implements TabCompleter {
    private final LiteDeathBan plugin;

    public Commands() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        Collections.singletonList("litedeathban").forEach(this::registerPluginCommand);
    }

    private void registerPluginCommand(String command) {
        PluginCommand pluginCommand = Bukkit.getServer().getPluginCommand(command);
        if (pluginCommand != null)
            pluginCommand.setTabCompleter(this);
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        switch (cmnd.getName().toLowerCase()) {
            case "litedeathban":
                new LiteDeathBanCommand(cs, args).run();
                break;
            case "undeathban":
                new UnDeathBanCommand(cs, args).run();
                break;
            case "revive":
                new ReviveCommand(cs, args).run();
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmnd, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();

        switch (cmnd.getName().toLowerCase()) {
            case "litedeathban":
                switch (args.length) {
                    case 1:
                        StringUtil.copyPartialMatches(args[0].toLowerCase(), Arrays.asList("help", "addlives", "addlifeparts", "setlives", "setlifeparts", "reload"), completions);
                        Collections.sort(completions);
                        break;
                    case 2:
                        if (Arrays.asList("addlives", "addlifeparts", "setlives", "setlifeparts").contains(args[0])) {
                            StringUtil.copyPartialMatches(args[1].toLowerCase(), Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()), completions);
                            Collections.sort(completions);
                        }
                        break;
                    case 3:
                        if (Arrays.asList("addlives", "addlifeparts", "setlives", "setlifeparts").contains(args[0])) {
                            StringUtil.copyPartialMatches(args[0].toLowerCase(), Arrays.asList("1", "2", "5", "10", "50", "100"), completions);
                            Collections.sort(completions);
                        }
                        break;
                }
                break;
            case "revive":
                if (args.length == 1) {
                    StringUtil.copyPartialMatches(args[0].toLowerCase(), Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()), completions);
                    Collections.sort(completions);
                }
                break;
        }
        return completions;
    }
}
