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
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;


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
            if (plot != null) {
                String plotID = plot.getId().x + "-" + plot.getId().y;
                if (ArenaManager.getAllEnabledArenas().containsKey(plotID)) {
                    //since only 1 arena will be anabled at a time atm just get first element.
                    for (Arena a : ArenaManager.getAllEnabledArenas().get(plotID)) {
                        if (a.getType() == GameType.TNTRUN && a.getStatus() == GameStatus.INGAME) {
                            //Run TNTRUN CODE
                            final Block standing = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                            if (standing.getType() == Material.SAND || standing.getType() == Material.GRAVEL) {
                                final Block belowStanding = standing.getLocation().add(0, -1, 0).getBlock();
                                if (belowStanding.getType() == Material.TNT) {
                                    //run delayed task here to remove both blocks;
                                    //TODO you need to clone the block somehow toevert it
                                    // saving the reference does nothing
                                    a.getData().put(standing.getLocation(), standing.getType());
                                    a.getData().put(belowStanding.getLocation(), belowStanding.getType());
                                    plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                                        public void run() {
                                            standing.setType(Material.AIR);
                                            belowStanding.setType(Material.AIR);
                                            //set both to air

                                        }
                                    }, 20L);
                                }

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
            System.out.println("hi");
            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                for (Arena a : ArenaManager.getAllEnabledArenas().get(id)) {
                    if (a.getStatus() == GameStatus.STARTING || a.getStatus() == GameStatus.INGAME)
                    if (a.getPlayers().contains(player)) {
                        a.getLostPlayers().add(player);
                        a.getPlayers().remove(player);
                        player.sendMessage(ChatColor.RED + "You have left the plot and abandoned the minigame.");
                    }
                    player.setGameMode(GameMode.CREATIVE); //because  its a creative server

                }
            }

        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        PlotPlayer pp = BukkitUtil.getPlayer(player);
        Plot plot = pp.getCurrentPlot();
        if (plot != null) {

            String id = plot.getId().x + "-" + plot.getId().y;

            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                for (Arena a : ArenaManager.getAllEnabledArenas().get(id)) {
                    //only one arena so you can do this.
                    if (a.getPlayers().contains(player)) {
                        a.getLostPlayers().add(player);
                        a.getPlayers().remove(player);
                    }
                    player.setGameMode(GameMode.CREATIVE); //because  its a creative server

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
                    player.sendMessage(ChatColor.GREEN + "You have entered a plot that is currently " +
                            "in a mini game. You may only spectate until the game is over.");
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
        if (plot != null) {
            String id = plot.getId().x + "-" + plot.getId().y;
            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                if (!event.getMessage().toLowerCase().contains("spawn")
                        && !event.getMessage().toLowerCase().contains("tp") && !event.getMessage().toLowerCase().contains("mini")){
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

        if (plot != null) {
            String id = plot.getId().x + "-" + plot.getId().y;
            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                    event.setCancelled(true);
                }
            }
        }
    @EventHandler
    public void foodChangeEvent(FoodLevelChangeEvent event){
       if (event.getEntity() instanceof  Player) {
           Player player = (Player) event.getEntity();

           PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
           Plot plot = plotPlayer.getCurrentPlot();

           if (plot != null) {
               String id = plot.getId().x + "-" + plot.getId().y;
               if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                   event.setCancelled(true);
               }
           }
       }
    }
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof  Player) {
            Player player = (Player) event.getEntity();

            PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
            Plot plot = plotPlayer.getCurrentPlot();

            if (plot != null) {
                Arena a = ArenaManager.getPlayersArena(player);
                if (a != null) {
                    if (a.getType() == GameType.TNTRUN) {
                        event.setCancelled(true);
                    }
                    else if (a.getType() == GameType.PAINTBALL && a.getStatus() == GameStatus.INGAME){

                        if (event.getDamager() instanceof Projectile){
                            if (((Projectile) event.getDamager()).getShooter() instanceof Player ){
                                if (event.getDamager() instanceof Snowball){
                                    a.playerLost(player);
                                }
                            }
                        }
                    }
                    else if (a.getType() == GameType.OITC && a.getStatus() == GameStatus.INGAME){

                        if (event.getDamager() instanceof Projectile){

                            if (((Projectile) event.getDamager()).getShooter() instanceof Player ){
                                Player shooter = (Player)((Projectile) event.getDamager()).getShooter();
                                if (event.getDamager() instanceof Arrow){
                                    a.playerLost(player);
                                   shooter.getInventory().addItem(new ItemStack(Material.ARROW));
                                    ArenaManager.broadcastToPlot(a.getPlot(), shooter.getDisplayName() + " has killed " + player.getDisplayName());
                                }
                            }
                        }
                        if (player.getHealth() - event.getFinalDamage() <=0){
                            if (event.getDamager() instanceof  Player) {
                                ArenaManager.broadcastToPlot(a.getPlot(), ((Player) event.getDamager()).getDisplayName() + " has killed " + player.getDisplayName());
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if (event.getEntity() instanceof  Player) {
            Player player = (Player) event.getEntity();
            double damage = event.getFinalDamage();
            PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
            Plot plot = plotPlayer.getCurrentPlot();

            if (plot != null) {
                Arena a = ArenaManager.getPlayersArena(player);
                if (a != null){
                    if (a.getStatus() == GameStatus.STARTING || a.getStatus() == GameStatus.ENDING){
                        event.setCancelled(true);
                    }
                    else if (a.getStatus() == GameStatus.INGAME){
                        if (player.getHealth() - damage <= 0){
                            a.playerLost(player);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        PlotPlayer pp = BukkitUtil.getPlayer(player);
        Plot plot = pp.getCurrentPlot();
        if (plot != null) {

            String id = plot.getId().x + "-" + plot.getId().y;

            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                for (Arena a : ArenaManager.getAllEnabledArenas().get(id)) {
                    if (a.getStatus() == GameStatus.INGAME) {
                        if (a.getPlayers().contains(player)) {
                            a.getLostPlayers().add(player);
                            a.getPlayers().remove(player);
                        }
                    }
                }
            }

        }
    }
    @EventHandler
    public void breakBlockEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
        Plot plot = plotPlayer.getCurrentPlot();

        if (plot != null) {
            String id = plot.getId().x + "-" + plot.getId().y;
            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerDropEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlotPlayer plotPlayer = BukkitUtil.getPlayer(player);
        Plot plot = plotPlayer.getCurrentPlot();

        if (plot != null) {
            String id = plot.getId().x + "-" + plot.getId().y;
            if (ArenaManager.getAllEnabledArenas().containsKey(id)) {
                event.setCancelled(true);
            }
        }
    }
}