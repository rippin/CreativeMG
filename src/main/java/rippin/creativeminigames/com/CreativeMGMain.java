package rippin.creativeminigames.com;


import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class CreativeMGMain extends JavaPlugin {

    public Logger logger = Logger.getLogger("Minecraft");
    public static CreativeMGMain plugin;
    public void onEnable(){
        plugin = this;
        plugin.getCommand("mini").setExecutor(new Commands());
        plugin.getServer().getPluginManager().registerEvents(new EventsListener(this),this);
        logger.info(" has been enabled");
    }

    public void onDisable(){
        plugin = null;
        logger.info(" has been enabled");

    }




}
