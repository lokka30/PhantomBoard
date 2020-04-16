package io.github.lokka30.phantomboard.utils;

import io.github.lokka30.phantomboard.PhantomBoard;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class Utils {

    private PhantomBoard instance;

    public Utils(final PhantomBoard instance) {
        this.instance = instance;
    }

    public String getRecommendedServerVersion() {
        return "1.15";
    }

    public int getRecommendedSettingsVersion() {
        return 4;
    }

    public int getRecommendedMessagesVersion() {
        return 2;
    }

    public int getRecommendedDataVersion() {
        return 1;
    }

    public String colorize(final String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @SuppressWarnings("unused")
    public String colorizeAndTranslate(final String msg, final Player player) {
        if (instance.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return colorize(msg);
        } else {
            return colorize(PlaceholderAPI.setPlaceholders(player, msg));
        }
    }

    public void log(final LogLevel level, String msg) {
        msg = colorize("&aPhantomBoard: &7" + msg);
        final Logger logger = Bukkit.getLogger();

        switch (level) {
            case INFO:
                logger.info(msg);
                break;
            case WARNING:
                logger.warning(msg);
                break;
            case SEVERE:
                logger.severe(msg);
                break;
            default:
                throw new IllegalStateException("LogLevel " + level.toString() + " has not been implemented properly! Supplied message: " + msg);
        }
    }
}
