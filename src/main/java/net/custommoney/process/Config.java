package net.custommoney.process;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        config.addDefault("on_give_money", false);
        config.addDefault("give_sum", 100);
        config.addDefault("delay", 20);
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public int getArgument(String select) {return config.getInt(select);}
}
