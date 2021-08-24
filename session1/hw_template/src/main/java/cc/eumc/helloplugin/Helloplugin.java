package cc.eumc.helloplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Helloplugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // 这个方法在插件启动时被调用

        // 在后台显示「我被启用了」
        getLogger().info("我被启用了");

        // 注册监听器
        // UserListener 是我们创建的类
        getServer().getPluginManager().registerEvents(new UserListener(), this);
    }


    @Override
    public void onDisable() {
        getLogger().info("我被禁用了");
    }
}
