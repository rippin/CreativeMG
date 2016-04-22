package rippin.creativeminigames.com;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by EF on 4/15/2016.
 */
public class SnowballTask {
    private Player player;
    private Arena arena;
    private BukkitTask task;

    public SnowballTask(Player player, Arena arena){
        this.player = player;
        this.arena = arena;
    }

    public void startCountdown(){
        task =  Bukkit.getServer().getScheduler().runTaskTimer(CreativeMGMain.plugin, new Runnable() {
            int i = 7;
            public void run() {
                if (arena.getStatus() != GameStatus.INGAME && arena.getStatus() != GameStatus.STARTING)
                    cancel();
                if (i == 0){
                    if (player == null || !player.isOnline() || !arena.getPlayers().contains(player)){
                        cancel();
                    }
                    else{
                        player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));

                    }
                    i = 7;
                }

                --i;
            }
        },1L, 20L);
    }
    public void cancel(){
        task.cancel();
    }
}
