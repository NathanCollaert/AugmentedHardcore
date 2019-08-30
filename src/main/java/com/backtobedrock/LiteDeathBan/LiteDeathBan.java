package com.backtobedrock.LiteDeathBan;

import com.backtobedrock.LiteDeathBan.eventHandlers.LiteDeathBanEventHandlers;
import java.io.File;
import java.io.IOException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class LiteDeathBan extends JavaPlugin implements Listener {

    private final LiteDeathBanConfig config = new LiteDeathBanConfig(getConfig());
    private final LiteDeathBanEventHandlers eventHandlers = new LiteDeathBanEventHandlers(this);
    private final LiteDeathBanCommands commands = new LiteDeathBanCommands(this);

    @Override
    public void onEnable() {
        super.onEnable(); //To change body of generated methods, choose Tools | Templates.
        getServer().getPluginManager().registerEvents(new LiteDeathBanEventHandlers(this), this);
        this.saveDefaultConfig();
        try {
            File file = new File(System.getProperty("user.dir") + "/plugins/LiteDeathBan/PlayerData.json");
            if (!file.createNewFile()) {
                System.out.println("Initialising Player Data...");
            } else {
                System.out.println("Creating New Player Data file...");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        return this.commands.onCommand(cs, cmnd, alias, args);
    }

}
