package cc.eumc.sailormouthpunisher;

import cc.eumc.sailormouthpunisher.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SailorMouthPunisher extends JavaPlugin {
    private SailorMouthPunisher plugin;

    public SailorMouthPunisher getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        this.plugin = this;

        saveDefaultConfig();
        reloadConfig();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
