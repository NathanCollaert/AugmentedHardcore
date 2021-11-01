package com.backtobedrock.augmentedhardcore.groups;

import org.bukkit.entity.Player;

/**
 * Handles processing group attributes of a player.
 *
 * @author Marcel Schoen
 */
public interface GroupHandler {

    /**
     * @param player The player for which to look up the group attribute value.
     * @return The attribute value (or null).
     */
    public Object getAttribute(Player player);
}
