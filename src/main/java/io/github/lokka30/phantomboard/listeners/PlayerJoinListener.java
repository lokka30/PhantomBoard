package io.github.lokka30.phantomboard.listeners;

import io.github.lokka30.phantomboard.PhantomBoard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private PhantomBoard instance;

    public PlayerJoinListener(final PhantomBoard instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        instance.getScoreboardManager().updateOnlineAndHiddenPlayers();
    }
}
