package net.custommoney.process;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.custommoney.CustomMoney;
import org.bukkit.entity.Player;

public class Placeholder extends PlaceholderExpansion {
    private final CustomMoney plugin;
    private final Eeconomy economy;
    public Placeholder(CustomMoney plugin) {
        this.plugin = plugin;
        this.economy = new Eeconomy(plugin);
    }

    @Override
    public String getIdentifier() {
        return "money";
    }

    @Override
    public String getAuthor() {
        return "Sladovatel";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {return "";}
        if (identifier.isEmpty()) {
            double nowBal = economy.getBalance(player);
            return String.valueOf(nowBal);
        }
        return null;
    }
}