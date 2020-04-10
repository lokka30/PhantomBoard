package io.github.lokka30.phantomboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ScoreboardManager {

    private PhantomBoard instance;

    public ScoreboardManager(final PhantomBoard instance) {
        this.instance = instance;
    }

    //These players won't be shown the scoreboard.
    private ArrayList<UUID> hiddenPlayers;

    private ArrayList<Player> onlinePlayers;

    private String title;
    private List<String> lines;
    private int period;

    public void init() {
        onlinePlayers = new ArrayList<>();
        updateOnlinePlayers();

        hiddenPlayers = new ArrayList<>();
        updateHiddenPlayers();

        title = instance.getSettings().get("scoreboard.title", instance.getUtils().colorize("&a&nPhantomBoard"));
        lines = instance.getSettings().get("scoreboard.lines", Collections.singletonList(instance.getUtils().colorize("&7PhantomBoard by lokka30")));
        period = instance.getSettings().get("scoreboard.updatePeriod", 5);

        scheduleTask();
    }

    public void updateOnlinePlayers() {
        onlinePlayers.clear();

        for(Player player : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(player);
        }
    }

    public void updateHiddenPlayers() {
        for(Player player : onlinePlayers) {
            final UUID uuid = player.getUniqueId();

            if(instance.getData().get("players." + uuid.toString() + ".hidden", false)) {
                hiddenPlayers.add(uuid);
            }
        }
    }

    public void toggleHidden(final UUID uuid) {
        if(hiddenPlayers.contains(uuid)) {
            //The player has hidden the scoreboard. Show the scoreboard.
            hiddenPlayers.remove(uuid);
        } else {
            //The player has shown the scoreboard. Hide the scoreboard.
            hiddenPlayers.add(uuid);
        }
    }

    private void scheduleTask() {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {

                //Before starting, check if there are no players on the server.
                if(!Bukkit.getOnlinePlayers().isEmpty()) {
                    Player player;

                    if(index > Bukkit.getOnlinePlayers().size()) {
                        index = 0;
                    }

                    player = onlinePlayers.get(index);

                    if(hiddenPlayers.contains(player.getUniqueId())) {
                       index++;
                    } else {
                        //TODO
                    }
                }
            }
        }.runTaskTimer(instance, 0L, period);
    }
}
