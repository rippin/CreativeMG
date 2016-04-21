package rippin.creativeminigames.com;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by EF on 3/21/2016.
 */
public class Utils {

    public static String gameTypes(){
        String s = "";
        for (GameType type : GameType.values()){
            s += type.getString() + " | ";
        }
        return s;
    }

    public static String prefix = ChatColor.translateAlternateColorCodes('&', "&6[&4BCMini&6] ");
    public static void errorMessage(CommandSender sender, String err){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&4Error: " + "&7" +err));
    }
    public static  void infoMessage(CommandSender sender, String msg){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&6" + msg));
    }

}
