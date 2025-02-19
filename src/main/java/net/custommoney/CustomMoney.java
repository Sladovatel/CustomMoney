package net.custommoney;

import net.custommoney.cmd.Money;
import net.custommoney.process.Config;
import net.custommoney.process.Eeconomy;
import net.custommoney.process.Placeholder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.*;

public final class CustomMoney extends JavaPlugin {
    public static Map<UUID, Double> playerBalances;
    private static CustomMoney inst;
    private static File dataFile;
    private Config config;
    private Eeconomy economy;

    @Override
    public void onEnable() {
        inst = this;
        getLogger().info(ChatColor.DARK_GREEN + "CustomMoney started its work successfully!");
        playerBalances = new HashMap<>();
        dataFile = new File(getDataFolder(), "player.db");

        config = new Config(this);
        config.loadConfig();

        if (!getDataFolder().exists()) {getDataFolder().mkdirs();}
        if (dataFile.exists()) {
            loadData();
        } else {
            saveData();
        }
        this.getCommand("money").setExecutor(new Money(this));

        if (!setupEconomy()) {getServer().getPluginManager().disablePlugin(this);return;}
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {new Placeholder(this).register();}
        economy = new Eeconomy(this);
        if (config.getArgument("on_give_money")==1){startMoneyTimer();}
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.DARK_RED + "CustomMoney finished my work!");
        saveData();
    }

    private boolean setupEconomy() {
        Economy customEconomy = new Eeconomy(this);
        getServer().getServicesManager().register(Economy.class, customEconomy, this, ServicePriority.Normal);
        return true;
    }

    //DataBase file handler | Обработчик файла с данными

    public static void saveData() {
        try {
            if (dataFile == null) {
                CustomMoney.getInst().getLogger().warning("The player.db file is not initialized!");
                return;
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
                oos.writeObject(playerBalances);
            }
        } catch (IOException e) {
            CustomMoney.getInst().getLogger().severe("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            if (dataFile == null) {
                getLogger().warning("The player.db file is not initialized!");
                return;
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
                playerBalances = (Map<UUID, Double>) ois.readObject();
                getLogger().info("Data loaded successfully from player.db!");
            }
        } catch (IOException | ClassNotFoundException e) {
            getLogger().severe("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //More operations | Другие дейтсвия

    public double getBalance(UUID uuid) {
        return playerBalances.getOrDefault(uuid, 0.0);
    }
    public static CustomMoney getInst() {
        return inst;
    }

    //Coins are gived every 5 minutes | Выдача монет каждые 5 минут

    private void startMoneyTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String permissionPrefix = "money.";
                    double multiplier = 1.0;
                    for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
                        String permission = perm.getPermission();
                        if (permission.startsWith(permissionPrefix)) {
                            try {
                                String multiplierStr = permission.substring(permissionPrefix.length());
                                multiplier = Double.parseDouble(multiplierStr);
                            } catch (NumberFormatException e) {
                                multiplier = 1.0;
                            }
                            break;
                        }
                    }
                    double amount = config.getArgument("give_sum") * multiplier;
                    economy.depositPlayer(player, amount);
                }
            }
        }.runTaskTimer(this, 0L, config.getArgument("delay"));
    }
}
