package rippin.creativeminigames.com;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.bukkit.events.PlayerEnterPlotEvent;
import com.plotsquared.bukkit.events.PlayerLeavePlotEvent;
import com.plotsquared.bukkit.events.PlayerTeleportToPlotEvent;
import com.plotsquared.bukkit.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;


/**
 * Created by Rippin on 4/1/16.
 */
public class EventsListener implements Listener {

    private CreativeMGMain plugin;

    public EventsListener(CreativeMGMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ())
            return;
        else {
            Player player = event.getPlayer();
            PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
            Plot plot = plotPlayer.getCurrentPlot();
            String plotID = plot.getId().x + "-" + plot.getId().y;
            if (plot != null) {
                if (ArenaManager.getAllEnabledArenas().containsKey(plotID)) {
                    //since only 1 arena will be anabled at a time atm just get first element.
                    for (Arena a : ArenaManager.getAllEnabledArenas().get(plotID)) {
                        if (a.getType() == GameType.TNTRUN && a.getStatus() == GameStatus.INGAME) {
                            //Run TNTRUN CODE
                            final Block standing = player.getLocation().getBlock();
                            if (standing.getType() == Material.SAND || standing.getType() == Material.SOUL_SAND) {
                                final Block belowStanding = standing.getLocation().add(0, -1, 0).getBlock();
                                if (belowStanding.getType() == Material.TNT)
                                    //run delayed task here to remove both blocks;
                                    a.getData().put(standing.getLocation(), standing);
                                    a.getData().put(belowStanding.getLocation(), belowStanding);
                                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                                    public void run() {
                                            standing.setType(Material.AIR);
                                            belowStanding.setType(Material.AIR);
                                            //set both to air

                                        }
                                    }, 20L);

                            }
                            else if (standing.getType() == Material.WATER || standing.getType() == Material.STATIONARY_WATER){
                                a.playerLost(player);
                            }
                        }
                        return;
                    }
                }
            }

        }
    }

    @EventHandler
    public void LeavePlotEvent(PlayerLeavePlotEvent event) {
        Plot plot = event.getPlot();
        if (plot != null) {
            Player player = event.getPlayer();
            String id = plot.getId().x + "-" + plot.getId().y;

            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                for (Arena a : ArenaManager.getAllEnabledArenas().get(id)) {
                    //only one arena so you can do this.
                    a.getLostPlayers().add(player);
                    a.getPlayers().remove(player);
                    player.setGameMode(GameMode.CREATIVE); //because  its a creative server
                    player.sendMessage(ChatColor.RED + "You have left the plot and abandoned the minigame.");
                }
            }

        }
    }

    @EventHandler
    public void enterPlotEvent(PlayerEnterPlotEvent event) {
        Plot plot = event.getPlot();
        if (plot != null) {
            Player player = event.getPlayer();
            String id = plot.getId().x + "-" + plot.getId().y;

            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                for (Arena a : ArenaManager.getAllEnabledArenas().get(id)) {
                    //only one arena so you can do this.
                    player.setGameMode(GameMode.SPECTATOR); //because  its a creative server
                    player.sendMessage(ChatColor.GREEN + "You have entered a plot that is currently" +
                            "in a minigame. You may only spectate until the game is over.");
                    a.getSpectators().add(player);
                }
            }

        }
    }

    @EventHandler
    public void teleportPlotEvent(PlayerTeleportToPlotEvent event) {
        Plot plot = event.getPlot();
        if (plot != null) {
            Player player = event.getPlayer();
            String id = plot.getId().x + "-" + plot.getId().y;

            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                for (Arena a : ArenaManager.getAllEnabledArenas().get(id)) {
                    //only one arena so you can do this.
                    player.setGameMode(GameMode.SPECTATOR); //because  its a creative server
                    player.sendMessage(ChatColor.GREEN + "You have entered a plot that is currently" +
                            "in a minigame. You may only spectate until the game is over.");
                    a.getSpectators().add(player);
                }
            }

        }
    }

    @EventHandler
    public void playerSendsCommandEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
        Plot plot = plotPlayer.getCurrentPlot();
        String id = plot.getId().x + "-" + plot.getId().y;
        if (plot != null) {
            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                if (!event.getMessage().equalsIgnoreCase("spawn") || !event.getMessage().toLowerCase().contains("tp")){
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void placeBlockEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
        Plot plot = plotPlayer.getCurrentPlot();
        String id = plot.getId().x + "-" + plot.getId().y;
        if (plot != null) {
            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                    event.setCancelled(true);
                }
            }
        }

    @EventHandler
    public void breakBlockEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
        Plot plot = plotPlayer.getCurrentPlot();
        String id = plot.getId().x + "-" + plot.getId().y;
        if (plot != null) {
            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void breakBlockEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
        Plot plot = plotPlayer.getCurrentPlot();
        String id = plot.getId().x + "-" + plot.getId().y;
        if (plot != null) {
            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                event.setCancelled(true);
            }
        }
    }
}