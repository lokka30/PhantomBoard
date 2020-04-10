package io.github.lokka30.phantomboard.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Logger;

public class Utils {

    public String getRecommendedServerVersion() {
        return "1.15";
    }

    public int getRecommendedSettingsVersion() {
        return 1;
    }

    public int getRecommendedMessagesVersion() {
        return 1;
    }

    public int getRecommendedDataVersion() {
        return 1;
    }

    public String colorize(final String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void log(final LogLevel level, String msg) {
        msg = colorize("&7" + msg);
        final Logger logger = Bukkit.getLogger();

        switch(level) {
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
                throw new IllegalStateException("Unknown LogLevel " + level.toString() + ". Message: " + msg);
        }
    }
}
