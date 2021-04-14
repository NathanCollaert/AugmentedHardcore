package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Commands implements TabCompleter {
    private final AugmentedHardcore plugin;

    public Commands() {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        Collections.singletonList("augmentedhardcore").forEach(this::registerPluginCommand);
    }

    private void registerPluginCommand(String command) {
        PluginCommand pluginCommand = Bukkit.getServer().getPluginCommand(command);
        if (pluginCommand != null) {
            pluginCommand.setTabCompleter(this);
        }
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) throws ExecutionException, InterruptedException {
        switch (cmnd.getName().toLowerCase()) {
            case "augmentedhardcore":
                new CommandAugmentedHardcore(cs, args).run();
                break;
            case "undeathban":
                new CommandUnDeathBan(cs, args).run();
                break;
            case "revive":
                new CommandRevive(cs, args).run();
                break;
            case "mystats":
                new CommandMyStats(cs, args).run();
                break;
            case "nextlifepart":
                new CommandNextLifePart(cs, args).run();
                break;
            case "nextmaxhealth":
                new CommandNextMaxHealth(cs, args).run();
                break;
            case "nextrevive":
                new CommandNextRevive(cs, args).run();
                break;
            case "lifeparts":
                new CommandLifeParts(cs, args).run();
                break;
            case "lives":
                new CommandLives(cs, args).run();
                break;
            case "deathbans":
                new CommandDeathBans(cs, args).run();
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmnd, @NotNull String alias, String[] args) {
        final List<String> completions = new ArrayList<>();

        switch (cmnd.getName().toLowerCase()) {
            case "augmentedhardcore":
                switch (args.length) {
                    case 1:
                        StringUtil.copyPartialMatches(args[0].toLowerCase(), Arrays.asList("help", "addlives", "addlifeparts", "setlives", "setlifeparts", "reload", "mystats"), completions);
                        Collections.sort(completions);
                        break;
                    case 2:
                        if (Arrays.asList("addlives", "addlifeparts", "setlives", "setlifeparts").contains(args[0])) {
                            StringUtil.copyPartialMatches(args[1].toLowerCase(), Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), completions);
                            Collections.sort(completions);
                        }
                        break;
                }
                break;
        }
        return completions;
    }
}
