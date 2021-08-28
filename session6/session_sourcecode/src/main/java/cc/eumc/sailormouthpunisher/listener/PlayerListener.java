package cc.eumc.sailormouthpunisher.listener;

import cc.eumc.sailormouthpunisher.SailorMouthPunisher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {
    private final SailorMouthPunisher plugin;
    private final List<String> badWords;
    private final Map<Player, Integer> playerBadWordCountMap = new HashMap<>();

    public PlayerListener(SailorMouthPunisher plugin) {
        this.plugin = plugin;

        this.badWords = plugin.getConfig().getStringList("BadWords");
    }


    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        for (String badWord : badWords) {
            // TODO: 大小写不敏感
            if (event.getMessage().contains(badWord)) {
                int count = playerBadWordCountMap.getOrDefault(player, 0) + 1;
                if (count >= 3) {
                    Bukkit.getServer().getScheduler()
                            .runTask(plugin,
                                    () -> player.getWorld().createExplosion(player.getLocation(), count, false, false));
                    sendMessage(player, String.format(plugin.getConfig().getString("Message.Punish", "&c你因为说脏话被轰炸了"), count));
                }
                playerBadWordCountMap.put(player, count);

                sendMessage(player, String.format(plugin.getConfig().getString("Message.Warn", "&e你说了第%d次脏话"), count));
                break;
            }
        }
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(message.replace("&", "§"));
    }
}
