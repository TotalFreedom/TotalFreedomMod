package me.StevenLawson.TotalFreedomMod;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.PropertyManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class TFM_ServerInterface
{
    public static final String COMPILE_NMS_VERSION = "v1_7_R3";
    public static final Pattern USERNAME_REGEX = Pattern.compile("^[\\w\\d_]{3,20}$");

    public static void setOnlineMode(boolean mode)
    {
        final PropertyManager manager = MinecraftServer.getServer().getPropertyManager();
        manager.a("online-mode", mode);
        manager.savePropertiesFile();
    }

    public static int purgeWhitelist()
    {
        String[] whitelisted = MinecraftServer.getServer().getPlayerList().getWhitelisted();
        int size = whitelisted.length;
        for (String player : MinecraftServer.getServer().getPlayerList().getWhitelist().getEntries())
        {
            MinecraftServer.getServer().getPlayerList().getWhitelist().remove(player);
        }

        try
        {
            MinecraftServer.getServer().getPlayerList().getWhitelist().save();
        }
        catch (Exception ex)
        {
            TFM_Log.warning("Could not purge the whitelist!");
            TFM_Log.warning(ex);
        }
        return size;
    }

    public static boolean isWhitelisted()
    {
        return MinecraftServer.getServer().getPlayerList().hasWhitelist;
    }

    public static List<?> getWhitelisted()
    {
        return Arrays.asList(MinecraftServer.getServer().getPlayerList().getWhitelisted());
    }

    public static String getVersion()
    {
        return MinecraftServer.getServer().getVersion();
    }

    public static void handlePlayerLogin(PlayerLoginEvent event)
    {
        final Server server = TotalFreedomMod.server;
        final Player player = event.getPlayer();
        final String username = player.getName();
        final UUID uuid = TFM_UuidManager.getUniqueId(username);
        final String ip = event.getAddress().getHostAddress().trim();

        // Perform username checks
        if (username.length() < 3 || username.length() > 20)
        {
            event.disallow(Result.KICK_OTHER, "Your username is an invalid length (must be between 3 and 20 characters long).");
            return;
        }

        if (!USERNAME_REGEX.matcher(username).find())
        {
            event.disallow(Result.KICK_OTHER, "Your username contains invalid characters.");
            return;
        }

        // Check if player is admin
        // Not safe to use TFM_Util.isSuperAdmin for player logging in because player.getAddress() will return a null until after player login.
        final boolean isAdmin;
        if (server.getOnlineMode())
        {
            isAdmin = TFM_AdminList.getSuperUUIDs().contains(uuid);
        }
        else
        {
            final TFM_Admin admin = TFM_AdminList.getEntryByIp(ip);
            isAdmin = admin != null && admin.isActivated();
        }

        // Validation below this point
        if (isAdmin) // Player is superadmin
        {
            // force-allow log in
            event.allow();

            for (Player onlinePlayer : server.getOnlinePlayers())
            {
                if (onlinePlayer.getName().equalsIgnoreCase(username))
                {
                    onlinePlayer.kickPlayer("An admin just logged in with the username you are using.");
                }
            }

            int count = server.getOnlinePlayers().length;
            if (count >= server.getMaxPlayers())
            {
                for (Player onlinePlayer : server.getOnlinePlayers())
                {
                    if (!TFM_AdminList.isSuperAdmin(onlinePlayer))
                    {
                        onlinePlayer.kickPlayer("You have been kicked to free up room for an admin.");
                        count--;
                    }

                    if (count < server.getMaxPlayers())
                    {
                        break;
                    }
                }
            }

            if (count >= server.getMaxPlayers())
            {
                event.disallow(Result.KICK_OTHER, "The server is full and a player could not be kicked, sorry!");
                return;
            }
            return;
        }

        // Player is not an admin

        // Server full check
        if (server.getOnlinePlayers().length >= server.getMaxPlayers())
        {
            event.disallow(Result.KICK_FULL, "Sorry, but this server is full.");
            return;
        }

        // Admin-only mode
        if (TFM_ConfigEntry.ADMIN_ONLY_MODE.getBoolean())
        {
            event.disallow(Result.KICK_OTHER, "Server is temporarily open to admins only.");
            return;
        }

        // Lockdown mode
        if (TotalFreedomMod.lockdownEnabled)
        {
            event.disallow(Result.KICK_OTHER, "Server is currently in lockdown mode.");
            return;
        }

        // Username already logged in
        for (Player onlinePlayer : server.getOnlinePlayers())
        {
            if (onlinePlayer.getName().equalsIgnoreCase(username))
            {
                event.disallow(Result.KICK_OTHER, "Your username is already logged into this server.");
                return;
            }
        }

        // Whitelist
        if (isWhitelisted())
        {
            if (!getWhitelisted().contains(username.toLowerCase()))
            {
                event.disallow(Result.KICK_OTHER, "You are not whitelisted on this server.");
                return;
            }
        }

        // UUID ban
        if (TFM_BanManager.isUuidBanned(uuid))
        {
            final TFM_Ban ban = TFM_BanManager.getByUuid(uuid);
            event.disallow(Result.KICK_OTHER, ban.getKickMessage());
            return;
        }

        // IP ban
        if (TFM_BanManager.isIpBanned(ip))
        {
            final TFM_Ban ban = TFM_BanManager.getByIp(ip);
            event.disallow(Result.KICK_OTHER, ban.getKickMessage());
            return;
        }

        // Permbanned IPs
        for (String testIp : TFM_PermbanList.getPermbannedIps())
        {
            if (TFM_Util.fuzzyIpMatch(testIp, ip, 4))
            {
                event.disallow(Result.KICK_OTHER,
                        ChatColor.RED + "Your IP address is permanently banned from this server.\n"
                        + "Release procedures are available at\n"
                        + ChatColor.GOLD + TFM_ConfigEntry.SERVER_PERMBAN_URL.getString());
                return;
            }
        }

        // Permbanned usernames
        for (String testPlayer : TFM_PermbanList.getPermbannedPlayers())
        {
            if (testPlayer.equalsIgnoreCase(username))
            {
                event.disallow(Result.KICK_OTHER,
                        ChatColor.RED + "Your username is permanently banned from this server.\n"
                        + "Release procedures are available at\n"
                        + ChatColor.GOLD + TFM_ConfigEntry.SERVER_PERMBAN_URL.getString());
                return;
            }
        }
    }
}
