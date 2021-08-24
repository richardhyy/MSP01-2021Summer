package cc.eumc.helloplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class UserListener implements Listener {

    // 玩家加入服务器时被调用
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 对玩家发送 Hello
        event.getPlayer().sendMessage("Hello");
        // 发送玩家的名字给这个玩家
        event.getPlayer().sendMessage("你是" + event.getPlayer().getName());
    }

    // 玩家发送消息时被调用
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // 你的代码：


    }

}
