package com.backtobedrock.LiteDeathBan;

import java.io.File;
import com.backtobedrock.LiteDeathBan.eventHandlers.LiteDeathBanEventHandlers;
import java.util.ArrayList;
import java.util.List;
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
    private LiteDeathBanCommands commands;
    private final TreeMap<UUID, Integer> tagList = new TreeMap<>();
    private final TreeMap<UUID, BossBar> bars = new TreeMap<>();
    private final TreeMap<UUID, String> confirmationList = new TreeMap<>();
    private final TreeMap<UUID, Integer> confirmationRunners = new TreeMap<>();
    private final List<UUID> usedRevive = new ArrayList<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        File file = new File(this.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            this.saveResource("messages.yml", false);
        }

        File dir = new File(this.getDataFolder() + "/userdata");
        dir.mkdirs();

        this.config = new LiteDeathBanConfig(getConfig());
        this.messages = new LiteDeathBanMessages(this);
        this.commands = new LiteDeathBanCommands(this);

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

    public void addToConfirmation(UUID plyrID, String name, int id) {
        this.confirmationList.put(plyrID, name);
        this.confirmationRunners.put(plyrID, id);

    }

    public void removeFromConfirmation(UUID plyrID) {
        this.confirmationList.remove(plyrID);
        this.confirmationRunners.remove(plyrID);
    }

    public String getFromConfirmation(UUID plyrID) {
        return this.confirmationList.get(plyrID);
    }

    public boolean doesConfirmationContain(UUID plyrID) {
        return this.confirmationList.containsKey(plyrID);
    }

    public void addToUsedRevive(UUID plyrID) {
        this.usedRevive.add(plyrID);
    }

    public void removeFromUsedRevive(UUID plyrID) {
        this.usedRevive.remove(plyrID);
    }

    public boolean doesUsedReviveContain(UUID plyrID) {
        return this.usedRevive.contains(plyrID);
    }
}
