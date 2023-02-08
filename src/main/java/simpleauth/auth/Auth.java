package simpleauth.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class Auth extends JavaPlugin implements Listener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, PlayerData> playerDataMap = new HashMap<>();
    private final Map<String, Boolean> loggedInPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        loadPlayerData();
    }

    @Override
    public void onDisable() {
        savePlayerData();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }
        Player player = (Player) sender;
        if (label.equalsIgnoreCase("register")) {
            if (args.length == 0) {
                player.sendMessage("Usage: /register <password>");
                return false;
            }
            String password = args[0];
            playerDataMap.put(player.getName(), new PlayerData(player.getName(), password));
            player.sendMessage("You have successfully registered.");
            savePlayerData();
        } else if (label.equalsIgnoreCase("login")) {
            if (args.length == 0) {
                player.sendMessage("Usage: /login <password>");
                return false;
            }
            String password = args[0];
            PlayerData playerData = playerDataMap.get(player.getName());
            if (playerData == null) {
                player.sendMessage("You need to register first.");
                return false;
            }
            if (!playerData.getPassword().equals(password)) {
                player.sendMessage("Incorrect password.");
                return false;
            }
            loggedInPlayers.put(player.getName(), true);
            player.sendMessage("You have successfully logged in.");
        }
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loggedInPlayers.put(player.getName(), false);
        player.sendMessage("Please register or login to move.");
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

    private void savePlayerData() {
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

    private static class PlayerData {
        private final String name;
        private final String password;

        public PlayerData(String name, String password) {
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
}