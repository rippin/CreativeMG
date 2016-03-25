package rippin.creativeminigames.com;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class Commands implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        String unknownArguments = "Error: Illegal command.";
        if (command.getName().equalsIgnoreCase("mini")){
            if (args.length == 0){
                //help messages
                return true;
            }
            if (args[0].equalsIgnoreCase("create")){

            }
           else if (args[0].equalsIgnoreCase("remove")){

            }
            else if (args[0].equalsIgnoreCase("setType")){

            }
        }

        return false;
    }
}
