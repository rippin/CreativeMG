package rippin.creativeminigames.com;

import com.intellectualcrafters.plot.flag.Flag;
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
        a.setStatus(GameStatus.INGAME);
        task = Bukkit.getServer().getScheduler().runTaskTimer(CreativeMGMain.plugin, new Runnable() {
           long i = iteration;
            public void run() {
                if (i < 0){
                    ArenaManager.broadcastToPlot(a.getPlot(), "&4Time has run out.");
                    cancel();
                }
                    if (a.getPlayers().size() == 1){ //CHANGE BACK TO 1 JUST FOR TESTING
                        a.playerWon(a.getPlayers());
                        a.end();
                        cancel();
                    }
                    else if (a.getPlayers().size() < 1){
                        ArenaManager.broadcastToPlot(a.getPlot(), "No winner somehow?");
                        a.end();
                        cancel();
                    }
                --i;
            }
        },1L, 20L);
    }

    public void cancel(){
    if (a.getType() == GameType.OITC)
        a.getPlot().setFlag("pvp", false);
        task.cancel();
    }
}
