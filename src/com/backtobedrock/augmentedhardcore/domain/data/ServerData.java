package com.backtobedrock.augmentedhardcore.domain.data;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerData {
    private final AugmentedHardcore plugin;

    //serializable
    private final Map<UUID, Ban> ongoingBans;
    private int totalBans;

    public ServerData() {
        this(new HashMap<>(), 0);
    }

    public ServerData(Map<UUID, Ban> ongoingBans, int totalBans) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.ongoingBans = ongoingBans;
        this.totalBans = totalBans;
    }

    public static ServerData deserialize(ConfigurationSection section) {
        Map<UUID, Ban> cOngoingBans = new HashMap<>();
        int cTotalBans = section.getInt("TotalBans", 0);

        //get ongoing bans section
        ConfigurationSection ongoingBanSection = section.getConfigurationSection("OngoingBans");
        //if it exists, loop over all keys and deserialize bans
        if (ongoingBanSection != null) {
            ongoingBanSection.getKeys(false).forEach(e -> {
                UUID uuid = UUID.fromString(e);
                //get ban
                ConfigurationSection banSection = ongoingBanSection.getConfigurationSection(e);
                Ban ban = null;
                if (banSection != null) {
                    ban = Ban.Deserialize(banSection);
                }
                //if exists, put in map
                if (ban != null) {
                    cOngoingBans.put(uuid, ban);
                }
            });
        }

        return new ServerData(cOngoingBans, cTotalBans);
    }

    public int getTotalBans() {
        return totalBans;
    }

    public int getTotalOngoingBans() {
        return ongoingBans.size();
    }

    public void addBan(Player player, Ban ban) {
        this.ongoingBans.put(player.getUniqueId(), ban);
        this.totalBans++;
        this.plugin.getServerRepository().updateServerData(this);
    }

    public void removeBan(OfflinePlayer player) {
        this.ongoingBans.remove(player.getUniqueId());
        this.plugin.getServerRepository().updateServerData(this);
    }

    public boolean isDeathBanned(OfflinePlayer player) {
        return this.ongoingBans.get(player.getUniqueId()) != null;
    }

    public Ban getBan(OfflinePlayer player) {
        return this.ongoingBans.get(player.getUniqueId());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> cOngoingBans = new HashMap<>();
        this.ongoingBans.forEach((key, value) -> cOngoingBans.put(key.toString(), value.serialize()));
        map.put("OngoingBans", cOngoingBans);
        map.put("TotalBans", this.totalBans);

        return map;
    }

    public Map<UUID, Ban> getOngoingBans() {
        return ongoingBans;
    }
}
