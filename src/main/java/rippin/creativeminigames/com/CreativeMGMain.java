package rippin.creativeminigames.com;


import org.bukkit.plugin.java.JavaPlugin;
import rippin.creativeminigames.com.Configs.Config;
import rippin.creativeminigames.com.Configs.PlotArenaConfig;

import java.util.logging.Logger;

public class CreativeMGMain extends JavaPlugin {

    public Logger logger = Logger.getLogger("Minecraft");
    public static CreativeMGMain plugin;
    public void onEnable(){
        plugin = this;
        Config.setUp(this);
        PlotArenaConfig.setUp(this);
        plugin.getCommand("mini").setExecutor(new Commands());
        plugin.getServer().getPluginManager().registerEvents(new EventsListener(this),this);
        logger.info(" has been enabled");
    }

    public void onDisable(){
        plugin = null;
        //disable all enabled arenas.
        ArenaManager.disableEmabledArenas();
        logger.info(" has been enabled");

    }




}
