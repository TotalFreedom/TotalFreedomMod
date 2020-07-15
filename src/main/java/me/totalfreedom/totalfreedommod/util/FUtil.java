package me.totalfreedom.totalfreedommod.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
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
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;

public class FUtil
{

    private static final Random RANDOM = new Random();
    //
    public static final String SAVED_FLAGS_FILENAME = "savedflags.dat";
    // See https://github.com/TotalFreedom/License - None of the listed names may be removed.
    public static final List<String> DEVELOPERS = Arrays.asList("Madgeek1450", "Prozza", "WickedGamingUK", "Wild1145", "aggelosQQ", "scripthead", "supernt");
    public static String DATE_STORAGE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
    public static final Map<String, ChatColor> CHAT_COLOR_NAMES = new HashMap<>();
    public static final List<ChatColor> CHAT_COLOR_POOL = Arrays.asList(
            ChatColor.DARK_RED,
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.DARK_GREEN,
            ChatColor.AQUA,
            ChatColor.DARK_AQUA,
            ChatColor.BLUE,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_PURPLE,
            ChatColor.LIGHT_PURPLE);
    private static Iterator<ChatColor> CHAT_COLOR_ITERATOR;
    private static String CHARACTER_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static Map<Integer, String> TIMEZONE_LOOKUP = new HashMap<>();

    static
    {
        for (ChatColor chatColor : CHAT_COLOR_POOL)
        {
            CHAT_COLOR_NAMES.put(chatColor.name().toLowerCase().replace("_", ""), chatColor);
        }

        for (int i = -12; i <= 12; i++)
        {
            String sec = String.valueOf(i).replace("-", "");
            if (i > -10 && i < 10)
            {
                sec = "0" + sec;
            }
            if (i >= 0)
            {
                sec = "+" + sec;
            }
            else
            {
                sec = "-" + sec;
            }
            TIMEZONE_LOOKUP.put(i, "GMT" + sec + ":00");
        }
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

    public static boolean isExecutive(String name)
    {
        return ConfigEntry.SERVER_OWNERS.getStringList().contains(name) || ConfigEntry.SERVER_EXECUTIVES.getStringList().contains(name) || ConfigEntry.SERVER_ASSISTANT_EXECUTIVES.getStringList().contains(name);
    }
    
    public static boolean isDeveloper(String name)
    {
        return FUtil.DEVELOPERS.contains(name);
    }

    public static String formatName(String name)
    {
        return WordUtils.capitalizeFully(name.replace("_", " "));
    }

    public static String showS(int count)
    {
        if (count == 1)
        {
            return "";
        }
        return "s";
    }

    public static List<String> getPlayerList()
    {
        List<String> names = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (!TotalFreedomMod.plugin().al.vanished.contains(player))
            {
                names.add(player.getName());
            }
        }
        return names;
    }

    public static String listToString(List<String> list)
    {
        if (list.size() == 0)
        {
            return null;
        }

        return String.join(", ", list);
    }

    public static List<String> stringToList(String string)
    {
        if (string == null)
        {
            return new ArrayList<>();
        }

        return Arrays.asList(string.split(", "));
    }

    /**
     * A way to get a sublist with a page index and a page size.
     * @param list A list of objects that should be split into pages.
     * @param size The size of the pages.
     * @param index The page index, if outside of bounds error will be thrown. The page index starts at 0 as with all lists.
     * @return A list of objects that is the page that has been selected from the previous last parameter.
     */
    public static List<String> getPageFromList(List<String> list, int size, int index)
    {
        try
        {
            if (size >= list.size())
            {
                return list;
            }
            else if (size * (index + 1) <= list.size())
            {
                return list.subList(size * index, size * (index + 1));
            }
            else
            {
                return list.subList(size * index, (size * index) + (list.size() % size));
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            return new ArrayList<>();
        }
    }

    public static List<String> getAllMaterialNames()
    {
        List<String> names = new ArrayList<>();
        for (Material material : Material.values())
        {
            names.add(material.name());
        }
        return names;
    }

    public static UUID nameToUUID(String name)
    {
        try
        {
            JSONArray json = new JSONArray();
            json.add(name);
            List<String> headers = new ArrayList<>();
            headers.add("Accept:application/json");
            headers.add("Content-Type:application/json");
            String response = postRequestToEndpoint("https://api.mojang.com/profiles/minecraft", "POST", headers, json.toString());
            // Don't care how stupid this looks, couldn't find anything to parse a json string to something readable in java with something not horrendously huge, maybe im just retarded
            Pattern pattern = Pattern.compile("(?<=\"id\":\")[a-f0-9].{31}");
            Matcher matcher = pattern.matcher(response);
            if (matcher.find())
            {
                String rawUUID = matcher.group(0).replaceFirst("([a-f0-9]{8})([a-f0-9]{4})([a-f0-9]{4})([a-f0-9]{4})([a-f0-9]+)", "$1-$2-$3-$4-$5");
                return UUID.fromString(rawUUID);
            }
        }
        catch (Exception e)
        {
            FLog.severe("Failed to convert name to UUID:\n" + e.toString());
        }
        return null;
    }

    public static String postRequestToEndpoint(String endpoint, String method, List<String>headers, String body) throws IOException
    {
        URL url = new URL(endpoint);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestMethod(method);
        for (String header :  headers)
        {
            String[] kv = header.split(":");
            connection.setRequestProperty(kv[0], kv[1]);
        }
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(body);
        outputStream.flush();
        outputStream.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }

        in.close();
        return response.toString();
    }

    public static void bcastMsg(String message, ChatColor color)
    {
        bcastMsg(message, color, true);
    }

    public static void bcastMsg(String message, ChatColor color, Boolean toConsole)
    {
        if (toConsole)
        {
            FLog.info(message, true);
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.sendMessage((color == null ? "" : color) + message);
        }
    }

    public static void bcastMsg(String message, Boolean toConsole)
    {
        bcastMsg(message, null, toConsole);
    }

    public static void bcastMsg(String message)
    {
        FUtil.bcastMsg(message, null, true);
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

    public static String formatLocation(Location location)
    {
        return String.format("%s: (%d, %d, %d)",
                location.getWorld().getName(),
                Math.round(location.getX()),
                Math.round(location.getY()),
                Math.round(location.getZ()));
    }

    public static boolean deleteFolder(File file)
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
        List<String> names = new ArrayList<>();
        for (OfflinePlayer player : players)
        {
            names.add(player.getName());
        }
        return StringUtils.join(names, ", ");
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

    public static boolean isFromHostConsole(String senderName)
    {
        return ConfigEntry.HOST_SENDER_NAMES.getList().contains(senderName.toLowerCase());
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
                return (T)field.get(from);

            }
            catch (NoSuchFieldException | IllegalAccessException ex)
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

    public static String rainbowify(String string)
    {
        CHAT_COLOR_ITERATOR = CHAT_COLOR_POOL.iterator();

        StringBuilder newString = new StringBuilder();
        char[] chars = string.toCharArray();

        for (char c : chars)
        {
            if (!CHAT_COLOR_ITERATOR.hasNext())
            {
                CHAT_COLOR_ITERATOR = CHAT_COLOR_POOL.iterator(); //Restart from first colour if there are no more colours in iterator.
            }
            newString.append(CHAT_COLOR_ITERATOR.next()).append(c);
        }

        return newString.toString();
    }

    public static String colorize(String string)
    {
        Matcher matcher = Pattern.compile("&#[a-f0-9A-F]{6}").matcher(string);
        while (matcher.find())
        {
            String code = matcher.group().replace("&", "");
            string = string.replace("&" + code, net.md_5.bungee.api.ChatColor.of(code) + "");
        }

        string = ChatColor.translateAlternateColorCodes('&', string);
        return string;
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

    public static String getNMSVersion()
    {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static int randomInteger(int min, int max)
    {
        int range = max - min + 1;
        int value = (int)(Math.random() * range) + min;
        return value;
    }

    public static String randomString(int length)
    {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
        String randomString = "";
        for (int i = 0; i < length; i++)
        {

            int selectedCharacter = randomInteger(1, characters.length()) - 1;

            randomString += characters.charAt(selectedCharacter);
        }

        return randomString;

    }

    public static boolean isPaper()
    {
        try
        {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        }
        catch (ClassNotFoundException ex)
        {
            return false;
        }
    }

    public static void fixCommandVoid(Player player)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            for (Entity passengerEntity : p.getPassengers())
            {
                if (passengerEntity == player)
                {
                    p.removePassenger(passengerEntity);
                }
            }
        }
    }

    public static char getRandomCharacter()
    {
        return CHARACTER_STRING.charAt(new Random().nextInt(CHARACTER_STRING.length()));
    }

    public static void give(Player player, Material material, String coloredName, int amount, String... lore)
    {
        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(FUtil.colorize(coloredName));
        List<String> loreList = new ArrayList<>();
        for (String entry : lore)
        {
            loreList.add(FUtil.colorize(entry));
        }
        meta.setLore(loreList);
        stack.setItemMeta(meta);
        player.getInventory().setItem(player.getInventory().firstEmpty(), stack);
    }

    public static Player getRandomPlayer()
    {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        return players.get(randomInteger(0, players.size() - 1));
    }

    // convert the current time
    public static int getTimeInTicks(int tz)
    {
        if (timeZoneOutOfBounds(tz))
        {
            return -1;
        }
        Calendar date = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_LOOKUP.get(tz)));
        int res = 0;
        for (int i = 0; i < date.get(Calendar.HOUR_OF_DAY) - 6; i++) // oh yeah i don't know why this is 6 hours ahead
        {
            res += 1000;
        }
        int addExtra = 0; // we're adding extra to account for repeating decimals
        for (int i = 0; i < date.get(Calendar.MINUTE); i++)
        {
            res += 16;
            addExtra++;
            if (addExtra == 3)
            {
                res += 1;
                addExtra = 0;
            }
        }
        // this is the best it can be. trust me.
        return res;
    }

    public static boolean timeZoneOutOfBounds(int tz)
    {
        return tz < -12 || tz > 12;
    }

    public static String getIp(Player player)
    {
        return player.getAddress().getAddress().getHostAddress().trim();
    }

    public static String getIp(PlayerLoginEvent event)
    {
        return event.getAddress().getHostAddress().trim();
    }

    private static Color interpolateColor(Color c1, Color c2, double factor)
    {
        long[] c1values = {c1.getRed(), c1.getGreen(), c1.getBlue()};
        long[] c2values = {c2.getRed(), c2.getGreen(), c2.getBlue()};
        for (int i = 0; i < 3; i++)
        {
            c1values[i] = Math.round(c1values[i] + factor * (c2values[i] - c1values[i]));
        }
        return Color.fromRGB((int) c1values[0], (int) c1values[1], (int) c1values[2]);
    }

    public static List<Color> createColorGradient(Color c1, Color c2, int steps)
    {
        double factor = 1.0 / (steps - 1.0);
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < steps; i++)
        {
            colors.add(interpolateColor(c1, c2, factor * i));
        }
        return colors;
    }

    public static Color fromAWT(java.awt.Color color)
    {
        return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static java.awt.Color toAWT(Color color)
    {
        return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static java.awt.Color getRandomAWTColor()
    {
        return new java.awt.Color(randomInteger(0, 255), randomInteger(0, 255), randomInteger(0, 255));
    }

    public static String getHexStringOfAWTColor(java.awt.Color color)
    {
        String hex = Integer.toHexString(color.getRGB() & 0xFFFFFF);
        if (hex.length() < 6)
        {
            hex = "0" + hex;
        }
        return "#" + hex;
    }

    public static void createExplosionOnDelay(Location location, float power, int delay)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                location.getWorld().createExplosion(location, power);
            }
        }.runTaskLater(TotalFreedomMod.getPlugin(), delay);
    }

    private static class MojangResponse
    {
        String id;
        String name;
    }
}
