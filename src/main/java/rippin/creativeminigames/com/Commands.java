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
                        if (args[0].equalsIgnoreCase("create")) {
                            if (!ArenaManager.isArena(plot, args[0])) {
                                //TODO: Possibly add an arena limit?
                                ArenaManager.createArena(player, args[0]);
                                player.sendMessage(ChatColor.GREEN + "Arena " + args[0] + " has been added.");
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            ArenaManager.removeArena(player, args[0]);
                            player.sendMessage(ChatColor.GREEN + "Arena " + args[0] + " has been removed.");
                        } else if (args[1].equalsIgnoreCase("setType") && args.length == 3) {
                            if (ArenaManager.isArena(plot, args[0])) {
                                Arena a = new Arena(args[1], plot);
                                if (a.setType(args[2]))
                                    player.sendMessage(ChatColor.GREEN + " Type: " + args[2] + " has been set for" +
                                            "Arena " + args[1] + ".");
                                }
                            } else if (args[1].equalsIgnoreCase("setSpawn") && args.length == 2) {
                                if (ArenaManager.isArena(plot, args[0])) {
                                    Arena a = new Arena(args[1], plot);
                                    int index = 0; //default spawn index
                                    a.setSpawn(index, player.getLocation());
                                    if (a.setSpawn(index, player.getLocation()))
                                        player.sendMessage(ChatColor.GREEN + " Spawn: " + (index + 1) + " has been set for" +
                                                "Arena " + args[1] + ".");
                                }
                            } else if (args[1].equalsIgnoreCase("setSpawn") && args.length == 3) {
                                if (ArenaManager.isArena(plot, args[0])) {
                                    Arena a = new Arena(args[1], plot);
                                    int index = Integer.valueOf(args[2]);
                                    if (a.setSpawn(index, player.getLocation()))
                                        player.sendMessage(ChatColor.GREEN + " Spawn: " + (index + 1) + " has been set for" +
                                                "Arena " + args[1] + ".");
                                }

                            } else if (args[0].equalsIgnoreCase("start") && args.length == 2) {
                                if (ArenaManager.isArena(plot, args[0])) {
                                    Arena a = new Arena(args[1], plot);
                                    a.start();
                                    ArenaManager.broadcastToPlot(plot, org.bukkit.ChatColor.GREEN
                                            + args[1] + " arena has been enabled by " + player.getDisplayName());
                                }
                            } else if (args[0].equalsIgnoreCase("end") && args.length == 2) {
                                if (ArenaManager.isArena(plot, args[0])) {
                                    Arena a = ArenaManager.getArena(args[0], plotPlayer);
                                    a.end();
                                    ArenaManager.broadcastToPlot(plot, org.bukkit.ChatColor.GREEN
                                            + args[1] + " arena has been disabled by " + player.getDisplayName());
                                }
                            }
                        }
                    }
                }
            } else {
            commandSender.sendMessage(ChatColor.RED + unknownArguments);
        }
            return false;
    }
}
