package com.backtobedrock.augmentedhardcore.domain.data;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerData {
    private final AugmentedHardcore plugin;

    //serializable
    private final Server server;
    private final Map<UUID, Pair<Integer, Ban>> ongoingBans;
    private int totalBans;

    public ServerData() {
        this(0, new HashMap<>());
    }

    public ServerData(int totalBans, Map<UUID, Pair<Integer, Ban>> ongoingBans) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.server = this.plugin.getServer();
        this.ongoingBans = ongoingBans;
        this.totalBans = totalBans;
    }

    public static ServerData deserialize(ConfigurationSection section) {
        Map<UUID, Pair<Integer, Ban>> cOngoingBans = new HashMap<>();
        int cTotalBans = section.getInt("TotalBans", 0);

        //get ongoing bans section
        ConfigurationSection ongoingBanSection = section.getConfigurationSection("OngoingBans");
        //if it exists, loop over all keys and deserialize bans
        if (ongoingBanSection != null) {
            ongoingBanSection.getKeys(false).forEach(e -> {
                UUID uuid = UUID.fromString(e);
                //get ban
                ConfigurationSection banSection = ongoingBanSection.getConfigurationSection(e);
                if (banSection != null) {
                    banSection.getKeys(false).forEach(a -> {
                        int id = Integer.parseInt(a);
                        ConfigurationSection banConfiguration = banSection.getConfigurationSection(a);
                        Ban ban = null;
                        if (banConfiguration != null) {
                            ban = Ban.Deserialize(banConfiguration);
                        }
                        //if exists, put in map
                        if (ban != null) {
                            cOngoingBans.put(uuid, new Pair<>(id, ban));
                        }
                    });
                }
            });
        }
        return new ServerData(cTotalBans, cOngoingBans);
    }

    public int getTotalBans() {
        return totalBans;
    }

    public int getTotalOngoingBans() {
        return ongoingBans.size();
    }

    public void addBan(UUID uuid, Pair<Integer, Ban> ban) {
        this.ongoingBans.put(uuid, ban);
        this.totalBans++;
        this.plugin.getServerRepository().updateServerData(this);
    }

    public void removeBan(OfflinePlayer player) {
        Pair<Integer, Ban> banPair = this.ongoingBans.remove(player.getUniqueId());
        if (banPair != null) {
            this.plugin.getServerRepository().removeBanFromServerData(player, banPair);
        }
    }

    public boolean isDeathBanned(OfflinePlayer player) {
        return this.ongoingBans.get(player.getUniqueId()) != null;
    }

    public Pair<Integer, Ban> getBan(OfflinePlayer player) {
        return this.ongoingBans.get(player.getUniqueId());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> cOngoingBans = new HashMap<>();
        this.ongoingBans.forEach((key, value) -> {
            Map<Integer, Object> cOngoingBansPlayer = new HashMap<>();
            cOngoingBansPlayer.put(value.getValue0(), value.getValue1().serialize());
            cOngoingBans.put(key.toString(), cOngoingBansPlayer);
        });
        map.put("OngoingBans", cOngoingBans);
        map.put("TotalBans", this.totalBans);

        return map;
    }

    public Map<UUID, Pair<Integer, Ban>> getOngoingBans() {
        return ongoingBans;
    }

    public Server getServer() {
        return server;
    }
}
