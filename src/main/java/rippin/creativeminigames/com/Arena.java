package rippin.creativeminigames.com;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rippin.creativeminigames.com.Configs.PlotArenaConfig;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class Arena {

    private List<Player> players = new ArrayList<Player>();
    private List<Player> spectators = new ArrayList<Player>();
    private List<Player> lostPlayers;
    private HashMap<Location, Material> data = new HashMap<Location, Material>();
    private ArenaTask task;
    private String name;
    private GameType type;
    private Plot plot;
    private List<Location> locations = new ArrayList<Location>();
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
    public boolean start(){
        status = GameStatus.STARTING;
        final Arena a = this;
        if (locations.isEmpty() || type == null) return false;
        //Try this here because setFlag seems to call PlayerLeavePlotEvent?
        if (getType() == GameType.OITC) {
            System.out.println("yo");
            getPlot().setFlag("pvp", true);
        }
        CreativeMGMain.plugin.getServer().getScheduler().runTaskLater(CreativeMGMain.plugin, new Runnable() {
            public void run() {


        System.out.println("fsf");
        for (PlotPlayer player : plot.getPlayersInPlot()) {
            players.add(Bukkit.getPlayer(player.getUUID()));
        }
        System.out.println("y");
        /*
            Check to make sure all settings are enabled.
         */
        //Because right now there can only be one Arena enabled
        Set<Arena> set = new HashSet<Arena>();
        set.add(a);
        ArenaManager.allEnabledArenas.put(getStringID(), set);
        new ArenaWarmupTask(a, 10).start();
            }
        },10L);
        return true;
    }

    public void end(){
        status = GameStatus.ENDING;
        ArenaManager.getAllEnabledArenas().remove(getStringID());
        for (Player player : players){
            player.setGameMode(GameMode.CREATIVE);
            player.teleport(locations.get(0));
            player.getInventory().clear();
        }

        for (Player player : spectators){
            player.setGameMode(GameMode.CREATIVE);
        }
        getPlayers().clear();
        getLostPlayers().clear();
        getSpectators().clear();
        Bukkit.getServer().getScheduler().runTaskLater(CreativeMGMain.plugin, new Runnable() {
            public void run() {
                regenBlocks();
            }
        }, 10L);
        task.cancel();
        status = GameStatus.WAITING;
    }

    public void startPlayers(List<Location> locs){
        if (type == GameType.TNTRUN) {
            for (Player player : players) {
                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                player.teleport(locs.get(0));

            }
        }
        else if (type == GameType.PAINTBALL){
            for (Player player : players) {
                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                int rand = ThreadLocalRandom.current().nextInt(0,locations.size() -1);
                    if (locs.get(rand) == null)
                        player.sendMessage("ERROR " + rand);
                player.teleport(locs.get(rand));
                player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
                new SnowballTask(player,this).startCountdown();
            }
        }

        else if (type == GameType.OITC){
            for (Player player : players) {
                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                int rand = ThreadLocalRandom.current().nextInt(0,locations.size() -1);

                player.teleport(locs.get(rand));
                player.getInventory().addItem(new ItemStack(Material.BOW));
                player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD));
                player.getInventory().addItem(new ItemStack(Material.ARROW));

            }
        }
    }

    public void playerWon(List<Player> players){
            if (!players.isEmpty())
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
            PlotArenaConfig.saveFile();
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

    public void setSpawn(Location loc){
        locations.add(loc);
        ArenaManager.setSpawn(this, loc, locations.size() - 1);
    }

    public GameStatus getStatus(){
        return status;
    }
    public void setStatus(GameStatus status) { this.status = status; }
    public void playerLost(Player player){
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
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

    public void removeSpawns(){
        locations = new ArrayList<Location>();
        for (String key : PlotArenaConfig.getConfig().getConfigurationSection("Arena." + getStringID() + "." + name + ".Spawn").getKeys(false)){
            PlotArenaConfig.getConfig().set("Arena." + getStringID() + "." + name + ".Spawn." + key , null);
        }
        PlotArenaConfig.saveFile();
    }

    public HashMap<Location, Material> getData(){
        return  data;
    }

    private void regenBlocks(){
        Iterator it = data.entrySet().iterator();

        while (it.hasNext()){
            Map.Entry<Location, Material> entry = (Map.Entry<Location, Material>) it.next();
            entry.getKey().getBlock().setType(entry.getValue());
        }
        getData().clear();
    }
    public List<Location> getLocations(){
        return locations;
    }
}
