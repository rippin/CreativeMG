package rippin.creativeminigames.com;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.bukkit.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import rippin.creativeminigames.com.Configs.PlotArenaConfig;

import java.util.*;


public class ArenaManager {
 private static HashMap<String, Set<Arena>> allArenas = new HashMap<String, Set<Arena>>();
    public static HashMap<String, Set<Arena>> allEnabledArenas = new HashMap<String, Set<Arena>>();
 private static List<String> wasInArena = new ArrayList<String>();
    private static FileConfiguration arenaConfig = PlotArenaConfig.getConfig();
    public static void createArena(Player p, String name) {
        PlotPlayer player = BukkitUtil.getPlayer(p);
        Plot plot = player.getCurrentPlot();
        String s = plot.getId().x + "-" + plot.getId().y;
        arenaConfig.createSection("Arena." + s  + "." + name);
        Arena a = new Arena(name, plot);
        if (ArenaManager.getAllArenas().containsKey(a.getStringID()))
            ArenaManager.getAllArenas().get(a.getStringID()).add(a);
        else {
            Set<Arena> set = new HashSet<Arena>();
                    getAllArenas().put(s, set);
        }
        PlotArenaConfig.saveFile();
    }

    public static void removeArena(Player p, String name) {
        PlotPlayer player = BukkitUtil.getPlayer(p);
        Plot plot = player.getCurrentPlot();
        String s = plot.getId().x + "-" + plot.getId().y;
        Arena a = new Arena(name, plot);
        if (ArenaManager.getAllArenas().containsKey(a.getStringID())) {
            ArenaManager.getAllArenas().get(a.getStringID()).remove(a);
            arenaConfig.set("Arena." + s + "." + name, null);
            PlotArenaConfig.saveFile();
            p.sendMessage(ChatColor.GREEN + "Arena " + name + " remove");
        }

    }


    public static void parseSpawns(Arena arena){
        String name = arena.getName();
        FileConfiguration config = arenaConfig;
        if (config.getConfigurationSection("Arena." + arena.getStringID() + "." + name + ".Spawn.") != null) {
            for (String key : config.getConfigurationSection("Arena." + arena.getStringID() + "." + name + ".Spawn.").getKeys(false)) {
                World w = Bukkit.getWorld(config.getString("Arena." + arena.getStringID() + "." + name + ".Spawn." + key + ".World"));
                double x = config.getDouble("Arena." + arena.getStringID() + "." + name + ".Spawn." + key + ".X");
                double y = config.getDouble("Arena." + arena.getStringID() + "." + name + ".Spawn." + key + ".Y");
                double z = config.getDouble("Arena." + arena.getStringID() + "." + name + ".Spawn." + key + ".Z");
                float yaw = (float) config.getDouble("Arena." + arena.getStringID() + "." + name + ".Spawn." + key + ".Yaw");
                float pitch = (float) config.getDouble("Arena." + arena.getStringID() + "." + name + ".Spawn." + key + ".Pitch");

                Location loc = new Location(w, x, y, z, yaw, pitch);
                arena.setSpawn(Integer.valueOf(key), loc);
                System.out.println("Set spawn " + key + " for arena " + name);

            }
        }
    }

    public static Arena getArena(String name, Plot plot){
        String s = plot.getId().x + "-" + plot.getId().y;
        if (getAllArenas().containsKey(s)){
            Set<Arena> set = getAllArenas().get(s);
            for (Arena a : set) {
                if (a.getName().equalsIgnoreCase(name)) {
                    return a;
                }
            }
        }
        return loadArena(name, plot);
    }

    public static void setSpawn(Arena arena, Location loc, int index){
        String name = arena.getName();
        String w = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        arenaConfig.createSection("Arena." + arena.getStringID() + "." + name + ".Spawn." + index);
        arenaConfig.set("Arena." + arena.getStringID() + "." + name + ".Spawn." + index + ".World", w);
        arenaConfig.set("Arena." + arena.getStringID() + "." + name  + ".Spawn." + index + ".X", x);
        arenaConfig.set("Arena." + arena.getStringID() + "." + name +  ".Spawn." + index + ".Y", y);
        arenaConfig.set("Arena." + arena.getStringID() + "." + name +  ".Spawn." + index + ".Z", z);
        arenaConfig.set("Arena." + arena.getStringID() + "." + name + ".Spawn." + index + ".Yaw", yaw);
        arenaConfig.set("Arena." + arena.getStringID() + "." + name + ".Spawn." + index + ".Pitch", pitch);
        PlotArenaConfig.saveFile();
    }

    public static HashMap<String, Set<Arena>> getAllArenas() {
        return allArenas;
    }

    public static Arena loadArena(String name, Plot p) {
        PlotId id = p.getId();
        ConfigurationSection s = arenaConfig.getConfigurationSection("Arena." + id.x + "-" + id.y + "." + name);
        if (s == null) return null;
        Arena arena;
        if (s.getString("ArenaType") != null) {
            GameType type = GameType.getFromString(s.getString("ArenaType"));
            arena = new Arena(name,type, p);
        }
        else {
            arena = new Arena(name, p);
        }
        parseSpawns(arena);
        //Cache Arena into the hashmap
        Set<Arena> set;
        if (getAllArenas().containsKey(arena.getStringID())){
            set = getAllArenas().get(arena.getStringID());
        }
        else{
            set = new HashSet<Arena>();
        }
        set.add(arena);
        getAllArenas().put(arena.getStringID(), set);
        //return the arena
        return arena;
    }
    public static void loadAllArenasFromPlot(Plot p) {
        PlotId id = p.getId();
        for (String name : arenaConfig.getConfigurationSection("Arena." + id.x + "-" + id.y + ".").getKeys(false)) {
            Arena arena;
            String s = ("Arena." + id.x + "-" + id.y + "." + name + ".ArenaType");
            if (arenaConfig.getString(s) != null) {
                GameType type = GameType.getFromString(arenaConfig.getString(s));
                arena = new Arena(name, type, p);
            } else {
                arena = new Arena(name, p);
            }
            parseSpawns(arena);
            //Cache Arena into the hashmap
            Set<Arena> set;
            if (getAllArenas().containsKey(arena.getStringID())) {
                set = getAllArenas().get(arena.getStringID());
            } else {
                set = new HashSet<Arena>();
            }
            set.add(arena);
            getAllArenas().put(arena.getStringID(), set);
            //return the arena
        }
    }
    //Will assume everyone in the plot is in the arena.
    public static boolean isInArena(Plot plot, Player player) {
       PlotPlayer p = BukkitUtil.getPlayer(player);
        Iterator it = allEnabledArenas.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Set<Arena>> entry = (Map.Entry<String, Set<Arena>>) it.next();
            String[] delim = entry.getKey().split("-");
            if (plot.getId().x.toString().equalsIgnoreCase(delim[0]) && plot.getId().y.toString().equalsIgnoreCase(delim[1]))
                if (p.getCurrentPlot().getPlayersInPlot().contains(p)) return true;

        }
        return false;
    }

    public static boolean isInAnyArena(Player player){
        PlotPlayer p = BukkitUtil.getPlayer(player);
        Plot plot = p.getCurrentPlot();
        Iterator it = allEnabledArenas.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Set<Arena>> entry = (Map.Entry<String, Set<Arena>>) it.next();
            String[] delim = entry.getKey().split("-");
            if (plot.getId().x.toString().equalsIgnoreCase(delim[0]) && plot.getId().y.toString().equalsIgnoreCase(delim[1]))
                return true;
        }
        return false;
    }

    public Arena getPlayersArena(Player player){
        Iterator it = allEnabledArenas.entrySet().iterator();
       PlotPlayer p =  BukkitUtil.getPlayer(player);
        Plot plot = p.getCurrentPlot();
        while (it.hasNext()){
            Map.Entry<String, Set<Arena>> entry = (Map.Entry<String, Set<Arena>>) it.next();
            String[] delim = entry.getKey().split("-");
            if (plot.getId().x.toString().equalsIgnoreCase(delim[0]) && plot.getId().y.toString().equalsIgnoreCase(delim[1]))
               return (Arena) entry.getValue().toArray()[0];
        }
        return null;
    }

    public static boolean isArena(Plot plot, String name) {
        Iterator it = getAllArenas().entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Set<Arena>> entry = (Map.Entry<String, Set<Arena>>) it.next();
            String[] delim = entry.getKey().split("-");
            System.out.println(delim[0] + "| " + delim[1]);
            if (plot.getId().x.toString().equalsIgnoreCase(delim[0]) && plot.getId().y.toString().equalsIgnoreCase(delim[1]))
                for (Arena a : entry.getValue()){
                    a.getName().equalsIgnoreCase(name);
                    return true;
                }
        }
        Arena a = loadArena(name, plot);
        if (a != null){
            return true;
        }
        return false;
    }

    public static void broadcastToPlot(Plot p, String message){
        List<PlotPlayer> players = p.getPlayersInPlot();

        for (PlotPlayer pp : players){
            pp.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
        }
    }


    public static void listArenasInPlot(Plot plot, CommandSender sender){
        Iterator it = allArenas.entrySet().iterator();
        sender.sendMessage(ChatColor.GRAY + "Current Minigames");
        while (it.hasNext()){
            Map.Entry<String, Set<Arena>> entry = (Map.Entry<String, Set<Arena>>) it.next();
            if (entry.getKey().equalsIgnoreCase(plot.getId().x + "-" + plot.getId().y))
                for (Arena a : entry.getValue()){
                    if (a.getType() != null) {
                        sender.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + a.getName() + "| " +
                                ChatColor.GRAY + "Gametype: " + ChatColor.GREEN + a.getType().getString());
                    }
                    else {
                        sender.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + a.getName() + "| " +
                                ChatColor.GRAY + "Gametype: " + ChatColor.RED + "NOT SET");
                    }
        }
        }
    }

    public static boolean wasInArena(Player player){

        return wasInArena.contains(player.getUniqueId().toString());
    }

    public static List<String> getWasInArena(){
    return wasInArena;
    }

    public static HashMap<String, Set<Arena>> getAllEnabledArenas() { return allEnabledArenas; }

}
