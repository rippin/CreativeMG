package rippin.creativeminigames.com;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import rippin.creativeminigames.com.Configs.PlotArenaConfig;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class Arena {

    private List<Player> players = new ArrayList<Player>();
    private List<Player> spectators = new ArrayList<Player>();
    private List<Player> lostPlayers;
    private HashMap<Location, String> data = new HashMap<Location, String>();
    private Map<Player, SnowballTask> snowballTasks = new HashMap<Player,SnowballTask>();
    private ArenaTask task;
    private String name;
    private GameType type;
    private Plot plot;
    private List<Location> locations = new ArrayList<Location>();
    private GameStatus status;
    public Arena(String name, GameType type, Plot plot){
        this.name = name.toLowerCase();
        this.type = type;
        this.plot = plot;
        lostPlayers = new ArrayList<Player>();
        status = GameStatus.WAITING;
        //do gameetype stuff

    }
    public Arena(String name, Plot plot){

        this.name = name.toLowerCase();
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
        if (getType() == GameType.OITC)
            getPlot().setFlag("pvp", true);
        //For some reason setFlag calls PlotPlayerLeaveEvent so workaround is to delay adding players for 10 ticks.
        CreativeMGMain.plugin.getServer().getScheduler().runTaskLater(CreativeMGMain.plugin, new Runnable() {

            public void run() {
            for (PlotPlayer player : plot.getPlayersInPlot()) {
                players.add(Bukkit.getPlayer(player.getUUID()));
             }
                /*
                Check to make sure all settings are enabled.
                */
                new ArenaWarmupTask(a, 10).start();
            }
        },10L);
        //Because right now there can only be one Arena enabled
        Set<Arena> set = new HashSet<Arena>();
        set.add(a);
        ArenaManager.allEnabledArenas.put(getStringID(), set);
        return true;
    }

    public void end(){
        status = GameStatus.ENDING;
            ArenaManager.getAllEnabledArenas().remove(getStringID());
            for (Player player : players){
                player.setGameMode(GameMode.CREATIVE);
                player.teleport(locations.get(0));
                player.getInventory().clear();
                if (type == GameType.PAINTBALL) {
                    if (snowballTasks.containsKey(player)) {
                        SnowballTask task = snowballTasks.get(player);
                        if (task != null)
                            task.cancel();
                    }
                }
            }
            for (Player player : spectators) {
                player.setGameMode(GameMode.CREATIVE);
                player.teleport(locations.get(0));
                if (type == GameType.PAINTBALL) {
                    if (snowballTasks.containsKey(player)) {
                        SnowballTask task = snowballTasks.get(player);
                        if (task != null)
                            task.cancel();
                    }
                }
                player.getInventory().clear();

            }
            getPlayers().clear();
            getLostPlayers().clear();
            getSpectators().clear();
            snowballTasks.clear();

        Bukkit.getServer().getScheduler().runTaskLater(CreativeMGMain.plugin, new Runnable() {
            public void run() {
                regenBlocks();
            }
        }, 10L);
        status = GameStatus.WAITING;
    }

    public void startPlayers(List<Location> locs){
        if (type == GameType.TNTRUN) {
            for (Player player : players) {
                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
                player.setHealth(player.getMaxHealth());
                //remove potions
                for (PotionEffect eff : player.getActivePotionEffects())
                player.removePotionEffect(eff.getType());
                player.setFoodLevel(20);
                player.teleport(locs.get(0));

            }
        }


        else if (type == GameType.PVPRUN){
            for (Player player : players) {
                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
                player.setHealth(player.getMaxHealth());
                //remove potions
                for (PotionEffect eff : player.getActivePotionEffects())
                    player.removePotionEffect(eff.getType());
                player.setFoodLevel(20);
                player.teleport(locs.get(0));
                player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));

            }
        }
        else if (type == GameType.TNTSPLEEF){
            for (Player player : players) {
                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                //remove potions
                for (PotionEffect eff : player.getActivePotionEffects())
                    player.removePotionEffect(eff.getType());
                player.teleport(locs.get(0));
                ItemStack is = new ItemStack(Material.BOW);
                is.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
                is.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
                player.getInventory().addItem(is);
                player.getInventory().addItem(new ItemStack(Material.ARROW));

            }
        }
        else if (type == GameType.PAINTBALL){
            for (Player player : players) {
                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
                //remove potions
                for (PotionEffect eff : player.getActivePotionEffects())
                    player.removePotionEffect(eff.getType());
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                int rand = ThreadLocalRandom.current().nextInt(0,locations.size() -1);
                    if (locs.get(rand) == null)
                        player.sendMessage("ERROR " + rand);
                player.teleport(locs.get(rand));
                player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
                SnowballTask t = new SnowballTask(player,this);
                t.startCountdown();
                snowballTasks.put(player, t);
            }
        }

        else if (type == GameType.OITC){
            for (Player player : players) {
                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
                //remove potions
                for (PotionEffect eff : player.getActivePotionEffects())
                    player.removePotionEffect(eff.getType());
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
        else
                ArenaManager.broadcastToPlot(plot, ChatColor.GOLD + "Game is over with no winner :(");
    }

    @Override
    public boolean equals(Object that){
    if (this == that) return true;
    if (!(that instanceof Arena)) return false;
        Arena thatArena = (Arena)that;
        if (thatArena.getStringID().equalsIgnoreCase(getStringID()) && thatArena.getName() == this.getName()) return true;
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
        getPlayers().remove(player);
        spectators.add(player);
        player.sendMessage(ChatColor.RED + "You are in spectator mode until the game is over or you leave this plot.");
        player.sendMessage(ChatColor.RED + "You can also use " + ChatColor.GRAY + "/spawn " + ChatColor.RED + " to leave the ");
        if (type == GameType.TNTRUN)
            ArenaManager.broadcastToPlot(plot, player.getDisplayName() + " &4has lost.");

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

    public HashMap<Location, String> getData(){
        return  data;
    }

    private void regenBlocks(){
        Iterator it = data.entrySet().iterator();

        while (it.hasNext()){
            Map.Entry<Location, String> entry = (Map.Entry<Location, String>) it.next();
            String split[] = entry.getValue().split(":");
            entry.getKey().getBlock().setType(Material.getMaterial(split[4]));
            entry.getKey().getBlock().setData(Byte.valueOf(split[5]));
        }
        getData().clear();
        PlotArenaConfig.getConfig().set("Arena." + getStringID() + "." + name + ".Data", null);
        PlotArenaConfig.saveFile();
    }
    public List<Location> getLocations(){
        return locations;
    }

    public void setData(Location loc, Block block){
        String d = loc.getWorld().getName() + ":" + loc.getX() + ":" +  loc.getY() + ":" + loc.getZ() +  ":" + block.getType().name() + ":" + block.getData();
        data.put(loc, d);
        PlotArenaConfig.getConfig().set("Arena." + getStringID() + "." + name + ".Data", new ArrayList<String>(data.values()));
        PlotArenaConfig.saveFile();
    }

    public boolean regenBlocksAfterCrash(){
        List<String> list = PlotArenaConfig.getConfig().getStringList("Arena." + getStringID() + "." + name + ".Data");
        if (list != null){
        for (String s : list){
            String split[] = s.split(":");
            Location loc = new Location(Bukkit.getWorld(split[0]), Double.valueOf(split[1]),Double.valueOf(split[2]),Double.valueOf(split[3]));
            loc.getBlock().setType(Material.getMaterial(split[4]));
            loc.getBlock().setData(Byte.valueOf(split[5]));
        }
            return true;
        }
        return false;
    }
}
