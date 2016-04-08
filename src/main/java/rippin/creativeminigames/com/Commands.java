package rippin.creativeminigames.com;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.bukkit.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class Commands implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        String unknownArguments = "Error: Illegal command.";
        if (command.getName().equalsIgnoreCase("mini")) {
            if (args.length == 0) {
                //help messages
                return true;
            }
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
                Plot plot = plotPlayer.getCurrentPlot();
                //Plot is null if player is not in a plot
                if (plot != null) {
                    if (plot.isAdded(player.getUniqueId()) || plot.isOwner(player.getUniqueId())) {
                        if (args[0].equalsIgnoreCase("create") && args.length == 2) {
                            if (!ArenaManager.isArena(plot, args[1])) {
                                //TODO: Possibly add an arena limit?
                                ArenaManager.createArena(player, args[1]);
                                player.sendMessage(ChatColor.GREEN + "Arena " + args[1] + " has been added.");
                            }
                            else {
                                player.sendMessage(ChatColor.RED + "That is already a Minigame Arena.");
                            }
                        }
                        else if(args[0].equalsIgnoreCase("list") && args.length == 1){
                            ArenaManager.listArenasInPlot(plot,commandSender);
                        }
                        else if (args[0].equalsIgnoreCase("loadArenas") && args.length == 1) {
                            ArenaManager.loadAllArenasFromPlot(plot);
                            player.sendMessage(ChatColor.GREEN + "Arenas have been loaded. Do /mini list to view them.");
                        }

                        else if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
                            ArenaManager.removeArena(player, args[1]);
                            player.sendMessage(ChatColor.GREEN + "Arena " + args[1] + " has been removed.");
                        }
                        else if (args[1].equalsIgnoreCase("setType") && args.length == 3) {
                            if (ArenaManager.isArena(plot, args[0])) {
                                Arena a = ArenaManager.getArena(args[0], plot);
                                if (a.setType(args[2]))
                                    player.sendMessage(ChatColor.GREEN + "Type: " + args[2] + " has been set for" +
                                            "Arena " + args[0] + ".");
                                else {
                                    player.sendMessage(ChatColor.RED + "Not a valid gametype."); //list types maybe?
                                }
                                }
                            else {
                                player.sendMessage(ChatColor.RED + " That is not a valid minigame name."); // list arenas?
                            }
                            }
                        else if (args[1].equalsIgnoreCase("setSpawn") && args.length == 2) {
                                if (ArenaManager.isArena(plot, args[0])) {
                                    Arena a = ArenaManager.getArena(args[0], plot);
                                    int index = 0; //default spawn index
                                    a.setSpawn(index, player.getLocation());
                                    if (a.setSpawn(index, player.getLocation()))
                                        player.sendMessage(ChatColor.GREEN + " Spawn: " + (index + 1) + " has been set for" +
                                                "Arena " + args[0] + ".");
                                }
                            }
                        else if (args[1].equalsIgnoreCase("setSpawn") && args.length == 3) {
                                if (ArenaManager.isArena(plot, args[0])) {
                                    Arena a = ArenaManager.getArena(args[0], plot);
                                    int index = Integer.valueOf(args[2]);
                                    if (a.setSpawn(index, player.getLocation()))
                                        player.sendMessage(ChatColor.GREEN + " Spawn: " + (index + 1) + " has been set for" +
                                                "Arena " + args[0] + ".");
                                }

                            }
                        else if (args[0].equalsIgnoreCase("start") && args.length == 2) {
                                if (ArenaManager.isArena(plot, args[1])) {
                                    Arena a = ArenaManager.getArena(args[1], plot);
                                    if (!ArenaManager.getAllEnabledArenas().containsKey(a.getStringID())) {
                                        a.start();
                                        ArenaManager.broadcastToPlot(plot, org.bukkit.ChatColor.GREEN
                                                + args[1] + " arena has been enabled by " + player.getDisplayName());
                                    }
                                    else {
                                        commandSender.sendMessage(ChatColor.RED + "There is already an anabled arena in this plot.");
                                    }
                                }
                            }
                        else if (args[0].equalsIgnoreCase("end") && args.length == 2) {
                                if (ArenaManager.isArena(plot, args[1])) {
                                    Arena a = ArenaManager.getArena(args[1], plot);
                                    if (ArenaManager.getAllEnabledArenas().containsKey(a.getStringID())) {
                                        ArenaManager.broadcastToPlot(plot, org.bukkit.ChatColor.GREEN
                                                + args[1] + " arena has been disabled by " + player.getDisplayName());
                                        a.end();
                                    }
                                    else {
                                        commandSender.sendMessage(ChatColor.RED + "This mini game is not enabled?");
                                    }
                                }
                            else {
                                    player.sendMessage(ChatColor.RED + " That is not a valid minigame name.");
                                }
                            }
                        }
                    }
                }
            else {
                commandSender.sendMessage(ChatColor.RED + unknownArguments);
            }
            return true;
        }
            return false;
    }
}
