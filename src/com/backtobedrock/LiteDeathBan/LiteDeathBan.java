package com.backtobedrock.LiteDeathBan;

import java.io.File;
import com.backtobedrock.LiteDeathBan.eventHandlers.LiteDeathBanEventHandlers;
import java.util.TreeMap;
import java.util.UUID;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class LiteDeathBan extends JavaPlugin implements Listener {

    private LiteDeathBanConfig config;
    private LiteDeathBanMessages messages;
    private final LiteDeathBanCommands commands = new LiteDeathBanCommands(this);
    private final TreeMap<UUID, Integer> tagList = new TreeMap<>();
    private final TreeMap<UUID, BossBar> bars = new TreeMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        File file = new File(this.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            this.saveResource("messages.yml", false);
        }

        File dir = new File(System.getProperty("user.dir") + "/plugins/LiteDeathBan/userdata");
        dir.mkdirs();

        this.config = new LiteDeathBanConfig(getConfig());
        this.messages = new LiteDeathBanMessages(this);

        getServer().getPluginManager().registerEvents(new LiteDeathBanEventHandlers(this), this);
        super.onEnable(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        return this.commands.onCommand(cs, cmnd, alias, args);
    }

    public LiteDeathBanConfig getLDBConfig() {
        return config;
    }

    public LiteDeathBanMessages getMessages() {
        return this.messages;
    }

    public void addToTagList(UUID plyrID, int id) {
        this.tagList.put(plyrID, id);

    }

    public void removeFromTagList(UUID plyrID) {
        this.tagList.remove(plyrID);
        if (this.config.getCombatTagWarningStyle().equalsIgnoreCase("bossbar")) {
            BossBar bar = this.bars.remove(plyrID);
            bar.setVisible(false);
        }
    }

    public int getFromTagList(UUID plyrID) {
        return this.tagList.get(plyrID);
    }

    public boolean doesTagListContain(UUID plyrID) {
        return this.tagList.containsKey(plyrID);
    }

    public void addBar(UUID id, BossBar bar) {
        this.bars.put(id, bar);
    }
}
