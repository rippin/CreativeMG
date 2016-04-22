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
                commandList(commandSender);
                return true;
            }
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;

                if (player.hasPermission("BCMinigames.user")) {
                    PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
                Plot plot = plotPlayer.getCurrentPlot();
                //Plot is null if player is not in a plot
                if (plot != null) {
                    if (plot.isAdded(player.getUniqueId()) || plot.isOwner(player.getUniqueId())) {
                        if (args[0].equalsIgnoreCase("create") && args.length == 2) {
                            if (!ArenaManager.isArena(plot, args[1])) {
                                //TODO: Possibly add an arena limit?
                                ArenaManager.createArena(player, args[1]);
                                Utils.infoMessage(commandSender, "Arena " + args[1] + " has been added.");
                            } else {
                                Utils.errorMessage(commandSender, "That is already a Minigame Arena.");
                            }
                            return true;
                        }
                        else if (args[0].equalsIgnoreCase("list") && args.length == 1) {
                            ArenaManager.listArenasInPlot(plot, commandSender);
                            return true;
                        }
                        else if ((args[0].equalsIgnoreCase("loadArenas")|| args[0].equalsIgnoreCase("load")) && args.length == 1) {
                            ArenaManager.loadAllArenasFromPlot(plot);
                            Utils.infoMessage(commandSender, "Arenas have been loaded. Do /mini list to view them.");
                            return true;
                        }
                        else if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
                            ArenaManager.removeArena(player, args[1]);
                            Utils.infoMessage(commandSender, "Arena " + args[1] + " has been removed.");
                            return true;
                        }
                        else if(args[0].equalsIgnoreCase("help")){
                            commandList(commandSender);
                            return true;
                        }
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("setType") && args.length == 3) {
                            if (ArenaManager.isArena(plot, args[0])) {
                                Arena a = ArenaManager.getArena(args[0], plot);
                                if (a.setType(args[2]))
                                    Utils.infoMessage(commandSender, "Type: " + args[2] + " has been set for" +
                                            "Arena " + args[0] + ".");
                                else {
                                    Utils.errorMessage(commandSender, "Not a valid game type.");
                                    Utils.errorMessage(commandSender, "Valid game types: " + "&4 " + Utils.gameTypes());
                                }
                            } else {
                                Utils.errorMessage(commandSender, "That is not a valid mini game name."); // list arenas?
                            }
                            return true;
                        } else if (args[1].equalsIgnoreCase("addSpawn") && args.length == 2) {
                            if (ArenaManager.isArena(plot, args[0])) {
                                Arena a = ArenaManager.getArena(args[0], plot);
                                int index = a.getLocations().size(); //default spawn index
                                a.setSpawn(player.getLocation());
                                    Utils.infoMessage(commandSender, " Spawn: " + (index + 1) + " has been set for" +
                                            " mini game " + args[0] + ".");
                            }

                            return true;
                        }
                        else if (args[1].equalsIgnoreCase("removeSpawns") && args.length == 2) {
                            if (ArenaManager.isArena(plot, args[0])) {
                                Arena a = ArenaManager.getArena(args[0], plot);
                                a.removeSpawns();
                                    Utils.infoMessage(commandSender, " Spawn have been removed for " +
                                            "mini game " + args[0] + ".");
                            }

                            return true;
                        }
                         else if (args[0].equalsIgnoreCase("start") && args.length == 2) {
                            if (ArenaManager.isArena(plot, args[1])) {
                                Arena a = ArenaManager.getArena(args[1], plot);
                                if (!ArenaManager.getAllEnabledArenas().containsKey(a.getStringID())) {
                                    if (a.start()) {
                                        ArenaManager.broadcastToPlot(plot, org.bukkit.ChatColor.GREEN
                                                + args[1] + " mini game has been started by " + player.getDisplayName());
                                    } else {
                                        Utils.errorMessage(commandSender, "This mini game is not set up. Do /mini list to view if you need to set a spawn or gametype.");
                                    }
                                } else {
                                    Utils.errorMessage(commandSender, "There is already an enabled mini game in this plot.");
                                }
                            }
                            return true;
                        } else if (args[0].equalsIgnoreCase("end") && args.length == 2) {
                            if (ArenaManager.isArena(plot, args[1])) {
                                Arena a = ArenaManager.getArena(args[1], plot);
                                if (ArenaManager.getAllEnabledArenas().containsKey(a.getStringID())) {
                                    ArenaManager.broadcastToPlot(plot, ChatColor.GREEN
                                            + args[1] + " mini game has been disabled by " + player.getDisplayName());
                                    a.end();
                                } else {
                                    Utils.errorMessage(commandSender, "This mini game is not enabled?");
                                }
                            } else {
                                Utils.errorMessage(commandSender, "That is not a valid minigame name.");
                            }
                            return true;
                        }
                        else {
                            Utils.errorMessage(commandSender, unknownArguments);
                            return true;
                        }
                      }
                    }
                } else {
                    Utils.errorMessage(commandSender, "You may only do mini game commands in a plot.");
                    }
                }
                else {
                    Utils.errorMessage(commandSender, "No permission to use these mini game commands.");
                }
            }
        }
            return false;
    }

    private void commandList(CommandSender sender){
        Utils.infoMessage(sender, "/mini create [name] - Create a minigame with a name");
        Utils.infoMessage(sender, "/mini remove [name] - Create a minigame with a name");
        Utils.infoMessage(sender, "/mini [name] setType [type]");
        Utils.infoMessage(sender, "/mini [name] addSpawn - Sets spawn at your location");
        Utils.infoMessage(sender, "/mini [name] addSpawn - Removes all spawns.");
        Utils.infoMessage(sender, "/mini start [name]");
        Utils.infoMessage(sender, "/mini end [name]");
        Utils.infoMessage(sender, "/mini list");
        Utils.infoMessage(sender, "/mini load");
    }
}
