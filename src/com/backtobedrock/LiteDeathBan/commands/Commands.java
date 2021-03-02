package com.backtobedrock.LiteDeathBan.commands;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Commands implements TabCompleter {
    private final LiteDeathBan plugin;

    public Commands() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        switch (cmnd.getName().toLowerCase()) {
            case "litedeathban":
                new LiteDeathBanCommand(cs, args).run();
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmnd, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();

//        switch (cmnd.getName().toLowerCase()) {
//            case "tcquests":
//                if (args.length > 0) {
//                    switch (args[0]) {
//                        case "serverquest":
//                            StringUtil.copyPartialMatches(args[1].toLowerCase(), Arrays.asList("last", "left", "new", "next"), completions);
//                            Collections.sort(completions);
//                            break;
//                        default:
//                            StringUtil.copyPartialMatches(args[0].toLowerCase(), Arrays.asList("help", "reload", "serverquest"), completions);
//                            Collections.sort(completions);
//                            break;
//                    }
//                }
//        }
        return completions;
    }
}
