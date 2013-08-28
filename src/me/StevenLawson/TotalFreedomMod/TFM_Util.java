package me.StevenLawson.TotalFreedomMod;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

public class TFM_Util
{
    private static final Map<String, Integer> ejectTracker = new HashMap<String, Integer>();
    public static final Map<String, EntityType> mobtypes = new HashMap<String, EntityType>();
    public static final List<String> STOP_COMMANDS = Arrays.asList("stop", "off", "end", "halt", "die");
    public static final List<String> REMOVE_COMMANDS = Arrays.asList("del", "delete", "rem", "remove");
    public static final List<String> DEVELOPERS = Arrays.asList("Madgeek1450", "DarthSalamon", "AcidicCyanide", "wild1145", "HeXeRei452");

    static
    {
        for (EntityType type : EntityType.values())
        {
            try
            {
                if (type.getName() != null)
                {
                    if (Creature.class.isAssignableFrom(type.getEntityClass()))
                    {
                        mobtypes.put(type.getName().toLowerCase(), type);
                    }
                }
            }
            catch (Exception ex)
            {
            }
        }
    }

    private TFM_Util()
    {
        throw new AssertionError();
    }

    public static void bcastMsg(String message, ChatColor color)
    {
        TFM_Log.info(message, true);

        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.sendMessage((color == null ? "" : color) + message);
        }
    }

    public static void bcastMsg(String message)
    {
        TFM_Util.bcastMsg(message, null);
    }

    // Still in use by listeners
    public static void playerMsg(CommandSender sender, String message, ChatColor color)
    {
        sender.sendMessage(color + message);
    }

    // Still in use by listeners
    public static void playerMsg(CommandSender sender, String message)
    {
        TFM_Util.playerMsg(sender, message, ChatColor.GRAY);
    }

    public static void adminAction(String adminName, String action, boolean isRed)
    {
        TFM_Util.bcastMsg(adminName + " - " + action, (isRed ? ChatColor.RED : ChatColor.AQUA));
    }

    public static String formatLocation(Location location)
    {
        return String.format("%s: (%d, %d, %d)",
                location.getWorld().getName(),
                Math.round(location.getX()),
                Math.round(location.getY()),
                Math.round(location.getZ()));
    }

    public static void gotoWorld(CommandSender sender, String targetworld)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if (player.getWorld().getName().equalsIgnoreCase(targetworld))
            {
                sender.sendMessage(ChatColor.GRAY + "Going to main world.");
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                return;
            }

            for (World world : Bukkit.getWorlds())
            {
                if (world.getName().equalsIgnoreCase(targetworld))
                {
                    sender.sendMessage(ChatColor.GRAY + "Going to world: " + targetworld);
                    player.teleport(world.getSpawnLocation());
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

    public static void buildHistory(Location location, int length, TFM_PlayerData playerdata)
    {
        Block center = location.getBlock();
        for (int xOffset = -length; xOffset <= length; xOffset++)
        {
            for (int yOffset = -length; yOffset <= length; yOffset++)
            {
                for (int zOffset = -length; zOffset <= length; zOffset++)
                {
                    Block block = center.getRelative(xOffset, yOffset, zOffset);
                    playerdata.insertHistoryBlock(block.getLocation(), block.getType());
                }
            }
        }
    }

    public static void generateCube(Location location, int length, Material material)
    {
        Block center = location.getBlock();
        for (int xOffset = -length; xOffset <= length; xOffset++)
        {
            for (int yOffset = -length; yOffset <= length; yOffset++)
            {
                for (int zOffset = -length; zOffset <= length; zOffset++)
                {
                    final Block block = center.getRelative(xOffset, yOffset, zOffset);
                    if (block.getType() != material)
                    {
                        block.setType(material);
                    }
                }
            }
        }
    }

    public static void generateHollowCube(Location location, int length, Material material)
    {
        Block center = location.getBlock();
        for (int xOffset = -length; xOffset <= length; xOffset++)
        {
            for (int yOffset = -length; yOffset <= length; yOffset++)
            {
                for (int zOffset = -length; zOffset <= length; zOffset++)
                {
                    // Hollow
                    if (Math.abs(xOffset) != length && Math.abs(yOffset) != length && Math.abs(zOffset) != length)
                    {
                        continue;
                    }

                    final Block block = center.getRelative(xOffset, yOffset, zOffset);

                    if (material != Material.SKULL)
                    {
                        // Glowstone light
                        if (material != Material.GLASS && xOffset == 0 && yOffset == 2 && zOffset == 0)
                        {
                            block.setType(Material.GLOWSTONE);
                            continue;
                        }

                        block.setType(material);
                    }
                    else // Darth mode
                    {
                        if (Math.abs(xOffset) == length && Math.abs(yOffset) == length && Math.abs(zOffset) == length)
                        {
                            block.setType(Material.GLOWSTONE);
                            continue;
                        }

                        block.setType(Material.SKULL);
                        Skull skull = (Skull) block.getState();
                        skull.setSkullType(SkullType.PLAYER);
                        skull.setOwner("DarthSalamon");
                        skull.update();
                    }
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

    public static void createDefaultConfiguration(String name, File pluginFile)
    {
        TotalFreedomMod tfm = TotalFreedomMod.plugin;

        File actual = new File(tfm.getDataFolder(), name);
        if (!actual.exists())
        {
            TFM_Log.info("Installing default configuration file template: " + actual.getPath());
            InputStream input = null;
            try
            {
                JarFile file = new JarFile(pluginFile);
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

    public static class TFM_EntityWiper
    {
        private static final List<Class<? extends Entity>> WIPEABLES = new ArrayList<Class<? extends Entity>>();

        static
        {
            WIPEABLES.add(EnderCrystal.class);
            WIPEABLES.add(EnderSignal.class);
            WIPEABLES.add(ExperienceOrb.class);
            WIPEABLES.add(Projectile.class);
            WIPEABLES.add(FallingBlock.class);
            WIPEABLES.add(Firework.class);
            WIPEABLES.add(Item.class);
        }

        private TFM_EntityWiper()
        {
            throw new AssertionError();
        }

        private static boolean canWipe(Entity entity, boolean wipeExplosives, boolean wipeVehicles)
        {
            if (wipeExplosives)
            {
                if (Explosive.class.isAssignableFrom(entity.getClass()))
                {
                    return true;
                }
            }

            if (wipeVehicles)
            {
                if (Boat.class.isAssignableFrom(entity.getClass()))
                {
                    return true;
                }
                else if (Minecart.class.isAssignableFrom(entity.getClass()))
                {
                    return true;
                }
            }

            Iterator<Class<? extends Entity>> it = WIPEABLES.iterator();
            while (it.hasNext())
            {
                if (it.next().isAssignableFrom(entity.getClass()))
                {
                    return true;
                }
            }

            return false;
        }

        public static int wipeEntities(boolean wipeExplosives, boolean wipeVehicles)
        {
            int removed = 0;

            Iterator<World> worlds = Bukkit.getWorlds().iterator();
            while (worlds.hasNext())
            {
                Iterator<Entity> entities = worlds.next().getEntities().iterator();
                while (entities.hasNext())
                {
                    Entity entity = entities.next();
                    if (canWipe(entity, wipeExplosives, wipeVehicles))
                    {
                        entity.remove();
                        removed++;
                    }
                }
            }

            return removed;
        }
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
        return STOP_COMMANDS.contains(command.toLowerCase());
    }

    public static boolean isRemoveCommand(String command)
    {
        return REMOVE_COMMANDS.contains(command.toLowerCase());
    }

    enum EjectMethod
    {
        STRIKE_ONE, STRIKE_TWO, STRIKE_THREE;
    }

    public static void autoEject(Player player, String kickMessage)
    {
        EjectMethod method = EjectMethod.STRIKE_ONE;
        String ip = null;

        try
        {
            ip = player.getAddress().getAddress().getHostAddress();

            Integer kicks = TFM_Util.ejectTracker.get(ip);
            if (kicks == null)
            {
                kicks = new Integer(0);
            }

            kicks = new Integer(kicks.intValue() + 1);

            TFM_Util.ejectTracker.put(ip, kicks);

            if (kicks.intValue() <= 1)
            {
                method = EjectMethod.STRIKE_ONE;
            }
            else if (kicks.intValue() == 2)
            {
                method = EjectMethod.STRIKE_TWO;
            }
            else if (kicks.intValue() >= 3)
            {
                method = EjectMethod.STRIKE_THREE;
            }
        }
        catch (Exception ex)
        {
        }

        TFM_Log.info("autoEject -> name: " + player.getName() + " - player ip: " + ip + " - method: " + method.toString());

        player.setOp(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();

        switch (method)
        {
            case STRIKE_ONE:
            {
                Calendar c = new GregorianCalendar();
                c.add(Calendar.MINUTE, 1);
                Date expires = c.getTime();

                TFM_Util.bcastMsg(ChatColor.RED + player.getName() + " has been banned for 1 minute.");

                TFM_ServerInterface.banIP(ip, kickMessage, "AutoEject", expires);
                TFM_ServerInterface.banUsername(player.getName(), kickMessage, "AutoEject", expires);
                player.kickPlayer(kickMessage);

                break;
            }
            case STRIKE_TWO:
            {
                Calendar c = new GregorianCalendar();
                c.add(Calendar.MINUTE, 3);
                Date expires = c.getTime();

                TFM_Util.bcastMsg(ChatColor.RED + player.getName() + " has been banned for 3 minutes.");

                TFM_ServerInterface.banIP(ip, kickMessage, "AutoEject", expires);
                TFM_ServerInterface.banUsername(player.getName(), kickMessage, "AutoEject", expires);
                player.kickPlayer(kickMessage);

                break;
            }
            case STRIKE_THREE:
            {
                //Bukkit.banIP(player_ip);
                TFM_ServerInterface.banIP(ip, kickMessage, "AutoEject", null);
                String[] ipAddressParts = ip.split("\\.");
                //Bukkit.banIP();
                TFM_ServerInterface.banIP(ipAddressParts[0] + "." + ipAddressParts[1] + ".*.*", kickMessage, "AutoEject", null);

                //p.setBanned(true);
                TFM_ServerInterface.banUsername(player.getName(), kickMessage, "AutoEject", null);

                TFM_Util.bcastMsg(ChatColor.RED + player.getName() + " has been banned permanently.");

                player.kickPlayer(kickMessage);

                break;
            }
        }
    }

    public static String getRank(CommandSender sender)
    {
        if (TFM_SuperadminList.isSuperadminImpostor(sender))
        {
            return "an " + ChatColor.YELLOW + ChatColor.UNDERLINE + "impostor" + ChatColor.RESET + ChatColor.AQUA + "!";
        }

        TFM_Superadmin entry = TFM_SuperadminList.getAdminEntry(sender.getName());

        if (entry != null)
        {
            if (entry.isActivated())
            {
                String loginMessage = entry.getCustomLoginMessage();

                if (loginMessage != null)
                {
                    if (!loginMessage.isEmpty())
                    {
                        return ChatColor.translateAlternateColorCodes('&', loginMessage);
                    }
                }

                if (entry.isSeniorAdmin())
                {
                    return "a " + ChatColor.LIGHT_PURPLE + "Senior Admin" + ChatColor.AQUA + ".";
                }
                else
                {
                    return "a " + ChatColor.GOLD + "Super Admin" + ChatColor.AQUA + ".";
                }
            }
        }

        if (sender.isOp())
        {
            return "an " + ChatColor.DARK_GREEN + "OP" + ChatColor.AQUA + ".";
        }

        return "a " + ChatColor.GREEN + "non-OP" + ChatColor.AQUA + ".";
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

    public static String playerListToNames(Set<OfflinePlayer> players)
    {
        List<String> names = new ArrayList<String>();
        for (OfflinePlayer player : players)
        {
            names.add(player.getName());
        }
        return StringUtils.join(names, ", ");
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Boolean> getSavedFlags()
    {
        Map<String, Boolean> flags = null;

        File input = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SAVED_FLAGS_FILE);
        if (input.exists())
        {
            try
            {
                FileInputStream fis = new FileInputStream(input);
                ObjectInputStream ois = new ObjectInputStream(fis);
                flags = (HashMap<String, Boolean>) ois.readObject();
                ois.close();
                fis.close();
            }
            catch (Exception ex)
            {
                TFM_Log.severe(ex);
            }
        }

        return flags;
    }

    public static boolean getSavedFlag(String flag) throws Exception
    {
        Boolean flagValue = null;

        Map<String, Boolean> flags = TFM_Util.getSavedFlags();

        if (flags != null)
        {
            if (flags.containsKey(flag))
            {
                flagValue = flags.get(flag);
            }
        }

        if (flagValue != null)
        {
            return flagValue.booleanValue();
        }
        else
        {
            throw new Exception();
        }
    }

    public static void setSavedFlag(String flag, boolean value)
    {
        Map<String, Boolean> flags = TFM_Util.getSavedFlags();

        if (flags == null)
        {
            flags = new HashMap<String, Boolean>();
        }

        flags.put(flag, value);

        try
        {
            FileOutputStream fos = new FileOutputStream(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SAVED_FLAGS_FILE));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(flags);
            oos.close();
            fos.close();
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }
    public static String DATE_STORAGE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

    public static String dateToString(Date date)
    {
        return new SimpleDateFormat(DATE_STORAGE_FORMAT, Locale.ENGLISH).format(date);
    }

    public static Date stringToDate(String dateString)
    {
        try
        {
            return new SimpleDateFormat(DATE_STORAGE_FORMAT, Locale.ENGLISH).parse(dateString);
        }
        catch (ParseException pex)
        {
            return new Date(0L);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean isFromHostConsole(String senderName)
    {
        return ((List<String>) TFM_ConfigEntry.HOST_SENDER_NAMES.getList()).contains(senderName.toLowerCase());
    }

    public static List<String> removeDuplicates(List<String> oldList)
    {
        List<String> newList = new ArrayList<String>();
        for (String entry : oldList)
        {
            if (!newList.contains(entry))
            {
                newList.add(entry);
            }
        }
        return newList;
    }

    public static boolean fuzzyIpMatch(String a, String b, int octets)
    {
        boolean match = true;

        String[] aParts = a.split("\\.");
        String[] bParts = b.split("\\.");

        if (aParts.length != 4 || bParts.length != 4)
        {
            return false;
        }

        if (octets > 4)
        {
            octets = 4;
        }
        else if (octets < 1)
        {
            octets = 1;
        }

        for (int i = 0; i < octets && i < 4; i++)
        {
            if (aParts[i].equals("*") || bParts[i].equals("*"))
            {
                continue;
            }

            if (!aParts[i].equals(bParts[i]))
            {
                match = false;
                break;
            }
        }

        return match;
    }

    public static int replaceBlocks(Location center, Material fromMaterial, Material toMaterial, int radius)
    {
        int affected = 0;

        Block centerBlock = center.getBlock();
        for (int xOffset = -radius; xOffset <= radius; xOffset++)
        {
            for (int yOffset = -radius; yOffset <= radius; yOffset++)
            {
                for (int zOffset = -radius; zOffset <= radius; zOffset++)
                {
                    Block block = centerBlock.getRelative(xOffset, yOffset, zOffset);

                    if (block.getType().equals(fromMaterial))
                    {
                        if (block.getLocation().distanceSquared(center) < (radius * radius))
                        {
                            block.setType(toMaterial);
                            affected++;
                        }
                    }
                }
            }
        }

        return affected;
    }

    public static void downloadFile(String url, File output) throws java.lang.Exception
    {
        downloadFile(url, output, false);
    }

    public static void downloadFile(String url, File output, boolean verbose) throws java.lang.Exception
    {
        URL website = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(output);
        fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        fos.close();

        if (verbose)
        {
            TFM_Log.info("Downloaded " + url + " to " + output.toString() + ".");
        }
    }

    public static void adminChatMessage(CommandSender sender, String message, boolean senderIsConsole)
    {
        String name = sender.getName() + " " + getPrefix(sender, senderIsConsole);
        TFM_Log.info("[ADMIN] " + name + ": " + message);

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (TFM_SuperadminList.isUserSuperadmin(player))
            {
                player.sendMessage("[" + ChatColor.AQUA + "ADMIN" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + name + ": " + ChatColor.AQUA + message);
            }
        }
    }

    public static String getPrefix(CommandSender sender, boolean senderIsConsole)
    {
        String prefix;
        if (senderIsConsole)
        {
            prefix = ChatColor.BLUE + "(Console)";
        }
        else
        {
            if (TFM_SuperadminList.isSeniorAdmin(sender))
            {
                prefix = ChatColor.LIGHT_PURPLE + "(SrA)";
            }
            else
            {
                prefix = ChatColor.GOLD + "(SA)";
            }
            if (DEVELOPERS.contains(sender.getName()))
            {
                prefix = ChatColor.DARK_PURPLE + "(Dev)";
            }
        }
        return prefix + ChatColor.WHITE;
    }

    public static String inputStreamToString(InputStream is, boolean preserveNewlines) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
        {
            sb.append(line).append(preserveNewlines ? System.getProperty("line.separator") : "");
        }
        return sb.toString();
    }

    //getField: Borrowed from WorldEdit
    @SuppressWarnings("unchecked")
    public static <T> T getField(Object from, String name)
    {
        Class<?> checkClass = from.getClass();
        do
        {
            try
            {
                Field field = checkClass.getDeclaredField(name);
                field.setAccessible(true);
                return (T) field.get(from);
            }
            catch (NoSuchFieldException ex)
            {
            }
            catch (IllegalAccessException ex)
            {
            }
        }
        while (checkClass.getSuperclass() != Object.class && ((checkClass = checkClass.getSuperclass()) != null));
        return null;
    }
    public static final List<ChatColor> COLOR_POOL = Arrays.asList(
            ChatColor.DARK_BLUE,
            ChatColor.DARK_GREEN,
            ChatColor.DARK_AQUA,
            ChatColor.DARK_RED,
            ChatColor.DARK_PURPLE,
            ChatColor.GOLD,
            ChatColor.BLUE,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.RED,
            ChatColor.LIGHT_PURPLE,
            ChatColor.YELLOW);
    private static final Random RANDOM = new Random();

    public static ChatColor randomChatColor()
    {
        return COLOR_POOL.get(RANDOM.nextInt(COLOR_POOL.size()));
    }

    public static String colorise(String string)
    {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
