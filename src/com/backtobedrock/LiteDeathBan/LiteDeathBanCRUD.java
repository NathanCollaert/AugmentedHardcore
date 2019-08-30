package com.backtobedrock.LiteDeathBan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    private final TreeMap<UUID, LiteDeathBanPlayer> playerData = new TreeMap<>();
    private static LiteDeathBanCRUD instance;
    private Logger log = Bukkit.getLogger();

    public void writeAllPlayerDataToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter file = new FileWriter(System.getProperty("user.dir") + "/plugins/LiteDeathbans/PlayerData.json")) {
            file.write(gson.toJson(this.playerData.values()));
            file.flush();
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    public void readAllPlayerDataFromFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<LiteDeathBanPlayer> myData;

        try (FileReader reader = new FileReader(System.getProperty("user.dir") + "/plugins/LiteDeathBan/PlayerData.json")) {
            //Read JSON file
            java.lang.reflect.Type myDataType = new com.google.gson.reflect.TypeToken<Collection<LiteDeathBanPlayer>>() {
            }.getType();
            myData = gson.fromJson(reader, myDataType);
            if (myData != null) {
                myData.stream().forEach(e -> {
//                    this.playerData.put(e.getMarkerID(), e);
                });
            }
        } catch (FileNotFoundException ex) {
            log.warning(ex.getMessage());
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    public static LiteDeathBanCRUD getInstance() {
        if (instance == null) {
            instance = new LiteDeathBanCRUD();
        }
        return instance;
    }
}
