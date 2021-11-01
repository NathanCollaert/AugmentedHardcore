package com.backtobedrock.augmentedhardcore.groups;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Reads the configured ban-time meta attribute from
 * the player's LuckPerms group.
 *
 * @author Marcel Schoen
 */
public class LuckPermsGroupHandler implements GroupHandler {

    private LuckPerms luckPerms = null;
    private String attributeName = null;

    /**
     * @param attributeName The configured name of the LuckPerms group attribute.
     */
    public LuckPermsGroupHandler(String attributeName) {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            this.attributeName = attributeName;
        } else {
            Bukkit.getLogger().warning("LuckPerms group handler not available!");
        }
    }

    @Override
    public Object getAttribute(Player player) {
        if(luckPerms != null) {
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
            String groupName = user.getPrimaryGroup();
            Group group = groupName == null ? null : luckPerms.getGroupManager().getGroup(groupName);
            if(group != null) {
                String result = luckPerms.getGroupManager().getGroup(groupName).getCachedData().getMetaData().getMetaValue(attributeName);
                return result;
            }
        } else {
            Bukkit.getLogger().warning("LuckPerms API not available, cannot resolve attribute '"
                    + attributeName + "'!");
        }
        return "";
    }
}
