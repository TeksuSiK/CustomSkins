package pl.teksusik.customskins.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.teksusik.customskins.CustomSkinsPlugin;

public class PlayerJoinListener implements Listener {
    private final CustomSkinsPlugin plugin;

    public PlayerJoinListener(CustomSkinsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        //TEMPORARY FIX - TODO
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (this.plugin.getSkinService().getSkins(player).size() == 0)
                this.plugin.getSkinService().loadSkins(player);
        });
    }
}
