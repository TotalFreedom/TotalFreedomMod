package me.StevenLawson.TotalFreedomMod;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

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

    @SuppressWarnings("unchecked")
    public static void banUsername(String name, String reason, String source, Date expireDate)
    {
        name = name.toLowerCase().trim();

        if (TFM_SuperadminList.getSuperadminNames().contains(name))
        {
            TFM_Log.info("Not banning username " + name + ": is superadmin");
            return;
        }

        for (String username : (List<String>) TFM_ConfigEntry.UNBANNABLE_USERNAMES.getList())
        {
            if (username.toLowerCase().trim().equals(name))
            {
                TFM_Log.info("Not banning username " + name + ": is unbannable as defined in config.");
                return;
            }
        }

        BanEntry entry = new BanEntry(name);
        if (expireDate != null)
        {
            entry.setExpires(expireDate);
        }
        if (reason != null)
        {
            entry.setReason(reason);
        }
        if (source != null)
        {
            entry.setSource(source);
        }
        BanList nameBans = MinecraftServer.getServer().getPlayerList().getNameBans();
        nameBans.add(entry);
    }

    public static boolean isNameBanned(String name)
    {
        name = name.toLowerCase().trim();
        BanList nameBans = MinecraftServer.getServer().getPlayerList().getNameBans();
        nameBans.removeExpired();
        return nameBans.getEntries().containsKey(name);
    }

    public static void banIP(String ip, String reason, String source, Date expireDate)
    {
        ip = ip.toLowerCase().trim();
        BanEntry entry = new BanEntry(ip);
        if (expireDate != null)
        {
            entry.setExpires(expireDate);
        }
        if (reason != null)
        {
            entry.setReason(reason);
        }
        if (source != null)
        {
            entry.setSource(source);
        }
        BanList ipBans = MinecraftServer.getServer().getPlayerList().getIPBans();
        ipBans.add(entry);
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

    public static int purgeWhitelist()
    {
        Set whitelisted = MinecraftServer.getServer().getPlayerList().getWhitelisted();
        int size = whitelisted.size();
        whitelisted.clear();
        return size;
    }

    public static void handlePlayerLogin(PlayerLoginEvent event)
    {
        // this should supersede all other onPlayerLogin authentication on the TFM server.
        // when using the TFM CraftBukkit, CraftBukkit itself should not do any of its own authentication.

        final Server server = TotalFreedomMod.plugin.getServer();

        final PlayerList playerList = MinecraftServer.getServer().getPlayerList();
        final BanList banByIP = playerList.getIPBans();
        final BanList banByName = playerList.getNameBans();

        final Player player = event.getPlayer();

        final String username = player.getName();
        final String ip = event.getAddress().getHostAddress().trim().toLowerCase();

        if (username.trim().length() <= 2)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your username is too short (must be at least 3 characters long).");
            return;
        }
        else if (Pattern.compile("[^a-zA-Z0-9\\-\\.\\_]").matcher(username).find())
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your username contains invalid characters.");
            return;
        }

        // not safe to use TFM_Util.isUserSuperadmin for player logging in because player.getAddress() will return a null until after player login.
        boolean isSuperadmin;
        if (server.getOnlineMode())
        {
            isSuperadmin = TFM_SuperadminList.getSuperadminNames().contains(username.toLowerCase());
        }
        else
        {
            isSuperadmin = TFM_SuperadminList.checkPartialSuperadminIP(ip, username.toLowerCase());
        }

        if (!isSuperadmin)
        {
            BanEntry entry = null;

            if (banByName.isBanned(username.toLowerCase()))
            {
                entry = (BanEntry) banByName.getEntries().get(username.toLowerCase());

                String kickMessage = ChatColor.RED + "You are banned from this server.";
                if (entry != null)
                {
                    kickMessage = kickMessage + "\nReason: " + entry.getReason();
                    if (entry.getExpires() != null)
                    {
                        kickMessage = kickMessage + "\nYour ban will be removed on " + dateFormat.format(entry.getExpires());
                    }
                }

                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessage);
                return;
            }

            boolean isIpBanned = false;

            Iterator ipBans = banByIP.getEntries().keySet().iterator();
            while (ipBans.hasNext())
            {
                String testIp = (String) ipBans.next();

                if (!testIp.matches("^\\d{1,3}\\.\\d{1,3}\\.(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)$"))
                {
                    continue;
                }

                if (ip.equals(testIp))
                {
                    entry = (BanEntry) banByIP.getEntries().get(testIp);
                    isIpBanned = true;
                    break;
                }

                if (TFM_Util.fuzzyIpMatch(testIp, ip, 4))
                {
                    entry = (BanEntry) banByIP.getEntries().get(testIp);
                    isIpBanned = true;
                    break;
                }
            }

            if (isIpBanned)
            {
                String kickMessage = ChatColor.RED + "Your IP address is banned from this server.";
                if (entry != null)
                {
                    kickMessage = kickMessage + "\nReason: " + entry.getReason();
                    if (entry.getExpires() != null)
                    {
                        kickMessage = kickMessage + "\nYour ban will be removed on " + dateFormat.format(entry.getExpires());
                    }
                }

                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessage);
                return;
            }

            for (String testPlayer : TotalFreedomMod.permbanned_players)
            {
                if (testPlayer.equalsIgnoreCase(username))
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + "Your username is permanently banned from this server.\nRelease procedures are available at http://bit.ly/PermBan");
                    return;
                }
            }

            for (String testIp : TotalFreedomMod.permbanned_ips)
            {
                if (TFM_Util.fuzzyIpMatch(testIp, ip, 4))
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + "Your IP address is permanently banned from this server.\nRelease procedures are available at http://bit.ly/PermBan");
                    return;
                }
            }

            if (server.getOnlinePlayers().length >= server.getMaxPlayers())
            {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, "Sorry, but this server is full.");
                return;
            }

            if (TFM_ConfigEntry.ADMIN_ONLY_MODE.getBoolean())
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is temporarily open to admins only.");
                return;
            }

            if (TotalFreedomMod.lockdownEnabled)
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is currently in lockdown mode.");
                return;
            }

            if (playerList.hasWhitelist)
            {
                if (!playerList.getWhitelisted().contains(username.toLowerCase()))
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You are not whitelisted on this server.");
                    return;
                }
            }

            for (Player test_player : server.getOnlinePlayers())
            {
                if (test_player.getName().equalsIgnoreCase(username))
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your username is already logged into this server.");
                    return;
                }
            }
        }
        else
        {
            for (Player testPlayer : server.getOnlinePlayers())
            {
                if (testPlayer.getName().equalsIgnoreCase(username))
                {
                    testPlayer.kickPlayer("An admin just logged in with the username you are using.");
                }
            }

            boolean canKick = true; // if the server is full of superadmins, however unlikely that might be, this will prevent an infinite loop.
            while (server.getOnlinePlayers().length >= server.getMaxPlayers() && canKick)
            {
                canKick = false;
                for (Player testPlayer : server.getOnlinePlayers())
                {
                    if (!TFM_SuperadminList.isUserSuperadmin(testPlayer))
                    {
                        canKick = true;
                        testPlayer.kickPlayer("You have been kicked to free up room for an admin.");
                        break;
                    }
                }
            }

            if (TotalFreedomMod.lockdownEnabled)
            {
                TFM_Util.playerMsg(player, "Warning: Server is currenty in lockdown-mode, new players will not be able to join!", ChatColor.RED);
            }
        }
    }

    public static String getVersion()
    {
        return MinecraftServer.getServer().getVersion();
    }
}
