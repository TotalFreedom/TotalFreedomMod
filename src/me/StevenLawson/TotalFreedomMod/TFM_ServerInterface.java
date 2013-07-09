package me.StevenLawson.TotalFreedomMod;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.server.v1_6_R2.BanEntry;
import net.minecraft.server.v1_6_R2.BanList;
import net.minecraft.server.v1_6_R2.MinecraftServer;
import net.minecraft.server.v1_6_R2.PlayerList;
import net.minecraft.server.v1_6_R2.PropertyManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

public class TFM_ServerInterface
{
    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

    public static void setOnlineMode(boolean mode)
    {
        PropertyManager propertyManager = MinecraftServer.getServer().getPropertyManager();
        propertyManager.a("online-mode", mode);
        propertyManager.savePropertiesFile();
    }

    public static void wipeNameBans()
    {
        BanList nameBans = MinecraftServer.getServer().getPlayerList().getNameBans();
        nameBans.getEntries().clear();
        nameBans.save();
    }

    public static void wipeIpBans()
    {
        BanList IPBans = MinecraftServer.getServer().getPlayerList().getIPBans();
        IPBans.getEntries().clear();
        IPBans.save();
    }

    public static void unbanUsername(String name)
    {
        name = name.toLowerCase().trim();
        BanList nameBans = MinecraftServer.getServer().getPlayerList().getNameBans();
        nameBans.remove(name);
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
        BanList nameBans = MinecraftServer.getServer().getPlayerList().getNameBans();
        nameBans.add(ban_entry);
    }

    public static boolean isNameBanned(String name)
    {
        name = name.toLowerCase().trim();
        BanList nameBans = MinecraftServer.getServer().getPlayerList().getNameBans();
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
        BanList ipBans = MinecraftServer.getServer().getPlayerList().getIPBans();
        ipBans.add(ban_entry);
    }

    public static void unbanIP(String ip)
    {
        ip = ip.toLowerCase().trim();
        BanList ipBans = MinecraftServer.getServer().getPlayerList().getIPBans();
        ipBans.remove(ip);
    }

    public static boolean isIPBanned(String ip)
    {
        ip = ip.toLowerCase().trim();
        BanList ipBans = MinecraftServer.getServer().getPlayerList().getIPBans();
        ipBans.removeExpired();
        return ipBans.getEntries().containsKey(ip);
    }

    @SuppressWarnings("rawtypes")
    public static int purgeWhitelist()
    {
        Set whitelisted = MinecraftServer.getServer().getPlayerList().getWhitelisted();
        int size = whitelisted.size();
        whitelisted.clear();
        return size;
    }

    public static void handlePlayerLogin(PlayerLoginEvent event)
    {

        final Server server = TotalFreedomMod.plugin.getServer();

        final PlayerList player_list = MinecraftServer.getServer().getPlayerList();
        final BanList banByIP = player_list.getIPBans();
        final BanList banByName = player_list.getNameBans();

        final Player p = event.getPlayer();

        final String player_name = p.getName();
        final String player_ip = event.getAddress().getHostAddress().trim().toLowerCase();

        if (player_name.trim().length() <= 2)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    "Your username is too short (must be at least 3 characters long).");
            return;
        }
        else if (Pattern.compile("[^a-zA-Z0-9\\-\\.\\_]").matcher(player_name).find())
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your username contains invalid characters.");
            return;
        }

        // not safe to use TFM_Util.isUserSuperadmin for player logging in because p.getAddress()
        // will return a null until after player login.
        boolean is_superadmin;
        if (server.getOnlineMode())
        {
            is_superadmin = TFM_SuperadminList.getSuperadminNames().contains(player_name.toLowerCase());
        }
        else
        {
            is_superadmin = TFM_SuperadminList.checkPartialSuperadminIP(player_ip, player_name.toLowerCase());
        }

        if (!is_superadmin)
        {
            BanEntry ban_entry = null;

            if (banByName.isBanned(player_name.toLowerCase()))
            {
                ban_entry = (BanEntry) banByName.getEntries().get(player_name.toLowerCase());

                String kick_message = ChatColor.RED + "You are banned from this server.";
                if (ban_entry != null)
                {
                    kick_message = kick_message + "\nReason: " + ban_entry.getReason();
                    if (ban_entry.getExpires() != null)
                    {
                        kick_message = kick_message + "\nYour ban will be removed on "
                                + date_format.format(ban_entry.getExpires());
                    }
                }

                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kick_message);
                return;
            }

            boolean is_ip_banned = false;

            @SuppressWarnings("rawtypes")
            Iterator ip_bans = banByIP.getEntries().keySet().iterator();
            while (ip_bans.hasNext())
            {
                String test_ip = (String) ip_bans.next();

                if (!test_ip.matches("^\\d{1,3}\\.\\d{1,3}\\.(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)$"))
                {
                    continue;
                }

                if (player_ip.equals(test_ip))
                {
                    ban_entry = (BanEntry) banByIP.getEntries().get(test_ip);
                    is_ip_banned = true;
                    break;
                }

                if (TFM_Util.fuzzyIpMatch(test_ip, player_ip, 4))
                {
                    ban_entry = (BanEntry) banByIP.getEntries().get(test_ip);
                    is_ip_banned = true;
                    break;
                }
            }

            if (is_ip_banned)
            {
                String kick_message = ChatColor.RED + "Your IP address is banned from this server.";
                if (ban_entry != null)
                {
                    kick_message = kick_message + "\nReason: " + ban_entry.getReason();
                    if (ban_entry.getExpires() != null)
                    {
                        kick_message = kick_message + "\nYour ban will be removed on "
                                + date_format.format(ban_entry.getExpires());
                    }
                }

                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kick_message);
                return;
            }

            for (String test_player : TotalFreedomMod.permbanned_players)
            {
                if (test_player.equalsIgnoreCase(player_name))
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED
                            + "Your username is permanently banned from this server.");
                    return;
                }
            }

            for (String test_ip : TotalFreedomMod.permbanned_ips)
            {
                if (TFM_Util.fuzzyIpMatch(test_ip, player_ip, 4))
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED
                            + "Your IP address is permanently banned from this server.");
                    return;
                }
            }

            if (server.getOnlinePlayers().length >= server.getMaxPlayers())
            {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, "Sorry, but this server is full.");
                return;
            }

            if (TotalFreedomMod.adminOnlyMode)
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is temporarily open to admins only.");
                return;
            }

            if (player_list.hasWhitelist)
            {
                if (!player_list.getWhitelisted().contains(player_name.toLowerCase()))
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You are not whitelisted on this server.");
                    return;
                }
            }

            for (Player test_player : server.getOnlinePlayers())
            {
                if (test_player.getName().equalsIgnoreCase(player_name))
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your username is already logged into this server.");
                    return;
                }
            }
        }
        else // if user is superadmin
        {
            // force-allow superadmins to log in
            event.allow();

            if (isIPBanned(player_ip))
            {
                unbanIP(player_ip);
            }

            if (isNameBanned(player_name))
            {
                unbanUsername(player_name);
            }

            for (Player test_player : server.getOnlinePlayers())
            {
                if (test_player.getName().equalsIgnoreCase(player_name))
                {
                    test_player.kickPlayer("An admin just logged in with the username you are using.");
                }
            }

            if (server.getOnlinePlayers().length >= server.getMaxPlayers())
            {
                for (Player op : server.getOnlinePlayers())
                {
                    if (!TFM_SuperadminList.isUserSuperadmin(op))
                    {
                        op.kickPlayer("You have been kicked to free up space for an admin");
                        return;
                    }
                }

                // if the server is full of superadmins, however unlikely that might be, this will prevent an infinite loop.
                if (server.getOnlinePlayers().length >= server.getMaxPlayers())
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_FULL, "Sorry, this server is full");
                }
            }
        }
    }

    public static String getVersion()
    {
        return MinecraftServer.getServer().getVersion();
    }
}
