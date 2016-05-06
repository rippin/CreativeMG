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
                            if (!ArenaManager.isArena(plot, args[1].toLowerCase())) {
                                //TODO: hardcode in size but change it to get it from a config later
                                if (ArenaManager.getArenas(plot).size() < 5) {
                                    ArenaManager.createArena(player, args[1].toLowerCase());
                                    Utils.infoMessage(commandSender, "Arena " + args[1] + " has been added.");
                                }
                                else {
                                    Utils.errorMessage(commandSender, "You already have the max amount of mini " +
                                            "game arenas. Do &a/mini remove [name] &7and remove one first.");
                                }
                            }
                             else {
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
                            ArenaManager.removeArena(player, args[1].toLowerCase());
                            Utils.infoMessage(commandSender, "Arena " + args[1] + " has been removed.");
                            return true;
                        }
                        else if(args[0].equalsIgnoreCase("commands")){
                            commandList(commandSender);
                            return true;
                        }
                        else if(args[0].equalsIgnoreCase("help")){
                            commandSender.sendMessage(ChatColor.GOLD+ "Creating a mini game is pretty simple.\n" +
                                    ChatColor.GOLD + " - First you need to be an owner/added to a plot.\n" +
                                    ChatColor.GOLD + " - Second you need to create the mini game arena.\n" +
                                    ChatColor.GOLD + " - You can customize the arena, but some game types have requirements.\n" +
                                    ChatColor.GOLD + "- TNTRUN and PVPRUN require a TNT BLOCK under the BLOCK you want to disappear.\n" +
                                    ChatColor.GOLD + "- TNTRUN, PVPRUN AND TNTSPLEEF, require the bottom layer where players fall to be water.\n" +
                                    ChatColor.GOLD + "- TNTSPLEEF, requires the layer that disappears to be TNT.\n" +
                                    ChatColor.GOLD + "- Once you have the arena set up it's time for the command set up.\n" +
                                    ChatColor.GOLD + "- To create an arena do /mini create [name]\n" +
                                    ChatColor.GOLD + "- To set the game type do /mini [name] setGameType [GameType]\n" +
                                    ChatColor.GOLD + "- To view the available mini game types do /mini gametypes \n" +
                                    ChatColor.GOLD + "- To add a spawn do /mini [name] addSpawn\n" +
                                    ChatColor.GOLD + "- Okay now you are ready to play. You need at least two people to play a mini game.\n" +
                                    ChatColor.GOLD + "- To start the mini game do /mini start [name]\n" +
                                    ChatColor.GOLD + "- If at any time you want to end the mini game do /mini end [name]\n" +
                                    ChatColor.DARK_RED +"- Also something important to remember. Each time the server restarts, to load your mini games " +
                                    "that were already created do /mini load");
                            ;

                            return true;
                        }
                        else if (args[0].equalsIgnoreCase("gametype") || args[0].equalsIgnoreCase("gametypes")){
                            Utils.infoMessage(commandSender, "Available gametypes: &6" +Utils.gameTypes());
                            return true;
                        }
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("setType") && args.length == 3) {
                            if (ArenaManager.isArena(plot, args[0].toLowerCase())) {
                                Arena a = ArenaManager.getArena(args[0].toLowerCase(), plot);
                                if (a.setType(args[2]))
                                    Utils.infoMessage(commandSender, "Type: " + args[2] + " has been set for " +
                                            "mini game " + args[0] + ".");
                                else {
                                    Utils.errorMessage(commandSender, "Not a valid game type.");
                                    Utils.errorMessage(commandSender, "Valid game types: " + "&4 " + Utils.gameTypes());
                                }
                            } else {
                                Utils.errorMessage(commandSender, "That is not a valid mini game name."); // list arenas?
                            }
                            return true;
                        } else if (args[1].equalsIgnoreCase("addSpawn") && args.length == 2) {
                            if (ArenaManager.isArena(plot, args[0].toLowerCase())) {
                                Arena a = ArenaManager.getArena(args[0].toLowerCase(), plot);
                                int index = a.getLocations().size(); //default spawn index
                                a.setSpawn(player.getLocation());
                                    Utils.infoMessage(commandSender, " Spawn: " + (index + 1) + " has been set for" +
                                            " mini game " + args[0] + ".");
                            }

                            return true;
                        }
                        else if (args[1].equalsIgnoreCase("removeSpawns") && args.length == 2) {
                            if (ArenaManager.isArena(plot, args[0].toLowerCase())) {
                                Arena a = ArenaManager.getArena(args[0].toLowerCase(), plot);
                                a.removeSpawns();
                                    Utils.infoMessage(commandSender, " Spawn have been removed for " +
                                            "mini game " + args[0] + ".");
                            }

                            return true;
                        }
                         else if (args[0].equalsIgnoreCase("start") && args.length == 2) {
                            if (ArenaManager.isArena(plot, args[1].toLowerCase())) {
                                Arena a = ArenaManager.getArena(args[1].toLowerCase(), plot);
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
                            else {
                                Utils.errorMessage(commandSender, "That is not a valid mini game. Typo? Or try to load the arenas first if you haven't. /mini load");
                            }
                            return true;
                        } else if (args[0].equalsIgnoreCase("end") && args.length == 2) {
                            if (ArenaManager.isArena(plot, args[1].toLowerCase())) {
                                Arena a = ArenaManager.getArena(args[1].toLowerCase(), plot);
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
                    return true;
                    }
                }
                else {
                    Utils.errorMessage(commandSender, "No permission to use these mini game commands.");
                    return true;
                }
            }
        }
            return false;
    }

    private void commandList(CommandSender sender){
        Utils.infoMessage(sender, "/mini help - For help creating mini games.");
        Utils.infoMessage(sender, "/mini create [name] &7- Create a minigame with a name");
        Utils.infoMessage(sender, "/mini remove [name] &7- Delete a minigame with a name");
        Utils.infoMessage(sender, "/mini [name] setType [type] &7 - Set a mini game type");
        Utils.infoMessage(sender, "/mini [name] addSpawn &7- Add a spawn at your location");
        Utils.infoMessage(sender, "/mini [name] removeSpawn &7- Removes all spawns.");
        Utils.infoMessage(sender, "/mini start [name] &7 - Start a mini game");
        Utils.infoMessage(sender, "/mini end [name] - &7 - Manually end a mini game.");
        Utils.infoMessage(sender, "/mini gametypes - &7 - Lists the available game types.");
        Utils.infoMessage(sender, "/mini list &7- Lists the available mini games.");
        Utils.infoMessage(sender, "/mini load &7- Loads your mini games");
        Utils.infoMessage(sender, "/mini commands &7- Lists this list xD");
    }
}
