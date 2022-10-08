package com.backtobedrock.augmentedhardcore.groups;

import org.bukkit.entity.Player;

/**
 * Dummy group handler is used if LuckPerms is not
 * available, to avoid hard class dependencies.
 *
 * @author Marcel Schoen
 */
public class DummyGroupHandler implements GroupHandler {

    @Override
    public Object getAttribute(Player player) {
        return "";
    }
}
