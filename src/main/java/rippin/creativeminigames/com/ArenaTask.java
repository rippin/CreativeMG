package rippin.creativeminigames.com;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;


/**
 * Created by Rippin on 4/2/16.
 */
public class ArenaTask {
    private Arena a;
    private long iteration;
    private BukkitTask task;

    public ArenaTask(Arena a, long iteration){
        this.a = a;
        this.iteration = iteration;
    }

    public void start(){
        task = Bukkit.getServer().getScheduler().runTaskTimer(CreativeMGMain.plugin, new Runnable() {
            public void run() {
                if (a.getType() == GameType.TNTRUN)
                    if (a.getPlayers().size() == 1){
                        a.playerWon(a.getPlayers());
                        a.end();
                        cancel();
                    }
                    else if (a.getPlayers().size() < 1){
                        ArenaManager.broadcastToPlot(a.getPlot(), "No winner somehow?");
                        a.end();
                        cancel();
                    }
            }
        },1L, iteration * 20L);
    }

    public void cancel(){
        task.cancel();
    }
}
