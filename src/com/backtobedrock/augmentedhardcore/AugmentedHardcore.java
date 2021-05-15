package com.backtobedrock.augmentedhardcore;

import com.backtobedrock.augmentedhardcore.commands.Commands;
import com.backtobedrock.augmentedhardcore.configs.Configurations;
import com.backtobedrock.augmentedhardcore.configs.Messages;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.StorageType;
import com.backtobedrock.augmentedhardcore.eventListeners.*;
import com.backtobedrock.augmentedhardcore.eventListeners.dependencies.ListenerCombatLogX;
import com.backtobedrock.augmentedhardcore.guis.AbstractGui;
import com.backtobedrock.augmentedhardcore.guis.GuiMyStats;
import com.backtobedrock.augmentedhardcore.repositories.PlayerRepository;
import com.backtobedrock.augmentedhardcore.repositories.ServerRepository;
import com.backtobedrock.augmentedhardcore.runnables.UpdateChecker;
import com.backtobedrock.augmentedhardcore.utilities.Metrics;
import com.backtobedrock.augmentedhardcore.utilities.UpdateUtils;
import com.backtobedrock.augmentedhardcore.utilities.placeholderAPI.PlaceholdersAugmentedHardcore;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class AugmentedHardcore extends JavaPlugin implements Listener {

    //various
    private final Map<Class<?>, AbstractEventListener> activeEventListeners = new HashMap<>();
    private final Map<UUID, AbstractGui> openGuis = new HashMap<>();
    private boolean stopping = false;

    //configurations
    private Commands commands;
    private Configurations configurations;
    private Messages messages;

    //repositories
    private PlayerRepository playerRepository;
    private ServerRepository serverRepository;

    //runnables
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        this.initialize();

        //unban patch
        BanList banListName = Bukkit.getBanList(BanList.Type.NAME), banListIP = Bukkit.getBanList(BanList.Type.IP);
        banListName.getBanEntries().stream().filter(e -> e.getSource().equals(this.getDescription().getName())).forEach(e -> banListName.pardon(e.getTarget()));
        banListIP.getBanEntries().stream().filter(e -> e.getSource().equals(this.getDescription().getName())).forEach(e -> banListIP.pardon(e.getTarget()));

        //update checker
        this.updateChecker = new UpdateChecker();
        this.updateChecker.start();

        //bstats metrics
        Metrics metrics = new Metrics(this, 10843);
        metrics.addCustomChart(new Metrics.SingleLineChart("currently_ongoing_death_bans", () -> this.serverRepository.getServerDataSync().getTotalOngoingBans()));
        metrics.addCustomChart(new Metrics.SingleLineChart("total_death_bans", () -> this.serverRepository.getServerDataSync().getTotalDeathBans()));

        //PAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholdersAugmentedHardcore().register();
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
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmnd, @NotNull String alias, String[] args) {
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
        try {
            File copy = new File(this.getDataFolder(), "config.old.yml");
            if (copy.exists()) {
                //noinspection ResultOfMethodCallIgnored
                copy.delete();
            }
            Files.copy(configFile.toPath(), copy.toPath());
            UpdateUtils.update(this, "config.yml", configFile, Arrays.asList("LifePartsPerKill", "MaxHealthIncreasePerKill"));
            configFile = new File(this.getDataFolder(), "config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //initialize config and messages
        this.configurations = new Configurations(configFile);
        this.messages = new Messages(messagesFile);

        //initialize commands
        this.commands = new Commands();

        //initialize database if needed
        this.initDB();

        //initialize repositories
        if (this.playerRepository == null) {
            this.playerRepository = new PlayerRepository();
        } else {
            this.playerRepository.onReload();
        }
        if (this.serverRepository == null) {
            this.serverRepository = new ServerRepository();
        }

        //register event listeners
        this.registerListeners();
    }

    private void initDB() {
        if (this.getConfigurations().getDataConfiguration().getStorageType() != StorageType.MYSQL) {
            return;
        }

        String setup = "";
        try (InputStream in = getClassLoader().getResourceAsStream("dbsetup.sql")) {
            setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining());
        } catch (IOException | NullPointerException e) {
            getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
            e.printStackTrace();
        }
        String[] queries = setup.split(";");
        for (String query : queries) {
            if (query.isEmpty()) {
                return;
            }
            try (Connection conn = this.getConfigurations().getDataConfiguration().getDatabase().getDataSource().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        getLogger().info("Setup complete.");
    }

    private void registerListeners() {
        Arrays.asList(
                //dependencies
                new ListenerCombatLogX(),
                //internal
                new ListenerCustomInventory(),
                new ListenerEntityDeath(),
                new ListenerPlayerDamageByEntity(),
                new ListenerPlayerDeath(),
                new ListenerPlayerGameModeChange(),
                new ListenerPlayerJoin(),
                new ListenerPlayerKick(),
                new ListenerPlayerLogin(),
                new ListenerPlayerQuit(),
                new ListenerPlayerRegainHealth(),
                new ListenerPlayerRespawn()
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

    public void addToGuis(Player player, AbstractGui gui) {
        this.openGuis.put(player.getUniqueId(), gui);
    }

    public void removeFromGuis(Player player) {
        AbstractGui gui = this.openGuis.remove(player.getUniqueId());
        if (gui == null) {
            return;
        }

        if (gui instanceof GuiMyStats) {
            ((GuiMyStats) gui).getPlayerData().unregisterObserver(player);
        }
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
}
