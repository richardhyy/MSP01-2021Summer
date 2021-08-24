package cc.eumc.scheduleflight;

import cc.eumc.scheduleflight.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class ScheduleFlight extends JavaPlugin {


    @Override
    public void onEnable() {
        // 注册监听器 🌟
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);


        /* 怎么实现「每秒钟检查并设置玩家飞行状态」？ 解除注释查看效果

        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {

                    boolean canFly = getMinute() % 2 == 0; // 56 % 2 == 0 , true
                    player.setAllowFlight(canFly);
                    player.setFlying(canFly);

                    player.sendMessage("Your fly status = " + canFly);

                }
            }
        }, 1, 20);
         */

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private int getMinute() {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }
}
