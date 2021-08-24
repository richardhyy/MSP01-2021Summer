package cc.eumc.scheduleflight.listener;

import cc.eumc.scheduleflight.util.PlayerExpUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 偶数: 启用飞行;  奇数: 禁用飞行
        boolean canFly = getMinute() % 2 == 0;              // e.g. 56 % 2 == 0 , true
        player.setAllowFlight(canFly);                      // 为了防止 `java.lang.IllegalArgumentException: Cannot make player fly if getAllowFlight() is false`
        player.setFlying(canFly);
        player.sendMessage("Your fly status = " + canFly);  // 告诉玩家

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // TODO: 你的代码：
        Player player = event.getPlayer();

        // 示例：获取玩家当前总经验数。
        // ⚠️ 注意！这不同于 player.getTotalExperience()
        int currentTotalExp = PlayerExpUtil.getPlayerExp(player);
        // 更多可用方法见 util.PlayerExpUtil

    }



    // Util methods  ｜  实用方法

    private int getMinute() {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);  // 0-59
    }

    private int getHourOfDay() {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY); // 0-23
    }

    private int getDayOfWeek() {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK); // 1-7, Fri = 6
    }
}
