package rippin.creativeminigames.com;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import java.util.List;


public class Arena {

    private List<PlotPlayer> players;
    private GameType type;
    public Arena(GameType type, Plot plot){
        //all plotplayers.
        players.addAll(plot.getPlayersInPlot());
        type = this.type;
        //do gameetype stuff

    }

    public void start(){
        if (type == GameType.TNTRUN){

        }
    }

    public void end(){

    }
}
