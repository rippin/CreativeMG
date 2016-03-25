package rippin.creativeminigames.com;


import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class CreativeMGMain extends JavaPlugin {

    public Logger logger = Logger.getLogger("Minecraft");

    public void onEnable(){

        logger.info(" has been enabled");
    }

    public void onDisable(){
        logger.info(" has been enabled");

    }




}
