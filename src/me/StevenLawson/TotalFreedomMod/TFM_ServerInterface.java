package me.StevenLawson.TotalFreedomMod;

import java.text.SimpleDateFormat;
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
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

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
        final TFM_BanManager banManager = TFM_BanManager.getInstance();

        final Player player = event.getPlayer();

        final String username = player.getName();
        final UUID uuid = player.getUniqueId();
        final String ip = event.getAddress().getHostAddress().trim();

        if (username.length() < 3 || username.length() > 20)
        {
            event.disallow(Result.KICK_OTHER, "Your username is an invalid length (must be between 3 and 20 characters long).");
            return;
        }

        if (!USERNAME_REGEX.matcher(username).find())
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your username contains invalid characters.");
            return;
        }

        // not safe to use TFM_Util.isSuperAdmin for player logging in because player.getAddress() will return a null until after player login.
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
        if (!isAdmin) // If the player is not an admin
        {
            // UUID bans
            if (banManager.isUuidBanned(uuid))
            {
                final TFM_Ban ban = banManager.getByUuid(uuid);

                String kickMessage = ChatColor.RED + "You are temporarily banned from this server."
                        + "\nAppeal at " + ChatColor.GOLD + TFM_ConfigEntry.SERVER_BAN_URL.getString();

                if (!ban.getReason().equals("none"))
                {
                    kickMessage = kickMessage + "\nReason: " + ban.getReason();
                }

                if (ban.getExpireUnix() != 0)
                {
                    kickMessage = kickMessage + "\nYour ban will be removed on " + dateFormat.format(TFM_Util.getUnixDate(ban.getExpireUnix()));
                }

                event.disallow(Result.KICK_OTHER, kickMessage);
                return;
            }

            if (banManager.isIpBanned(ip))
            {
                final TFM_Ban ban = banManager.getByIp(ip);

                String kickMessage = ChatColor.RED + "Your IP address is temporarily banned from this server."
                        + "\nAppeal at " + ChatColor.GOLD + TFM_ConfigEntry.SERVER_BAN_URL.getString();

                if (!ban.getReason().equals("none"))
                {
                    kickMessage = kickMessage + "\nReason: " + ban.getReason();
                }

                if (ban.getExpireUnix() != 0)
                {
                    kickMessage = kickMessage + "\nYour ban will be removed on " + dateFormat.format(TFM_Util.getUnixDate(ban.getExpireUnix()));
                }

                event.disallow(Result.KICK_OTHER, kickMessage);
                return;
            }

            // Permbanned Ips
            for (String testIp : TFM_PermbanList.getPermbannedIps())
            {
                if (TFM_Util.fuzzyIpMatch(testIp, ip, 4))
                {
                    event.disallow(Result.KICK_OTHER,
                            ChatColor.RED + "Your IP address is permanently banned from this server.\nRelease procedures are available at\n"
                            + ChatColor.GOLD + TFM_ConfigEntry.SERVER_PERMBAN_URL);
                    return;
                }
            }

            // Permbanned names
            for (String testPlayer : TFM_PermbanList.getPermbannedPlayers())
            {
                if (testPlayer.equalsIgnoreCase(username))
                {
                    event.disallow(Result.KICK_OTHER,
                            ChatColor.RED + "Your username is permanently banned from this server.\nRelease procedures are available at\n"
                            + ChatColor.GOLD + TFM_ConfigEntry.SERVER_PERMBAN_URL);
                    return;
                }
            }

            // Server full check
            if (server.getOnlinePlayers().length >= server.getMaxPlayers())
            {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, "Sorry, but this server is full.");
                return;
            }

            // Admin-only mode
            if (TFM_ConfigEntry.ADMIN_ONLY_MODE.getBoolean())
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is temporarily open to admins only.");
                return;
            }

            // Lockdown mode
            if (TotalFreedomMod.lockdownEnabled)
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is currently in lockdown mode.");
                return;
            }

            // Whitelist check
            if (isWhitelisted())
            {
                if (!getWhitelisted().contains(username.toLowerCase()))
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You are not whitelisted on this server.");
                    return;
                }
            }

            // Username already logged in check
            for (Player onlinePlayer : server.getOnlinePlayers())
            {
                if (onlinePlayer.getName().equalsIgnoreCase(username))
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your username is already logged into this server.");
                    return;
                }
            }
        }
        else // Player is superadmin
        {
            // force-allow superadmins to log in
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
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "The server is full and a player could not be kicked, sorry!");
                return;
            }

        }
    }
}
