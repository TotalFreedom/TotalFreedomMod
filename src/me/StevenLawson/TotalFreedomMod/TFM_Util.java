package me.StevenLawson.TotalFreedomMod;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import net.minecraft.server.BanEntry;
import net.minecraft.server.BanList;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;

public class TFM_Util
{
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
        TFM_Log.info(message);

        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(color + message);
        }
    }

    public static void bcastMsg(String message)
    {
        TFM_Log.info(message);

        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.sendMessage(message);
        }
    }

    //JeromSar
    public static void playerMsg(CommandSender sender, String message, ChatColor color)
    {
        sender.sendMessage(color + message);
    }

    //JeromSar
    public static void playerMsg(CommandSender sender, String message)
    {
        TFM_Util.playerMsg(sender, message, ChatColor.GRAY);
    }

    //JeromSar
    public static void adminAction(String adminName, String action, boolean isRed)
    {
        TFM_Util.bcastMsg(adminName + " - " + action, (isRed ? ChatColor.RED : ChatColor.AQUA));
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

    @Deprecated
    public static void createDefaultConfiguration(String name, TotalFreedomMod tfm, File plugin_file)
    {
        TFM_Util.createDefaultConfiguration(name, plugin_file);
    }

    public static void createDefaultConfiguration(String name, File plugin_file)
    {
        TotalFreedomMod tfm = TotalFreedomMod.plugin;

        File actual = new File(tfm.getDataFolder(), name);
        if (!actual.exists())
        {
            TFM_Log.info("Installing default configuration file template: " + actual.getPath());
            InputStream input = null;
            try
            {
                JarFile file = new JarFile(plugin_file);
                ZipEntry copy = file.getEntry(name);
                if (copy == null)
                {
                    TFM_Log.severe("Unable to read default configuration: " + actual.getPath());
                    return;
                }
                input = file.getInputStream(copy);
            }
            catch (IOException ioex)
            {
                TFM_Log.severe("Unable to read default configuration: " + actual.getPath());
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

                    TFM_Log.info("Default configuration file written: " + actual.getPath());
                }
                catch (IOException ioex)
                {
                    TFM_Log.severe("Unable to write default configuration: " + actual.getPath() + "\n" + ExceptionUtils.getStackTrace(ioex));
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

    @Deprecated
    public static boolean isUserSuperadmin(CommandSender user, TotalFreedomMod tfm)
    {
        return isUserSuperadmin(user);
    }

    public static boolean isUserSuperadmin(CommandSender user)
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
                InetSocketAddress address = p.getAddress();
                if (address != null)
                {
                    String user_ip = address.getAddress().getHostAddress();
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
            TFM_Log.severe(ex);
        }

        return false;
    }

    @Deprecated
    public static boolean checkPartialSuperadminIP(String user_ip, TotalFreedomMod tfm)
    {
        return TFM_Util.checkPartialSuperadminIP(user_ip);
    }

    public static boolean checkPartialSuperadminIP(String user_ip)
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

                FileConfiguration config = YamlConfiguration.loadConfiguration(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));

                fileloop:
                for (String user : config.getKeys(false))
                {
                    List<String> user_ips = (List<String>) config.getStringList(user);
                    for (String ip : user_ips)
                    {
                        ip = ip.toLowerCase().trim();
                        if (ip.equals(match_ip))
                        {
                            TFM_Log.info("New IP '" + user_ip + "' matches old IP '" + match_ip + "' via partial match, adding it to superadmin list.");
                            user_ips.add(user_ip);
                            config.set(user, user_ips);
                            break fileloop;
                        }
                    }
                }

                try
                {
                    config.save(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SUPERADMIN_FILE));
                }
                catch (IOException ex)
                {
                    TFM_Log.severe(ex);
                }
            }

            return match_ip != null;
        }
    }

    public static int wipeEntities(boolean wipe_explosives)
    {
        return wipeEntities(wipe_explosives, false);
    }

    public static int wipeEntities(boolean wipe_explosives, boolean wipe_carts)
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity ent : world.getEntities())
            {
                if (ent instanceof Projectile
                        || ent instanceof Item
                        || ent instanceof ExperienceOrb
                        || (ent instanceof Explosive && wipe_explosives)
                        || (ent instanceof Minecart && wipe_carts))
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

        TFM_Log.info("autoEject -> name: " + p.getName() + " - player_ip: " + player_ip + " - method: " + method.toString());

        p.setOp(false);
        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().clear();

        switch (method)
        {
            case STRIKE_ONE:
            {
                Calendar c = new GregorianCalendar();
                c.add(Calendar.MINUTE, 1);
                Date expires = c.getTime();

                TFM_Util.bcastMsg(ChatColor.RED + p.getName() + " has been banned for 1 minute.");

                TFM_Util.banIP(player_ip, kickMessage, "AutoEject", expires);
                TFM_Util.banUsername(p.getName(), kickMessage, "AutoEject", expires);
                p.kickPlayer(kickMessage);

                break;
            }
            case STRIKE_TWO:
            {
                Calendar c = new GregorianCalendar();
                c.add(Calendar.MINUTE, 3);
                Date expires = c.getTime();

                TFM_Util.bcastMsg(ChatColor.RED + p.getName() + " has been banned for 3 minutes.");

                TFM_Util.banIP(player_ip, kickMessage, "AutoEject", expires);
                TFM_Util.banUsername(p.getName(), kickMessage, "AutoEject", expires);
                p.kickPlayer(kickMessage);

                break;
            }
            case STRIKE_THREE:
            {
                //Bukkit.banIP(player_ip);
                TFM_Util.banIP(player_ip, kickMessage, "AutoEject", null);
                String[] ip_address_parts = player_ip.split("\\.");
                //Bukkit.banIP();
                TFM_Util.banIP(ip_address_parts[0] + "." + ip_address_parts[1] + ".*.*", kickMessage, "AutoEject", null);

                //p.setBanned(true);
                TFM_Util.banUsername(p.getName(), kickMessage, "AutoEject", null);

                TFM_Util.bcastMsg(ChatColor.RED + p.getName() + " has been banned permanently.");

                p.kickPlayer(kickMessage);

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

    public static boolean isSuperadminImpostor(CommandSender user)
    {
        if (!(user instanceof Player))
        {
            return false;
        }

        Player p = (Player) user;

        if (TotalFreedomMod.superadmins.contains(p.getName().toLowerCase()))
        {
            return !TFM_Util.isUserSuperadmin(p);
        }

        return false;
    }

    //JeromSar
    public static String getRank(CommandSender sender)
    {
        if (TFM_Util.isSuperadminImpostor(sender))
        {
            return "an " + ChatColor.YELLOW + ChatColor.UNDERLINE + "impostor" + ChatColor.RESET + ChatColor.AQUA + "!";
        }

        if (sender.getName().equalsIgnoreCase("markbyron"))
        {
            return "the " + ChatColor.LIGHT_PURPLE + "owner" + ChatColor.AQUA + ".";
        }

        if (sender.getName().equalsIgnoreCase("madgeek1450"))
        {
            return "the " + ChatColor.DARK_PURPLE + "developer" + ChatColor.AQUA + ".";
        }
        if (sender.getName().equalsIgnoreCase("darthsalamon"))
        {
            return "a " + ChatColor.DARK_PURPLE + "developer" + ChatColor.AQUA + "!";
        }
        if (sender.getName().equalsIgnoreCase("miwojedk"))
        {
            return "a " + ChatColor.DARK_RED+ "master-builder" + ChatColor.AQUA + "!";
        }

        if (TFM_Util.isUserSuperadmin(sender))
        {
            return "an " + ChatColor.RED + "admin" + ChatColor.AQUA + ".";
        }

        if (sender.isOp())
        {
            return "an " + ChatColor.DARK_GREEN + "OP" + ChatColor.AQUA + ".";
        }

        return "a " + ChatColor.GREEN + "non-OP" + ChatColor.AQUA + ".";
    }

    public static void banUsername(String name, String reason, String source, Date expire_date)
    {
        name = name.toLowerCase().trim();

        BanEntry ban_entry = new BanEntry(name);

        if (expire_date != null)
        {
            ban_entry.setExpires(expire_date);
        }
        if (reason != null)
        {
            ban_entry.setReason(reason);
        }
        if (source != null)
        {
            ban_entry.setSource(source);
        }

        BanList nameBans = MinecraftServer.getServer().getServerConfigurationManager().getNameBans();

        nameBans.add(ban_entry);
    }

    public static void unbanUsername(String name)
    {
        name = name.toLowerCase().trim();

        BanList nameBans = MinecraftServer.getServer().getServerConfigurationManager().getNameBans();

        nameBans.remove(name);
    }

    public static boolean isNameBanned(String name)
    {
        name = name.toLowerCase().trim();
        BanList nameBans = MinecraftServer.getServer().getServerConfigurationManager().getNameBans();
        nameBans.removeExpired();
        return nameBans.getEntries().containsKey(name);
    }

    public static void banIP(String ip, String reason, String source, Date expire_date)
    {
        ip = ip.toLowerCase().trim();

        BanEntry ban_entry = new BanEntry(ip);

        if (expire_date != null)
        {
            ban_entry.setExpires(expire_date);
        }
        if (reason != null)
        {
            ban_entry.setReason(reason);
        }
        if (source != null)
        {
            ban_entry.setSource(source);
        }

        BanList ipBans = MinecraftServer.getServer().getServerConfigurationManager().getIPBans();

        ipBans.add(ban_entry);
    }

    public static void unbanIP(String ip)
    {
        ip = ip.toLowerCase().trim();
        BanList ipBans = MinecraftServer.getServer().getServerConfigurationManager().getIPBans();
        ipBans.remove(ip);
    }

    public static boolean isIPBanned(String ip)
    {
        ip = ip.toLowerCase().trim();
        BanList ipBans = MinecraftServer.getServer().getServerConfigurationManager().getIPBans();
        ipBans.removeExpired();
        return ipBans.getEntries().containsKey(ip);
    }

    public static Date parseDateOffset(String time)
    {
        Pattern timePattern = Pattern.compile(
                "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find())
        {
            if (m.group() == null || m.group().isEmpty())
            {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++)
            {
                if (m.group(i) != null && !m.group(i).isEmpty())
                {
                    found = true;
                    break;
                }
            }
            if (found)
            {
                if (m.group(1) != null && !m.group(1).isEmpty())
                {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty())
                {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty())
                {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty())
                {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty())
                {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty())
                {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty())
                {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!found)
        {
            return null;
        }

        Calendar c = new GregorianCalendar();

        if (years > 0)
        {
            c.add(Calendar.YEAR, years);
        }
        if (months > 0)
        {
            c.add(Calendar.MONTH, months);
        }
        if (weeks > 0)
        {
            c.add(Calendar.WEEK_OF_YEAR, weeks);
        }
        if (days > 0)
        {
            c.add(Calendar.DAY_OF_MONTH, days);
        }
        if (hours > 0)
        {
            c.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes > 0)
        {
            c.add(Calendar.MINUTE, minutes);
        }
        if (seconds > 0)
        {
            c.add(Calendar.SECOND, seconds);
        }

        return c.getTime();
    }
    
    public static String arrayToString(Set<OfflinePlayer> set)
    {
    	String players = "";
        String delim = "";
    	
    	for (OfflinePlayer p : set)
        {
            players += delim;
            players += p.getName();
            delim = ", ";
        }
    	
    	return players;
    }
// I wrote all this before i discovered getTargetBlock >.> - might come in handy some day...
//    public static final double LOOKAT_VIEW_HEIGHT = 1.65;
//    public static final double LOOKAT_STEP_DISTANCE = 0.2;
//
//    public static Location getLookatLocation(Player player)
//    {
//        Location player_loc = player.getLocation();
//
//        Vector player_pos = player_loc.toVector().add(new Vector(0.0, LOOKAT_VIEW_HEIGHT, 0.0));
//        Vector player_dir = player_loc.getDirection().normalize();
//
//        for (double offset = 0.0; offset <= 300.0; offset += LOOKAT_STEP_DISTANCE)
//        {
//            Location check_loc = player_pos.clone().add(player_dir.clone().multiply(offset)).toLocation(player.getWorld());
//
//            if (!check_loc.getBlock().isEmpty())
//            {
//                return check_loc;
//            }
//        }
//
//        return null;
//    }
}
