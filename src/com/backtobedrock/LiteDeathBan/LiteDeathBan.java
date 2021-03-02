package com.backtobedrock.LiteDeathBan;

import com.backtobedrock.LiteDeathBan.commands.Commands;
import com.backtobedrock.LiteDeathBan.configs.Configuration;
import com.backtobedrock.LiteDeathBan.configs.Messages;
import com.backtobedrock.LiteDeathBan.eventListeners.*;
import com.backtobedrock.LiteDeathBan.repositories.PlayerRepository;
import com.backtobedrock.LiteDeathBan.repositories.ServerRepository;
import com.backtobedrock.LiteDeathBan.utils.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class LiteDeathBan extends JavaPlugin implements Listener {

    //configurations
    private Commands commands;
    private Configuration configuration;
    private Messages messages;

    //repositories
    private PlayerRepository playerRepository = null;
    private ServerRepository serverRepository = null;
//    private CombatTagRepository combatTagRepository = null;

    //various
    private boolean stopping = false;

    @Override
    public void onEnable() {
        //initialize plugin
        this.initialize();

        //register event listeners
        this.registerListeners();

        //bstats metrics
        Metrics metrics = new Metrics(this, 5655);

        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.stopping = true;
        this.getServerRepository().getServerData(data -> {
            this.getServerRepository().updateServerData(data);
        });
        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        return this.commands.onCommand(cs, cmnd, alias, args);
    }

    public void initialize() {
        //create userdata folder if none existent
        File udFile = new File(this.getDataFolder() + "/userdata");
        if (udFile.mkdirs()) {
            this.getLogger().log(Level.INFO, "Creating {0}.", udFile.getAbsolutePath());
        }

        //get config.yml and make if none existent
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            this.getLogger().log(Level.INFO, "Creating {0}.", configFile.getAbsolutePath());
            this.saveResource("config.yml", false);
        }

        //get messages.yml and make if none existent
        File messagesFile = new File(this.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            this.getLogger().log(Level.INFO, "Creating {0}.", messagesFile.getAbsolutePath());
            this.saveResource("messages.yml", false);
        }

        //initialize configurations
        this.configuration = new Configuration(configFile);
        this.messages = new Messages(messagesFile);
        //initialize commands
        this.commands = new Commands();
        //initialize repositories
        if (this.playerRepository == null) {
            this.playerRepository = new PlayerRepository();
        } else {
            this.playerRepository.onReload();
        }
        if (this.serverRepository == null)
            this.serverRepository = new ServerRepository();
//        if (this.combatTagRepository == null)
//            this.combatTagRepository = new CombatTagRepository();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerKickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerKillListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Messages getMessages() {
        return messages;
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public ServerRepository getServerRepository() {
        return serverRepository;
    }

    public boolean isStopping() {
        return stopping;
    }

//    public CombatTagRepository getCombatTagRepository() {
//        return combatTagRepository;
//    }
}
