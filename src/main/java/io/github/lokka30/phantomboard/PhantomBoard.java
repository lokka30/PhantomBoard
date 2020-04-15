package io.github.lokka30.phantomboard;

import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.FlatFile;
import io.github.lokka30.phantomboard.commands.PhantomBoardCommand;
import io.github.lokka30.phantomboard.listeners.PlayerJoinListener;
import io.github.lokka30.phantomboard.listeners.PlayerQuitListener;
import io.github.lokka30.phantomboard.utils.LogLevel;
import io.github.lokka30.phantomboard.utils.UpdateChecker;
import io.github.lokka30.phantomboard.utils.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class PhantomBoard extends JavaPlugin {

    private Utils utils;
    private ScoreboardManager scoreboardManager;

    private FlatFile settings;
    private FlatFile messages;
    private FlatFile data;

    private PluginManager pluginManager;

    @Override
    public void onLoad() {
        utils = new Utils(this);
        scoreboardManager = new ScoreboardManager(this);
        pluginManager = getServer().getPluginManager();
    }

    @Override
    public void onEnable() {
        utils.log(LogLevel.INFO, "----+---- ENABLING BEGAN ----+----");

        utils.log(LogLevel.INFO, "Starting compatibility check...");
        if(checkCompatibility()) {
            utils.log(LogLevel.INFO, "Loading files...");
            loadFiles();

            utils.log(LogLevel.INFO, "Registering events...");
            registerEvents();

            utils.log(LogLevel.INFO, "Registering commands...");
            registerCommands();

            utils.log(LogLevel.INFO, "Starting bStats metrics...");
            new Metrics(this, 7072);

            utils.log(LogLevel.INFO, "Starting scoreboard task...");
            scoreboardManager.load();

            utils.log(LogLevel.INFO, "----+---- ENABLING COMPLETE ----+----");

            checkUpdates();
        } else {
            pluginManager.disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        utils.log(LogLevel.INFO, "----+---- DISABLING BEGAN ----+----");

        utils.log(LogLevel.INFO, "----+---- DISABLING COMPLETE ----+----");
    }

    private boolean checkCompatibility() {
        // --- Check if the server version is compatible. ---
        // Note: This doesn't stop the loading of the plugin. It just informs the server owners that they won't get support.
        final String currentVersion = getServer().getVersion();
        final String recommendedVersion = utils.getRecommendedServerVersion();

        if (currentVersion.contains(recommendedVersion)) {
            utils.log(LogLevel.INFO, "Server is running supported version &a" + currentVersion + "&7.");
        } else {
            utils.log(LogLevel.WARNING, "Possible incompatibility found: &cServer is not running " + recommendedVersion + "!");
            utils.log(LogLevel.WARNING, "Your server version doesn't match with the recommended version above.");
            utils.log(LogLevel.WARNING, "Support will not be provided from the author if encounter issues if you don't run the recommended server version.");
        }

        // --- Check if the server has PAPI loaded. ---
        // Note: If PAPI isn't found, the plugin will stop loading.
        if (pluginManager.getPlugin("PlaceholderAPI") == null) {
            utils.log(LogLevel.INFO, "&aPlaceholderAPI&7 is not installed, so placeholders will not be translated.");
        } else {
            utils.log(LogLevel.INFO, "&aPlaceholderAPI&7 is installed, placeholder support enabled.");
        }

        // --- No incompatibilities found, continue loading. ---
        return true;
    }

    public void loadFiles() {
        //Tell LightningStorage to start its business.
        final String path = "plugins/PhantomBoard/";
        settings = LightningBuilder
                .fromFile(new File(path + "settings"))
                .addInputStreamFromResource("settings.yml")
                .createYaml();
        messages = LightningBuilder
                .fromFile(new File(path + "messages"))
                .addInputStreamFromResource("messages.yml")
                .createYaml();
        data = LightningBuilder
                .fromFile(new File(path + "data"))
                .addInputStreamFromResource("data.json")
                .createJson();

        //Check if they exist
        final File settingsFile = new File(path + "settings.yml");
        final File messagesFile = new File(path + "messages.yml");
        final File dataFile = new File(path + "data.json");

        if (!(settingsFile.exists() && !settingsFile.isDirectory())) {
            utils.log(LogLevel.INFO, "File &asettings.yml&7 doesn't exist. Creating it now.");
            saveResource("settings.yml", false);
        }

        if (!(messagesFile.exists() && !messagesFile.isDirectory())) {
            utils.log(LogLevel.INFO, "File &amessages.yml&7 doesn't exist. Creating it now.");
            saveResource("messages.yml", false);
        }

        if (!(dataFile.exists() && !dataFile.isDirectory())) {
            utils.log(LogLevel.INFO, "File &adata.json&7 doesn't exist. Creating it now.");
            saveResource("data.json", false);
        }

        //Check their versions
        if (settings.get("file-version", 0) != utils.getRecommendedSettingsVersion()) {
            utils.log(LogLevel.SEVERE, "File &asettings.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }

        if (messages.get("file-version", 0) != utils.getRecommendedMessagesVersion()) {
            utils.log(LogLevel.SEVERE, "File &amessages.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }

        if (data.get("file-version", 0) != utils.getRecommendedDataVersion()) {
            utils.log(LogLevel.SEVERE, "File &adata.yml&7 is out of date! Errors are likely to occur! Reset it or merge the old values to the new file.");
        }
    }

    public void registerEvents() {
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerQuitListener(this), this);
    }

    public void registerCommands() {
        Objects.requireNonNull(getCommand("phantomboard")).setExecutor(new PhantomBoardCommand(this));
    }

    public void checkUpdates() {
        //TODO remove this when the update checker is properly implemented.
        if (!getSettings().get("update-checker-bypass", false)) {
            return;
        }

        if (getSettings().get("use-update-checker", true)) {
            utils.log(LogLevel.INFO, "&aUpdate Checker &8- &7Starting update check...");

            //TODO change resource ID once uploaded to spigot
            new UpdateChecker(this, 12345).getVersion(version -> {
                if (getDescription().getVersion().equalsIgnoreCase(version)) {
                    utils.log(LogLevel.INFO, "&aUpdate Checker &8- &7You're using the latest version.");
                } else {
                    utils.log(LogLevel.WARNING, "&aUpdate Checker &8- &7There's a new update available. Head to the SpigotMC resource page to download it.");
                }
            });
        }
    }

    public FlatFile getSettings() {
        return settings;
    }

    public FlatFile getMessages() {
        return messages;
    }

    public FlatFile getData() {
        return data;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public Utils getUtils() {
        return utils;
    }
}
