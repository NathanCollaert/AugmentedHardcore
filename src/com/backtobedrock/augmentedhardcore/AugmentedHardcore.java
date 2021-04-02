package com.backtobedrock.augmentedhardcore;

import com.backtobedrock.augmentedhardcore.commands.Commands;
import com.backtobedrock.augmentedhardcore.configs.Configurations;
import com.backtobedrock.augmentedhardcore.configs.Messages;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.eventListeners.*;
import com.backtobedrock.augmentedhardcore.repositories.PlayerRepository;
import com.backtobedrock.augmentedhardcore.repositories.ServerRepository;
import com.backtobedrock.augmentedhardcore.utils.Metrics;
import com.backtobedrock.augmentedhardcore.utils.PlaceholderUtils;
import com.backtobedrock.augmentedhardcore.utils.UpdateUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class AugmentedHardcore extends JavaPlugin implements Listener {

    //various
    private final Map<Class<?>, AbstractEventListener> activeEventListeners = new HashMap<>();
    private boolean stopping = false;
    //configurations
    private Commands commands;
    private Configurations configurations;
    private Messages messages;
    //repositories
    private PlayerRepository playerRepository = null;
    private ServerRepository serverRepository = null;

    @Override
    public void onEnable() {
        this.initialize();

        //bstats metrics
        Metrics metrics = new Metrics(this, 10843);

        //PAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderUtils().register();
        }

        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.stopping = true;

        if (this.getServerRepository() != null) {
            ServerData serverData = this.getServerRepository().getServerDataSync();
            if (serverData != null) {
                this.getServerRepository().updateServerData(serverData);
            }
        }

        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        return this.commands.onCommand(cs, cmnd, alias, args);
    }

    public void initialize() {
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
        if (this.configurations == null) {
            try {
                File copy = new File(this.getDataFolder(), "config.old.yml");
                if (copy.exists()) {
                    copy.delete();
                }
                Files.copy(configFile.toPath(), copy.toPath());
                UpdateUtils.update(this, "config.yml", configFile, Arrays.asList("LifePartsPerKill", "MaxHealthIncreasePerKill"));
                configFile = new File(this.getDataFolder(), "config.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.configurations = new Configurations(configFile);
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

        //register event listeners
        this.registerListeners();
    }

    private void registerListeners() {
        Arrays.asList(
                new InventoryClickListener(),
                new EntityDeathListener(),
                new PlayerDamageByEntityListener(),
                new PlayerDeathListener(),
                new PlayerJoinListener(),
                new PlayerKickListener(),
                new PlayerLoginListener(),
                new PlayerQuitListener(),
                new PlayerRegainHealthListener(),
                new PlayerRespawnListener()
        ).forEach(e ->
                {
                    if (this.activeEventListeners.containsKey(e.getClass())) {
                        AbstractEventListener listener = this.activeEventListeners.get(e.getClass());
                        if (!listener.isEnabled()) {
                            HandlerList.unregisterAll(listener);
                            this.activeEventListeners.remove(listener.getClass());
                        }
                    } else if (e.isEnabled()) {
                        getServer().getPluginManager().registerEvents(e, this);
                        this.activeEventListeners.put(e.getClass(), e);
                    }
                }
        );
    }

    public Configurations getConfigurations() {
        return configurations;
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
}
