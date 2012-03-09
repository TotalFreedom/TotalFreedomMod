package me.StevenLawson.TotalFreedomMod;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;

public class TFM_Util
{
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Map<String, Integer> eject_tracker = new HashMap<String, Integer>();
    public static final Map<String, EntityType> mobtypes = new HashMap<String, EntityType>();
    public static final List<String> stop_commands = new ArrayList<String>();

    static
    {
        mobtypes.put("blaze", EntityType.BLAZE);
        mobtypes.put("cavespider", EntityType.CAVE_SPIDER);
        mobtypes.put("chicken", EntityType.CHICKEN);
        mobtypes.put("cow", EntityType.COW);
        mobtypes.put("creeper", EntityType.CREEPER);
        mobtypes.put("enderdragon", EntityType.ENDER_DRAGON);
        mobtypes.put("enderman", EntityType.ENDERMAN);
        mobtypes.put("ghast", EntityType.GHAST);
        mobtypes.put("giant", EntityType.GIANT);
        mobtypes.put("irongolem", EntityType.IRON_GOLEM);
        mobtypes.put("mushroomcow", EntityType.MUSHROOM_COW);
        mobtypes.put("ocelot", EntityType.OCELOT);
        mobtypes.put("pig", EntityType.PIG);
        mobtypes.put("pigzombie", EntityType.PIG_ZOMBIE);
        mobtypes.put("sheep", EntityType.SHEEP);
        mobtypes.put("silverfish", EntityType.SILVERFISH);
        mobtypes.put("skeleton", EntityType.SKELETON);
        mobtypes.put("slime", EntityType.SLIME);
        mobtypes.put("snowman", EntityType.SNOWMAN);
        mobtypes.put("spider", EntityType.SPIDER);
        mobtypes.put("squid", EntityType.SQUID);
        mobtypes.put("villager", EntityType.VILLAGER);
        mobtypes.put("wolf", EntityType.WOLF);
        mobtypes.put("zombie", EntityType.ZOMBIE);
        
        stop_commands.add("stop");
        stop_commands.add("off");
        stop_commands.add("end");
        stop_commands.add("halt");
        stop_commands.add("die");
    }

    private TFM_Util()
    {
        throw new AssertionError();
    }

    public static void bcastMsg(String message, ChatColor color)
    {
        log.info(message);

        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(color + message);
        }
    }

    public static void bcastMsg(String message)
    {
        log.info(ChatColor.stripColor(message));

        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(message);
        }
    }

    public static String implodeStringList(String glue, List<String> pieces)
    {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < pieces.size(); i++)
        {
            if (i != 0)
            {
                output.append(glue);
            }
            output.append(pieces.get(i));
        }
        return output.toString();
    }

    public static String formatLocation(Location in_loc)
    {
        return String.format("%s: (%d, %d, %d)",
                in_loc.getWorld().getName(),
                Math.round(in_loc.getX()),
                Math.round(in_loc.getY()),
                Math.round(in_loc.getZ()));
    }

    public static void gotoWorld(CommandSender sender, String targetworld)
    {
        if (sender instanceof Player)
        {
            Player sender_p = (Player) sender;

            if (sender_p.getWorld().getName().equalsIgnoreCase(targetworld))
            {
                sender.sendMessage(ChatColor.GRAY + "Going to main world.");
                sender_p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()); 
                return;
            }

            for (World world : Bukkit.getWorlds())
            {
                if (world.getName().equalsIgnoreCase(targetworld))
                {
                    sender.sendMessage(ChatColor.GRAY + "Going to world: " + targetworld);
                    sender_p.teleport(world.getSpawnLocation());
                    return;
                }
            }
            
            sender.sendMessage(ChatColor.GRAY + "World " + targetworld + " not found.");
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.NOT_FROM_CONSOLE);
        }
    }

    public static void buildHistory(Location location, int length, TFM_UserInfo playerdata)
    {
        Block center_block = location.getBlock();
        for (int x_offset = -length; x_offset <= length; x_offset++)
        {
            for (int y_offset = -length; y_offset <= length; y_offset++)
            {
                for (int z_offset = -length; z_offset <= length; z_offset++)
                {
                    Block block = center_block.getRelative(x_offset, y_offset, z_offset);
                    playerdata.insertHistoryBlock(block.getLocation(), block.getType());
                }
            }
        }
    }

    public static void generateCube(Location location, int length, Material material)
    {
        Block center_block = location.getBlock();
        for (int x_offset = -length; x_offset <= length; x_offset++)
        {
            for (int y_offset = -length; y_offset <= length; y_offset++)
            {
                for (int z_offset = -length; z_offset <= length; z_offset++)
                {
                    center_block.getRelative(x_offset, y_offset, z_offset).setType(material);
                }
            }
        }
    }

    public static void setWorldTime(World world, long ticks)
    {
        long time = world.getTime();
        time -= time % 24000;
        world.setTime(time + 24000 + ticks);
    }

    public static void createDefaultConfiguration(String name, TotalFreedomMod tfm, File plugin_file)
    {
        File actual = new File(tfm.getDataFolder(), name);
        if (!actual.exists())
        {
            log.info("[" + tfm.getDescription().getName() + "]: Installing default configuration file template: " + actual.getPath());
            InputStream input = null;
            try
            {
                JarFile file = new JarFile(plugin_file);
                ZipEntry copy = file.getEntry(name);
                if (copy == null)
                {
                    log.severe("[" + tfm.getDescription().getName() + "]: Unable to read default configuration: " + actual.getPath());
                    return;
                }
                input = file.getInputStream(copy);
            }
            catch (IOException ioex)
            {
                log.severe("[" + tfm.getDescription().getName() + "]: Unable to read default configuration: " + actual.getPath());
            }
            if (input != null)
            {
                FileOutputStream output = null;

                try
                {
                    tfm.getDataFolder().mkdirs();
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length;
                    while ((length = input.read(buf)) > 0)
                    {
                        output.write(buf, 0, length);
                    }

                    log.info("[" + tfm.getDescription().getName() + "]: Default configuration file written: " + actual.getPath());
                }
                catch (IOException ioex)
                {
                    log.log(Level.SEVERE, "[" + tfm.getDescription().getName() + "]: Unable to write default configuration: " + actual.getPath(), ioex);
                }
                finally
                {
                    try
                    {
                        if (input != null)
                        {
                            input.close();
                        }
                    }
                    catch (IOException ioex)
                    {
                    }

                    try
                    {
                        if (output != null)
                        {
                            output.close();
                        }
                    }
                    catch (IOException ioex)
                    {
                    }
                }
            }
        }
    }

    public static boolean isUserSuperadmin(CommandSender user, TotalFreedomMod tfm)
    {
        try
        {
            if (!(user instanceof Player))
            {
                return true;
            }

            if (Bukkit.getOnlineMode())
            {
                if (TotalFreedomMod.superadmins.contains(user.getName().toLowerCase()))
                {
                    return true;
                }
            }

            Player p = (Player) user;
            if (p != null)
            {
                InetSocketAddress ip_address_obj = p.getAddress();
                if (ip_address_obj != null)
                {
                    String user_ip = ip_address_obj.getAddress().toString().replaceAll("/", "").trim();
                    if (user_ip != null && !user_ip.isEmpty())
                    {
                        if (TotalFreedomMod.superadmin_ips.contains(user_ip))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            log.severe("Exception in TFM_Util.isUserSuperadmin: " + ex.getMessage());
        }

        return false;
    }

    public static boolean checkPartialSuperadminIP(String user_ip, TotalFreedomMod tfm)
    {
        user_ip = user_ip.trim();

        if (TotalFreedomMod.superadmin_ips.contains(user_ip))
        {
            return true;
        }
        else
        {
            String[] user_octets = user_ip.split("\\.");
            if (user_octets.length != 4)
            {
                return false;
            }

            String match_ip = null;
            for (String test_ip : TotalFreedomMod.superadmin_ips)
            {
                String[] test_octets = test_ip.split("\\.");
                if (test_octets.length == 4)
                {
                    if (user_octets[0].equals(test_octets[0]) && user_octets[1].equals(test_octets[1]) && user_octets[2].equals(test_octets[2]))
                    {
                        match_ip = test_ip;
                        break;
                    }
                }
            }

            if (match_ip != null)
            {
                TotalFreedomMod.superadmin_ips.add(user_ip);

                FileConfiguration config = YamlConfiguration.loadConfiguration(new File(tfm.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));

                fileloop:
                for (String user : config.getKeys(false))
                {
                    List<String> user_ips = (List<String>) config.getStringList(user);
                    for (String ip : user_ips)
                    {
                        ip = ip.toLowerCase().trim();
                        if (ip.equals(match_ip))
                        {
                            log.info("New IP '" + user_ip + "' matches old IP '" + match_ip + "' via partial match, adding it to superadmin list.");
                            user_ips.add(user_ip);
                            config.set(user, user_ips);
                            break fileloop;
                        }
                    }
                }

                try
                {
                    config.save(new File(tfm.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));
                }
                catch (IOException ex)
                {
                    log.log(Level.SEVERE, null, ex);
                }
            }

            return match_ip != null;
        }
    }

    public static int wipeDropEntities(boolean wipe_tnt)
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity ent : world.getEntities())
            {
                if (ent instanceof Arrow || (ent instanceof TNTPrimed && wipe_tnt) || ent instanceof Item || ent instanceof ExperienceOrb)
                {
                    ent.remove();
                    removed++;
                }
            }
        }
        return removed;
    }

    public static boolean deleteFolder(File file)
    {
        if (file.exists())
        {
            if (file.isDirectory())
            {
                for (File f : file.listFiles())
                {
                    if (!TFM_Util.deleteFolder(f))
                    {
                        return false;
                    }
                }
            }
            file.delete();
            return !file.exists();
        }
        else
        {
            return false;
        }
    }

    public static EntityType getEntityType(String mobname) throws Exception
    {
        mobname = mobname.toLowerCase().trim();
        
        if (!TFM_Util.mobtypes.containsKey(mobname))
        {
            throw new Exception();
        }
        
        return TFM_Util.mobtypes.get(mobname);
    }

    public static void zip(File directory, File zipfile, boolean verbose, CommandSender sender) throws IOException
    {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = out;
        try
        {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty())
            {
                directory = queue.pop();
                for (File kid : directory.listFiles())
                {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory())
                    {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    }
                    else
                    {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(kid, zout);
                        zout.closeEntry();
                    }

                    if (verbose)
                    {
                        sender.sendMessage("Zipping: " + name);
                    }
                }
            }
        }
        finally
        {
            res.close();
        }
    }

    public static void unzip(File zipfile, File directory) throws IOException
    {
        ZipFile zfile = new ZipFile(zipfile);
        Enumeration<? extends ZipEntry> entries = zfile.entries();
        while (entries.hasMoreElements())
        {
            ZipEntry entry = entries.nextElement();
            File file = new File(directory, entry.getName());
            if (entry.isDirectory())
            {
                file.mkdirs();
            }
            else
            {
                file.getParentFile().mkdirs();
                InputStream in = zfile.getInputStream(entry);
                try
                {
                    copy(in, file);
                }
                finally
                {
                    in.close();
                }
            }
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        while (true)
        {
            int readCount = in.read(buffer);
            if (readCount < 0)
            {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    private static void copy(File file, OutputStream out) throws IOException
    {
        InputStream in = new FileInputStream(file);
        try
        {
            copy(in, out);
        }
        finally
        {
            in.close();
        }
    }

    private static void copy(InputStream in, File file) throws IOException
    {
        OutputStream out = new FileOutputStream(file);
        try
        {
            copy(in, out);
        }
        finally
        {
            out.close();
        }
    }

    public static boolean isStopCommand(String command)
    {
        return stop_commands.contains(command.toLowerCase());
    }

    enum EjectMethod
    {
        STRIKE_ONE, STRIKE_TWO, STRIKE_THREE;
    }

    public static void autoEject(Player p, String kickMessage)
    {
        EjectMethod method = EjectMethod.STRIKE_ONE;
        String player_ip = null;
        
        try
        {
            player_ip = p.getAddress().getAddress().getHostAddress();
            
            Integer num_kicks = TFM_Util.eject_tracker.get(player_ip);
            if (num_kicks == null)
            {
                num_kicks = new Integer(0);
            }

            num_kicks = new Integer(num_kicks.intValue() + 1);
            
            TFM_Util.eject_tracker.put(player_ip, num_kicks);

            if (num_kicks.intValue() <= 1)
            {
                method = EjectMethod.STRIKE_ONE;
            }
            else if (num_kicks.intValue() == 2)
            {
                method = EjectMethod.STRIKE_TWO;
            }
            else if (num_kicks.intValue() >= 3)
            {
                method = EjectMethod.STRIKE_THREE;
            }
        }
        catch (Exception ex)
        {
        }
        
        log.info("autoEject -> name: " + p.getName() + " - player_ip: " + player_ip + " - method: " + method.toString());

        p.setOp(false);
        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().clear();
        p.kickPlayer(kickMessage);

        switch (method)
        {
            case STRIKE_ONE:
            {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("tempban %s 1m", p.getName()));
                break;
            }
            case STRIKE_TWO:
            {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("tempban %s 3m", p.getName()));
                break;
            }
            case STRIKE_THREE:
            {
                Bukkit.banIP(player_ip);
                Bukkit.getOfflinePlayer(p.getName()).setBanned(true);
                break;
            }
        }
    }
    
    public static void generateFlatlands()
    {
        generateFlatlands(TotalFreedomMod.flatlandsGenerationParams);
    }

    public static void generateFlatlands(String genParams)
    {
        WorldCreator flatlands = new WorldCreator("flatlands");
        flatlands.generateStructures(false);
        flatlands.type(WorldType.NORMAL);
        flatlands.environment(World.Environment.NORMAL);
        flatlands.generator(new CleanroomChunkGenerator(genParams));
        Bukkit.getServer().createWorld(flatlands);
    }
}
