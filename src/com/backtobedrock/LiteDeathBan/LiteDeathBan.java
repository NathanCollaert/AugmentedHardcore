package com.backtobedrock.LiteDeathBan;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import com.backtobedrock.LiteDeathBan.eventHandlers.LiteDeathBanEventHandlers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class LiteDeathBan extends JavaPlugin implements Listener {

    private LiteDeathBanConfig config;
    private final LiteDeathBanEventHandlers eventHandlers = new LiteDeathBanEventHandlers(this);
    private final LiteDeathBanCommands commands = new LiteDeathBanCommands(this);
    public Logger log = Bukkit.getLogger();

    @Override
    public void onLoad() {
        super.onLoad(); //To change body of generated methods, choose Tools | Templates.
        this.saveDefaultConfig();
        this.config = new LiteDeathBanConfig(getConfig());
        try {
            File file = new File(System.getProperty("user.dir") + "/plugins/LiteDeathBan/PlayerData.json");
            if (!file.createNewFile()) {
                log.info("[LDB] Initialising Player Data...");
                LiteDeathBanCRUD.getInstance().readAllPlayerDataFromFile();
                log.info("[LDB] Initialising Player Data Finished.");
            } else {
                log.info("[LDB] Creating New Player Data file...");
            }
        } catch (IOException ex) {
            log.warning(ex.getMessage());
        }
    }

    @Override
    public void onEnable() {
        super.onEnable(); //To change body of generated methods, choose Tools | Templates.
        getServer().getPluginManager().registerEvents(new LiteDeathBanEventHandlers(this), this);
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

}
