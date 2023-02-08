package simpleauth.auth;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Auth extends JavaPlugin implements Listener {

    private final Map<String, Boolean> loggedInPlayers = new HashMap<>();
    private final Map<String, PlayerData> playerDataMap = new HashMap<>();
    private static final Gson GSON = new Gson();

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("register")).setExecutor(new RegisterCommand(this));
        Objects.requireNonNull(getCommand("login")).setExecutor(new LoginCommand(this));
        Objects.requireNonNull(getCommand("forcelogin")).setExecutor(new ForceLoginCommand(this));
        loadPlayerData();
    }

    @Override
    public void onDisable() {
        savePlayerData();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loggedInPlayers.put(player.getName(), false);
        player.sendMessage("Please register or login to move.");
        player.sendMessage("Use: /register|login nickname password");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!loggedInPlayers.containsKey(player.getName()) || !loggedInPlayers.get(player.getName())) {
            event.setCancelled(true);
        }
    }

    private void loadPlayerData() {
        File file = new File(getDataFolder(), "playerdata.json");
        if (!file.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            PlayerData[] playerDataArray = GSON.fromJson(reader, PlayerData[].class);
            for (PlayerData playerData : playerDataArray) {
                playerDataMap.put(playerData.getName(), playerData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void savePlayerData() {
        File file = new File(getDataFolder(), "playerdata.json");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            PlayerData[] playerDataArray = playerDataMap.values().toArray(new PlayerData[0]);
            GSON.toJson(playerDataArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class PlayerData {
        private final String name;
        private final String password;

        protected PlayerData(String name, String password) {
            this.name = name;
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public String getPassword() {
            return password;
        }
    }

    public Map<String, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

    public Map<String, Boolean> getLoggedInPlayers() {
        return loggedInPlayers;
    }
}