package com.backtobedrock.LiteDeathBan.helperClasses;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeathLogData {

    private final transient LiteDeathBan plugin;

    private final String PlayerName;
    private final String PlayerID;
    private final PlayerLocation DeathLocation;
    private final String DeathDate;
    private final int LivesLeft;

    public DeathLogData(LiteDeathBan plugin, Player plyr, String deathDate, int livesLeft) {
        this.plugin = plugin;
        this.PlayerName = plyr.getName();
        this.PlayerID = plyr.getUniqueId().toString();
        this.DeathLocation = new PlayerLocation(plyr);
        this.DeathDate = deathDate;
        this.LivesLeft = livesLeft;
        this.addToFile();
    }

    private void addToFile() {
        File file = new File(this.plugin.getDataFolder() + "/logs/DeathLog.json");
        if (!file.exists()) {
            try {
                if (file.getParentFile().mkdirs() && file.createNewFile()) {
                    Bukkit.getLogger().log(Level.INFO, "[LiteDeathBan] DeathLog file has been created");
                }
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "[LiteDeathBan] Cannot create DeathLog file.");
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
            long pos = randomAccessFile.length();
            while (randomAccessFile.length() > 0) {
                pos--;
                randomAccessFile.seek(pos);
                if (randomAccessFile.readByte() == ']') {
                    randomAccessFile.seek(pos);
                    break;
                }
            }
            String jsonElement = gson.toJson(this);
            switch ((int) pos) {
                case 0:
                    randomAccessFile.writeBytes("[" + jsonElement + "]");
                    break;
                case 1:
                    randomAccessFile.writeBytes(jsonElement + "]");
                    break;
                default:
                    randomAccessFile.writeBytes("," + jsonElement + "]");
                    break;
            }
        } catch (FileNotFoundException e) {
            System.out.println("[LiteDeathBan] Cannot find DeathLog file.");
        } catch (IOException e) {
            System.out.println("[LiteDeathBan] Cannot write to DeathLog file.");
        }
    }
}
