package com.backtobedrock.LiteDeathBan.helperClasses;

import org.bukkit.entity.Player;

public class PlayerLocation {

    private final double x;
    private final double y;
    private final double z;

    public PlayerLocation(Player plyr) {
        this.x = plyr.getLocation().getX();
        this.y = plyr.getLocation().getY();
        this.z = plyr.getLocation().getZ();
    }
}
