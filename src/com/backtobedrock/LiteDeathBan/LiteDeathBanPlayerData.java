package com.backtobedrock.LiteDeathBan;

import java.util.UUID;
import org.bukkit.Bukkit;

/**
 *
 * @author Nathan_C
 */
public class LiteDeathBanPlayerData {

    private UUID ID;
    private String playerName;
    private int lives;

    public LiteDeathBanPlayerData(UUID ID) {
        this(ID, 1);
    }

    public LiteDeathBanPlayerData(UUID ID, int lives) {
        this.ID = ID;
        this.playerName = Bukkit.getOfflinePlayer(ID).getName();
        this.setLives(lives);
    }

    public UUID getID() {
        return ID;
    }

    public void setID(UUID ID) {
        this.ID = ID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        if (lives < 0) {
            this.lives = 0;
        } else {
            this.lives = lives;
        }
    }

}
