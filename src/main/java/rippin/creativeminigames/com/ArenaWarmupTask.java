package rippin.creativeminigames.com;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by Rippin on 4/2/16.
 */
public class ArenaWarmupTask {
    private Arena a;
    private int iteration;
    private BukkitTask task;
    public ArenaWarmupTask(Arena a, int iteration){
        this.a = a;
        this.iteration = iteration;
    }

    public void start(){
            a.startPlayers(a.getLocations());
      //No warmup needed for these gametypes.
       if (a.getType() == GameType.PAINTBALL && a.getType() == GameType.OITC){
           ArenaTask task = new ArenaTask(a, 1000);
           a.setTask(task);
           task.start();
           return;
       }
        task =  Bukkit.getServer().getScheduler().runTaskTimer(CreativeMGMain.plugin, new Runnable() {
            int i = iteration;
            public void run() {
                if (i == 10){
                    ArenaManager.broadcastToPlot(a.getPlot(), "&aGame starting in " + i + " seconds.");
                }
                if (i < 6 && i > 0){
                    ArenaManager.broadcastToPlot(a.getPlot(), "&aGame starting in " + i + " seconds.");
                }
                if (i == 0){
                    ArenaTask task = new ArenaTask(a, 1000);
                    ArenaManager.broadcastToPlot(a.getPlot(), "&aGame started.");
                    task.start();
                    a.setTask(task);
                    cancel();
                }

                --i;
            }
        },1L, 20L);
    }

    public void cancel(){
        task.cancel();
    }
}
