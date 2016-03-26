package rippin.creativeminigames.com;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import java.util.List;


public class Arena {

    private List<PlotPlayer> players;
    private String name;
    private GameType type;
    private Plot plot;
    public Arena(String name, GameType type, Plot plot){
        //all plotplayers.
        players.addAll(plot.getPlayersInPlot());
        this.name = name;
        this.type = type;
        this.plot = plot;
        //do gameetype stuff

    }

    public void start(){
        if (type == GameType.TNTRUN){

        }
    }

    public void end(){

    }
    @Override
    public boolean equals(Object that){
    if (this == that) return true;
    if (!(that instanceof Arena)) return false;
        Arena thatArena = (Arena)that;
        if (thatArena.plot == this.getPlot() && thatArena.getName() == this.getName()) return true;
        return false;
    }

    public String getName(){
        return name;
    }

    public Plot getPlot(){
        return plot;
    }

    public
}
