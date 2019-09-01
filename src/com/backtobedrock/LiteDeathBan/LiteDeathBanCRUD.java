package com.backtobedrock.LiteDeathBan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 *
 * @author Nathan_C
 */
public class LiteDeathBanCRUD {

    private final TreeMap<UUID, LiteDeathBanPlayerData> playerData = new TreeMap<>();
    private static LiteDeathBanCRUD instance;
    private static final Logger log = Bukkit.getLogger();

    private void writeAllPlayerDataToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter file = new FileWriter(System.getProperty("user.dir") + "/plugins/LiteDeathban/PlayerData.json")) {
            file.write(gson.toJson(this.playerData.values()));
            file.flush();
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    public void readAllPlayerDataFromFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<LiteDeathBanPlayerData> myData;

        try (FileReader reader = new FileReader(System.getProperty("user.dir") + "/plugins/LiteDeathBan/PlayerData.json")) {
            //Read JSON file
            java.lang.reflect.Type myDataType = new com.google.gson.reflect.TypeToken<Collection<LiteDeathBanPlayerData>>() {
            }.getType();
            myData = gson.fromJson(reader, myDataType);
            if (myData != null) {
                myData.stream().forEach(e -> {
                    this.playerData.put(e.getID(), e);
                });
            }
        } catch (FileNotFoundException ex) {
            log.warning(ex.getMessage());
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    public void addPlayerData(LiteDeathBanPlayerData p) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(System.getProperty("user.dir") + "/plugins/LiteDeathBan/PlayerData.json", "rw")) {

            long pos = randomAccessFile.length();
            while (randomAccessFile.length() > 0) {
                pos--;
                randomAccessFile.seek(pos);
                if (randomAccessFile.readByte() == ']') {
                    randomAccessFile.seek(pos);
                    break;
                }
            }

            String jsonElement = gson.toJson(p);
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
            this.playerData.put(p.getID(), p);
        } catch (FileNotFoundException e) {
            log.warning(e.getMessage());
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    public void updatePlayerData(LiteDeathBanPlayerData p) {
        if (this.playerData.replace(p.getID(), p) != null) {
            this.writeAllPlayerDataToFile();
        }
    }

    public void deletePlayerData(UUID id) {
        if (this.playerData.get(id) != null) {
            this.playerData.remove(id);
            this.writeAllPlayerDataToFile();
        }
    }

    public LiteDeathBanPlayerData getPlayerDataWithID(UUID id) {
        return this.playerData.get(id);
    }

    public static LiteDeathBanCRUD getInstance() {
        if (instance == null) {
            instance = new LiteDeathBanCRUD();
        }
        return instance;
    }
}
