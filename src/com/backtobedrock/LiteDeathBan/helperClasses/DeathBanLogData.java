package com.backtobedrock.LiteDeathBan.helperClasses;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeathBanLogData {

    private final transient LiteDeathBan plugin;
    private final transient Logger log = Bukkit.getLogger();
    private final String PlayerName;
    private final String PlayerID;
    private final String DeathLocation;
    private final String BanDate;
    private final String BanExpiryDate;
    private final int BanTimeInMinutes;

    public DeathBanLogData(LiteDeathBan plugin, Player plyr, String banDate, String banExpiryDate, int banTime) {
        this.plugin = plugin;
        this.PlayerName = plyr.getName();
        this.PlayerID = plyr.getUniqueId().toString();
        this.DeathLocation = String.format("x: %.2f, y: %.2f, z: %.2f", plyr.getLocation().getX(), plyr.getLocation().getY(), plyr.getLocation().getZ());
        this.BanDate = banDate;
        this.BanExpiryDate = banExpiryDate;
        this.BanTimeInMinutes = banTime;
        this.addToFile();
    }

    private void addToFile() {
        File file = new File(this.plugin.getDataFolder() + "/logs/DeathBanLog.json");
        if (!file.exists()) {
            try {
                if (file.getParentFile().mkdirs() && file.createNewFile()) {
                    this.log.log(Level.INFO, "[LiteDeathBan] DeathBanLog file has been created");
                }
            } catch (IOException e) {
                this.log.log(Level.SEVERE, "[LiteDeathBan] Cannot create DeathBanLog file.");
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
            System.out.println("[LiteDeathBan] Cannot find DeathBanLog file.");
        } catch (IOException e) {
            System.out.println("[LiteDeathBan] Cannot write to DeathBanLog file.");
        }
    }
}
