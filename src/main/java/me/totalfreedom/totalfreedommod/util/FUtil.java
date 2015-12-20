package me.totalfreedom.totalfreedommod.util;

import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.DepreciationAggregator;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.config.FConfig;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import net.pravian.aero.util.Ips;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.FileUtil;

public class FUtil
{

    private static final Map<String, Integer> ejectTracker = new HashMap<String, Integer>();
    public static final Map<String, EntityType> mobtypes = new HashMap<String, EntityType>();
    // See https://github.com/TotalFreedom/License - None of the listed names may be removed.
    public static final List<String> DEVELOPERS = Arrays.asList("Madgeek1450", "Prozza", "DarthSalmon", "AcidicCyanide", "Wild1145", "WickedGamingUK");
    private static final Random RANDOM = new Random();
    public static String DATE_STORAGE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
    public static final Map<String, ChatColor> CHAT_COLOR_NAMES = new HashMap<String, ChatColor>();
    public static final List<ChatColor> CHAT_COLOR_POOL = Arrays.asList(
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

    static
    {
        for (EntityType type : EntityType.values())
        {
            try
            {
                if (DepreciationAggregator.getName_EntityType(type) != null)
                {
                    if (Creature.class.isAssignableFrom(type.getEntityClass()))
                    {
                        mobtypes.put(DepreciationAggregator.getName_EntityType(type).toLowerCase(), type);
                    }
                }
            }
            catch (Exception ex)
            {
            }
        }

        for (ChatColor chatColor : CHAT_COLOR_POOL)
        {
            CHAT_COLOR_NAMES.put(chatColor.name().toLowerCase().replace("_", ""), chatColor);
        }
    }

    private FUtil()
    {
        throw new AssertionError();
    }

    public static void cancel(BukkitTask task)
    {
        if (task == null)
        {
            return;
        }

        try
        {
            task.cancel();
        }
        catch (Exception ex)
        {
        }
    }

    public static void bcastMsg(String message, ChatColor color)
    {
        FLog.info(message, true);

        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.sendMessage((color == null ? "" : color) + message);
        }
    }

    public static void bcastMsg(String message)
    {
        FUtil.bcastMsg(message, null);
    }

    // Still in use by listeners
    public static void playerMsg(CommandSender sender, String message, ChatColor color)
    {
        sender.sendMessage(color + message);
    }

    // Still in use by listeners
    public static void playerMsg(CommandSender sender, String message)
    {
        FUtil.playerMsg(sender, message, ChatColor.GRAY);
    }

    public static void setFlying(Player player, boolean flying)
    {
        player.setAllowFlight(true);
        player.setFlying(flying);
    }

    public static void adminAction(String adminName, String action, boolean isRed)
    {
        FUtil.bcastMsg(adminName + " - " + action, (isRed ? ChatColor.RED : ChatColor.AQUA));
    }

    public static boolean isUniqueId(String uuid)
    {
        try
        {
            UUID.fromString(uuid);
            return true;
        }
        catch (IllegalArgumentException ex)
        {
            return false;
        }
    }

    public static String formatLocation(Location location)
    {
        return String.format("%s: (%d, %d, %d)",
                location.getWorld().getName(),
                Math.round(location.getX()),
                Math.round(location.getY()),
                Math.round(location.getZ()));
    }

    public static String formatPlayer(Player player)
    {
        return player.getName() + " (" + Ips.getIp(player) + ")";
    }

    /**
     * Escapes an IP-address to a config-friendly version.
     *
     * <p>
     * Example:
     * <pre>
     * IpUtils.toEscapedString("192.168.1.192"); // 192_168_1_192
     * </pre></p>
     *
     * @param ip The IP-address to escape.
     * @return The config-friendly IP address.
     * @see #fromEscapedString(String)
     */
    public static String toEscapedString(String ip) // BukkitLib @ https://github.com/Pravian/BukkitLib
    {
        return ip.trim().replaceAll("\\.", "_");
    }

    /**
     * Un-escapes a config-friendly Ipv4-address.
     *
     * <p>
     * Example:
     * <pre>
     * IpUtils.fromEscapedString("192_168_1_192"); // 192.168.1.192
     * </pre></p>
     *
     * @param escapedIp The IP-address to un-escape.
     * @return The config-friendly IP address.
     * @see #toEscapedString(String)
     */
    public static String fromEscapedString(String escapedIp) // BukkitLib @ https://github.com/Pravian/BukkitLib
    {
        return escapedIp.trim().replaceAll("_", "\\.");
    }

    public static void gotoWorld(Player player, String targetWorld)
    {
        if (player == null)
        {
            return;
        }

        if (player.getWorld().getName().equalsIgnoreCase(targetWorld))
        {
            playerMsg(player, "Going to main world.", ChatColor.GRAY);
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            return;
        }

        for (World world : Bukkit.getWorlds())
        {
            if (world.getName().equalsIgnoreCase(targetWorld))
            {
                playerMsg(player, "Going to world: " + targetWorld, ChatColor.GRAY);
                player.teleport(world.getSpawnLocation());
                return;
            }
        }

        playerMsg(player, "World " + targetWorld + " not found.", ChatColor.GRAY);
    }

    public static String decolorize(String string)
    {
        return string.replaceAll("\\u00A7(?=[0-9a-fk-or])", "&");
    }

    public static void setWorldTime(World world, long ticks)
    {
        long time = world.getTime();
        time -= time % 24000;
        world.setTime(time + 24000 + ticks);
    }

    public static void createDefaultConfiguration(final String configFileName)
    {
        final File targetFile = new File(TotalFreedomMod.plugin.getDataFolder(), configFileName);

        if (targetFile.exists())
        {
            return;
        }

        FLog.info("Installing default configuration file template: " + targetFile.getPath());

        try
        {
            final InputStream configFileStream = TotalFreedomMod.plugin.getResource(configFileName);
            FileUtils.copyInputStreamToFile(configFileStream, targetFile);
            configFileStream.close();
        }
        catch (IOException ex)
        {
            FLog.severe(ex);
        }
    }

    public static boolean deleteFolder(final File file)
    {
        if (file.exists() && file.isDirectory())
        {
            return FileUtils.deleteQuietly(file);
        }
        return false;
    }

    public static void deleteCoreDumps()
    {
        final File[] coreDumps = new File(".").listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                return file.getName().startsWith("java.core");
            }
        });

        for (File dump : coreDumps)
        {
            FLog.info("Removing core dump file: " + dump.getName());
            dump.delete();
        }
    }

    public static EntityType getEntityType(String mobname) throws Exception
    {
        mobname = mobname.toLowerCase().trim();

        if (!FUtil.mobtypes.containsKey(mobname))
        {
            throw new Exception();
        }

        return FUtil.mobtypes.get(mobname);
    }

    /**
     * Write the specified InputStream to a file.
     *
     * @param in The InputStream from which to read.
     * @param file The File to write to.
     * @throws IOException
     */
    public static void copy(InputStream in, File file) throws IOException // BukkitLib @ https://github.com/Pravian/BukkitLib
    {
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
        }

        final OutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }

    /**
     * Returns a file at located at the Plugins Data folder.
     *
     * @param plugin The plugin to use
     * @param name The name of the file.
     * @return The requested file.
     */
    public static File getPluginFile(Plugin plugin, String name) // BukkitLib @ https://github.com/Pravian/BukkitLib
    {
        return new File(plugin.getDataFolder(), name);
    }

    public static void autoEject(Player player, String kickMessage)
    {
        EjectMethod method = EjectMethod.STRIKE_ONE;
        final String ip = Ips.getIp(player);

        if (!FUtil.ejectTracker.containsKey(ip))
        {
            FUtil.ejectTracker.put(ip, 0);
        }

        int kicks = FUtil.ejectTracker.get(ip);
        kicks += 1;

        FUtil.ejectTracker.put(ip, kicks);

        if (kicks <= 1)
        {
            method = EjectMethod.STRIKE_ONE;
        }
        else if (kicks == 2)
        {
            method = EjectMethod.STRIKE_TWO;
        }
        else if (kicks >= 3)
        {
            method = EjectMethod.STRIKE_THREE;
        }

        FLog.info("AutoEject -> name: " + player.getName() + " - player ip: " + ip + " - method: " + method.toString());

        player.setOp(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();

        switch (method)
        {
            case STRIKE_ONE:
            {
                final Calendar cal = new GregorianCalendar();
                cal.add(Calendar.MINUTE, 1);
                final Date expires = cal.getTime();

                FUtil.bcastMsg(ChatColor.RED + player.getName() + " has been banned for 1 minute.");

                TotalFreedomMod.plugin.bm.addBan(Ban.forPlayer(player, Bukkit.getConsoleSender(), expires, kickMessage));
                player.kickPlayer(kickMessage);

                break;
            }
            case STRIKE_TWO:
            {
                final Calendar c = new GregorianCalendar();
                c.add(Calendar.MINUTE, 3);
                final Date expires = c.getTime();

                FUtil.bcastMsg(ChatColor.RED + player.getName() + " has been banned for 3 minutes.");

                TotalFreedomMod.plugin.bm.addBan(Ban.forPlayer(player, Bukkit.getConsoleSender(), expires, kickMessage));
                player.kickPlayer(kickMessage);
                break;
            }
            case STRIKE_THREE:
            {
                TotalFreedomMod.plugin.bm.addBan(Ban.forPlayerFuzzy(player, Bukkit.getConsoleSender(), null, kickMessage));

                FUtil.bcastMsg(ChatColor.RED + player.getName() + " has been banned.");

                player.kickPlayer(kickMessage);
                break;
            }
        }
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

        File input = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SAVED_FLAGS_FILENAME);
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
                FLog.severe(ex);
            }
        }

        return flags;
    }

    public static boolean getSavedFlag(String flag) throws Exception
    {
        Boolean flagValue = null;

        Map<String, Boolean> flags = FUtil.getSavedFlags();

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
        Map<String, Boolean> flags = FUtil.getSavedFlags();

        if (flags == null)
        {
            flags = new HashMap<String, Boolean>();
        }

        flags.put(flag, value);

        try
        {
            final FileOutputStream fos = new FileOutputStream(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SAVED_FLAGS_FILENAME));
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(flags);
            oos.close();
            fos.close();
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
    }

    public static void createBackups(String file)
    {
        createBackups(file, false);
    }

    public static void createBackups(String file, boolean onlyWeekly)
    {
        final String save = file.split("\\.")[0];
        final FConfig config = new FConfig(TotalFreedomMod.plugin, "backup/backup.yml", false);
        config.load();

        // Weekly
        if (!config.isInt(save + ".weekly"))
        {
            performBackup(file, "weekly");
            config.set(save + ".weekly", FUtil.getUnixTime());
        }
        else
        {
            int lastBackupWeekly = config.getInt(save + ".weekly");

            if (lastBackupWeekly + 3600 * 24 * 7 < FUtil.getUnixTime())
            {
                performBackup(file, "weekly");
                config.set(save + ".weekly", FUtil.getUnixTime());
            }
        }

        if (onlyWeekly)
        {
            config.save();
            return;
        }

        // Daily
        if (!config.isInt(save + ".daily"))
        {
            performBackup(file, "daily");
            config.set(save + ".daily", FUtil.getUnixTime());
        }
        else
        {
            int lastBackupDaily = config.getInt(save + ".daily");

            if (lastBackupDaily + 3600 * 24 < FUtil.getUnixTime())
            {
                performBackup(file, "daily");
                config.set(save + ".daily", FUtil.getUnixTime());
            }
        }

        config.save();
    }

    private static void performBackup(String file, String type)
    {
        FLog.info("Backing up " + file + " to " + file + "." + type + ".bak");
        final File backupFolder = new File(TotalFreedomMod.plugin.getDataFolder(), "backup");

        if (!backupFolder.exists())
        {
            backupFolder.mkdirs();
        }

        final File oldYaml = new File(TotalFreedomMod.plugin.getDataFolder(), file);
        final File newYaml = new File(backupFolder, file + "." + type + ".bak");
        FileUtil.copy(oldYaml, newYaml);
    }

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
        return ((List<String>) ConfigEntry.HOST_SENDER_NAMES.getList()).contains(senderName.toLowerCase());
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

    public static String getFuzzyIp(String ip)
    {
        final String[] ipParts = ip.split("\\.");
        if (ipParts.length == 4)
        {
            return String.format("%s.%s.*.*", ipParts[0], ipParts[1]);
        }

        return ip;
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
        final URL website = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(output);
        fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        fos.close();

        if (verbose)
        {
            FLog.info("Downloaded " + url + " to " + output.toString() + ".");
        }
    }

    public static void adminChatMessage(CommandSender sender, String message, boolean senderIsConsole)
    {
        String name = sender.getName() + " " + TotalFreedomMod.plugin.rm.getDisplayRank(sender).getColoredTag() + ChatColor.WHITE;
        FLog.info("[ADMIN] " + name + ": " + message);

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (TotalFreedomMod.plugin.al.isAdmin(player))
            {
                player.sendMessage("[" + ChatColor.AQUA + "ADMIN" + ChatColor.WHITE + "] " + ChatColor.DARK_RED + name + ": " + ChatColor.AQUA + message);
            }
        }
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
        } while (checkClass.getSuperclass() != Object.class
                && ((checkClass = checkClass.getSuperclass()) != null));

        return null;
    }

    public static ChatColor randomChatColor()
    {
        return CHAT_COLOR_POOL.get(RANDOM.nextInt(CHAT_COLOR_POOL.size()));
    }

    public static String colorize(String string)
    {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static Date getUnixDate(long unix)
    {
        return new Date(unix * 1000);
    }

    public static long getUnixTime()
    {
        return System.currentTimeMillis() / 1000L;
    }

    public static long getUnixTime(Date date)
    {
        if (date == null)
        {
            return 0;
        }

        return date.getTime() / 1000L;
    }

    public static String getNmsVersion()
    {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);

    }

    public static void reportAction(Player reporter, Player reported, String report)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (TotalFreedomMod.plugin.al.isAdmin(player))
            {
                playerMsg(player, ChatColor.RED + "[REPORTS] " + ChatColor.GOLD + reporter.getName() + " has reported " + reported.getName() + " for " + report);
            }
        }
    }

    public static enum EjectMethod
    {

        STRIKE_ONE, STRIKE_TWO, STRIKE_THREE;
    }

    public static class MethodTimer
    {

        private long lastStart;
        private long total = 0;

        public MethodTimer()
        {
        }

        public void start()
        {
            this.lastStart = System.currentTimeMillis();
        }

        public void update()
        {
            this.total += (System.currentTimeMillis() - this.lastStart);
        }

        public long getTotal()
        {
            return this.total;
        }

        public void printTotalToLog(String timerName)
        {
            FLog.info("DEBUG: " + timerName + " used " + this.getTotal() + " ms.");
        }
    }

}
