package me.StevenLawson.TotalFreedomMod;

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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.FileUtil;

public class TFM_Util
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
                if (TFM_DepreciationAggregator.getName_EntityType(type) != null)
                {
                    if (Creature.class.isAssignableFrom(type.getEntityClass()))
                    {
                        mobtypes.put(TFM_DepreciationAggregator.getName_EntityType(type).toLowerCase(), type);
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

    public static void setFlying(Player player, boolean flying)
    {
        player.setAllowFlight(true);
        player.setFlying(flying);
    }

    public static void adminAction(String adminName, String action, boolean isRed)
    {
        TFM_Util.bcastMsg(adminName + " - " + action, (isRed ? ChatColor.RED : ChatColor.AQUA));
    }

    public static String getIp(OfflinePlayer player)
    {
        if (player.isOnline())
        {
            return player.getPlayer().getAddress().getAddress().getHostAddress().trim();
        }

        final TFM_Player entry = TFM_PlayerList.getEntry(TFM_UuidManager.getUniqueId(player));

        return (entry == null ? null : entry.getIps().get(0));
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

    public static String formatPlayer(OfflinePlayer player)
    {
        return player.getName() + " (" + TFM_UuidManager.getUniqueId(player) + ")";
    }

    /**
     * Escapes an IP-address to a config-friendly version.
     *
     * <p>Example:
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
     * <p>Example:
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

    public static void buildHistory(Location location, int length, TFM_PlayerData playerdata)
    {
        final Block center = location.getBlock();
        for (int xOffset = -length; xOffset <= length; xOffset++)
        {
            for (int yOffset = -length; yOffset <= length; yOffset++)
            {
                for (int zOffset = -length; zOffset <= length; zOffset++)
                {
                    final Block block = center.getRelative(xOffset, yOffset, zOffset);
                    playerdata.insertHistoryBlock(block.getLocation(), block.getType());
                }
            }
        }
    }

    public static void generateCube(Location location, int length, Material material)
    {
        final Block center = location.getBlock();
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
        final Block center = location.getBlock();
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
                        final Skull skull = (Skull) block.getState();
                        skull.setSkullType(SkullType.PLAYER);
                        skull.setOwner("Prozza");
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

    public static void createDefaultConfiguration(final String configFileName)
    {
        final File targetFile = new File(TotalFreedomMod.plugin.getDataFolder(), configFileName);

        if (targetFile.exists())
        {
            return;
        }

        TFM_Log.info("Installing default configuration file template: " + targetFile.getPath());

        try
        {
            final InputStream configFileStream = TotalFreedomMod.plugin.getResource(configFileName);
            FileUtils.copyInputStreamToFile(configFileStream, targetFile);
            configFileStream.close();
        }
        catch (IOException ex)
        {
            TFM_Log.severe(ex);
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
            TFM_Log.info("Removing core dump file: " + dump.getName());
            dump.delete();
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
    public static File getPluginFile(Plugin plugin, String name)  // BukkitLib @ https://github.com/Pravian/BukkitLib
    {
        return new File(plugin.getDataFolder(), name);
    }

    public static void autoEject(Player player, String kickMessage)
    {
        EjectMethod method = EjectMethod.STRIKE_ONE;
        final String ip = TFM_Util.getIp(player);

        if (!TFM_Util.ejectTracker.containsKey(ip))
        {
            TFM_Util.ejectTracker.put(ip, 0);
        }

        int kicks = TFM_Util.ejectTracker.get(ip);
        kicks += 1;

        TFM_Util.ejectTracker.put(ip, kicks);

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

        TFM_Log.info("AutoEject -> name: " + player.getName() + " - player ip: " + ip + " - method: " + method.toString());

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

                TFM_Util.bcastMsg(ChatColor.RED + player.getName() + " has been banned for 1 minute.");

                TFM_BanManager.addIpBan(new TFM_Ban(ip, player.getName(), "AutoEject", expires, kickMessage));
                TFM_BanManager.addUuidBan(new TFM_Ban(TFM_UuidManager.getUniqueId(player), player.getName(), "AutoEject", expires, kickMessage));
                player.kickPlayer(kickMessage);

                break;
            }
            case STRIKE_TWO:
            {
                final Calendar c = new GregorianCalendar();
                c.add(Calendar.MINUTE, 3);
                final Date expires = c.getTime();

                TFM_Util.bcastMsg(ChatColor.RED + player.getName() + " has been banned for 3 minutes.");

                TFM_BanManager.addIpBan(new TFM_Ban(ip, player.getName(), "AutoEject", expires, kickMessage));
                TFM_BanManager.addUuidBan(new TFM_Ban(TFM_UuidManager.getUniqueId(player), player.getName(), "AutoEject", expires, kickMessage));
                player.kickPlayer(kickMessage);
                break;
            }
            case STRIKE_THREE:
            {
                String[] ipAddressParts = ip.split("\\.");

                TFM_BanManager.addIpBan(new TFM_Ban(ip, player.getName(), "AutoEject", null, kickMessage));
                TFM_BanManager.addIpBan(new TFM_Ban(ipAddressParts[0] + "." + ipAddressParts[1] + ".*.*", player.getName(), "AutoEject", null, kickMessage));
                TFM_BanManager.addUuidBan(new TFM_Ban(TFM_UuidManager.getUniqueId(player), player.getName(), "AutoEject", null, kickMessage));

                TFM_Util.bcastMsg(ChatColor.RED + player.getName() + " has been banned.");

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
            final FileOutputStream fos = new FileOutputStream(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.SAVED_FLAGS_FILENAME));
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(flags);
            oos.close();
            fos.close();
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static void createBackups(String file)
    {
        createBackups(file, false);
    }

    public static void createBackups(String file, boolean onlyWeekly)
    {
        final String save = file.split("\\.")[0];
        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, "backup/backup.yml", false);
        config.load();

        // Weekly
        if (!config.isInt(save + ".weekly"))
        {
            performBackup(file, "weekly");
            config.set(save + ".weekly", TFM_Util.getUnixTime());
        }
        else
        {
            int lastBackupWeekly = config.getInt(save + ".weekly");

            if (lastBackupWeekly + 3600 * 24 * 7 < TFM_Util.getUnixTime())
            {
                performBackup(file, "weekly");
                config.set(save + ".weekly", TFM_Util.getUnixTime());
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
            config.set(save + ".daily", TFM_Util.getUnixTime());
        }
        else
        {
            int lastBackupDaily = config.getInt(save + ".daily");

            if (lastBackupDaily + 3600 * 24 < TFM_Util.getUnixTime())
            {
                performBackup(file, "daily");
                config.set(save + ".daily", TFM_Util.getUnixTime());
            }
        }

        config.save();
    }

    private static void performBackup(String file, String type)
    {
        TFM_Log.info("Backing up " + file + " to " + file + "." + type + ".bak");
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
            TFM_Log.info("Downloaded " + url + " to " + output.toString() + ".");
        }
    }

    public static void adminChatMessage(CommandSender sender, String message, boolean senderIsConsole)
    {
        String name = sender.getName() + " " + TFM_PlayerRank.fromSender(sender).getPrefix() + ChatColor.WHITE;
        TFM_Log.info("[ADMIN] " + name + ": " + message);

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (TFM_AdminList.isSuperAdmin(player))
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
        }
        while (checkClass.getSuperclass() != Object.class
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

    public static long getUnixTime()
    {
        return System.currentTimeMillis() / 1000L;
    }

    public static Date getUnixDate(long unix)
    {
        return new Date(unix * 1000);
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
            if (TFM_AdminList.isSuperAdmin(player))
            {
                playerMsg(player, ChatColor.RED + "[REPORTS] " + ChatColor.GOLD + reporter.getName() + " has reported " + reported.getName() + " for " + report);
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
            TFM_Log.info("DEBUG: " + timerName + " used " + this.getTotal() + " ms.");
        }
    }
}
