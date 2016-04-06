package rippin.creativeminigames.com;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import rippin.creativeminigames.com.Configs.PlotArenaConfig;

import java.util.*;


public class Arena {

    private List<Player> players = new ArrayList<Player>();
    private List<Player> spectators = new ArrayList<Player>();
    private List<Player> lostPlayers;
    private HashMap<Location, Block> data = new HashMap<Location, Block>();
    private ArenaTask task;
    private String name;
    private GameType type;
    private Plot plot;
    private List<Location> locations = Arrays.asList(new Location[10]);
    private GameStatus status;
    public Arena(String name, GameType type, Plot plot){
        this.name = name;
        this.type = type;
        this.plot = plot;
        lostPlayers = new ArrayList<Player>();
        status = GameStatus.WAITING;
        //do gameetype stuff

    }
    public Arena(String name, Plot plot){

        this.name = name;
        this.plot = plot;
        lostPlayers = new ArrayList<Player>();
        status = GameStatus.WAITING;

    }
    //make sure arena from this plot is not already enabled in command.
    public void start(){
        status = GameStatus.STARTING;
        for (PlotPlayer player : plot.getPlayersInPlot()) {
            players.add(Bukkit.getPlayer(player.getUUID()));
        }
        System.out.println("size: " + players.size());
        /*
            Check to make sure all settings are enabled.
         */
        //Because right now there can only be one Arena enabled
        Set<Arena> set = new HashSet<Arena>();
        set.add(this);
        ArenaManager.allEnabledArenas.put(getStringID(), set);
        new ArenaWarmupTask(this, 10).start();
    }

    public void end(){
        status = GameStatus.ENDING;
        ArenaManager.getAllEnabledArenas().remove(getStringID());
        getPlayers().clear();
        getLostPlayers().clear();
        Bukkit.getServer().getScheduler().runTaskLater(CreativeMGMain.plugin, new Runnable() {
            public void run() {
                regenBlocks();
            }
        }, 10L);


        status = GameStatus.WAITING;
    }

    public void startPlayers(Location loc){
        if (type == GameType.TNTRUN)
        for (Player player : players){
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(loc);

        }
    }

    public void playerWon(List<Player> players){
            ArenaManager.broadcastToPlot(plot, players.get(0).getDisplayName() + ChatColor.GOLD + " has won");
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

    public void setString(String str){
        this.name = str;
    }

    public Plot getPlot(){
        return plot;
    }
    public GameType getType(){
        return type;
    }
    public boolean setType(String type){
        GameType t = GameType.getFromString(type);
        if (t != null) {
            this.type = t;
            PlotArenaConfig.getConfig().set("Arena." + getStringID() + "." + name + ".ArenaType", this.type.getString());
            return true;
        }
        return false;
    }
    public void setType(GameType type){
        this.type = type;
        PlotArenaConfig.getConfig().set("Arena." + getStringID() + "." + name + ".ArenaType", type.getString());
    }

    public String getStringID(){
        return plot.getId().x + "-" + plot.getId().y;
    }

    public boolean setSpawn(int index, Location loc){
        boolean flag = true;
        if (type == GameType.TNTRUN) {

            index = 0 ;
        }
        locations.set(index, loc);
        ArenaManager.setSpawn(this, loc, index);
        return flag;
    }

    public GameStatus getStatus(){
        return status;
    }
    public void setStatus(GameStatus status) { this.status = status; }
    public void playerLost(Player player){
        player.setGameMode(GameMode.SPECTATOR);
        lostPlayers.add(player);
        player.teleport(locations.get(0));
        player.sendMessage(ChatColor.RED + "You are in spectator mode until the game is over or you leave this plot.");
        player.sendMessage(ChatColor.RED + "You can also use " + ChatColor.GRAY + "/spawn " + ChatColor.RED + " to leave the ");
    }

    public List<Player> getPlayers() {
        return players;
    }
    public List<Player> getLostPlayers(){
        return lostPlayers;
    }
    public List<Player> getSpectators(){
        return  spectators;
    }

    public ArenaTask getTask(){
        return task;
    }
    public void setTask(ArenaTask task){
        this.task = task;
    }

    public HashMap<Location, Block> getData(){
        return  data;
    }

    private void regenBlocks(){
        Iterator it = data.entrySet().iterator();

        while (it.hasNext()){
            Map.Entry<Location, Block> entry = (Map.Entry<Location, Block>) it.next();
            entry.getKey().getBlock().setType(entry.getValue().getType());
        }
        getData().clear();
    }
    public List<Location> getLocations(){
        return locations;
    }
}
