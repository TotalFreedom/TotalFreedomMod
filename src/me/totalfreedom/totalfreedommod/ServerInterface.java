package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.util.FSync;
import me.totalfreedom.totalfreedommod.util.FLog;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import me.totalfreedom.totalfreedommod.banning.FBan;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.listener.PlayerListener;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PropertyManager;
import net.pravian.aero.component.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class ServerInterface extends AbstractService<TotalFreedomMod>
{
    public static final String COMPILE_NMS_VERSION = "v1_8_R3";
    public static final Pattern USERNAME_REGEX = Pattern.compile("^[\\w\\d_]{3,20}$");

    public ServerInterface(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    public void setOnlineMode(boolean mode)
    {
        final PropertyManager manager = MinecraftServer.getServer().getPropertyManager();
        manager.setProperty("online-mode", mode);
        manager.savePropertiesFile();
    }

    public int purgeWhitelist()
    {
        String[] whitelisted = MinecraftServer.getServer().getPlayerList().getWhitelisted();
        int size = whitelisted.length;
        for (EntityPlayer player : MinecraftServer.getServer().getPlayerList().players)
        {
            MinecraftServer.getServer().getPlayerList().getWhitelist().remove(player.getProfile());
        }

        try
        {
            MinecraftServer.getServer().getPlayerList().getWhitelist().save();
        }
        catch (Exception ex)
        {
            FLog.warning("Could not purge the whitelist!");
            FLog.warning(ex);
        }
        return size;
    }

    public boolean isWhitelisted()
    {
        return MinecraftServer.getServer().getPlayerList().getHasWhitelist();
    }

    public List<?> getWhitelisted()
    {
        return Arrays.asList(MinecraftServer.getServer().getPlayerList().getWhitelisted());
    }

    public String getVersion()
    {
        return MinecraftServer.getServer().getVersion();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
    {
        final String ip = event.getAddress().getHostAddress().trim();
        final boolean isAdmin = plugin.al.getEntryByIp(ip) != null;

        // Check if the player is already online
        for (Player onlinePlayer : server.getOnlinePlayers())
        {
            if (!onlinePlayer.getName().equalsIgnoreCase(event.getName()))
            {
                continue;
            }

            if (!isAdmin)
            {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Your username is already logged into this server.");
            }
            else
            {
                event.allow();
                FSync.playerKick(onlinePlayer, "An admin just logged in with the username you are using.");
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        final Player player = event.getPlayer();
        final String username = player.getName();
        final String ip = event.getAddress().getHostAddress().trim();

        // Check username length
        if (username.length() < 3 || username.length() > TotalFreedomMod.MAX_USERNAME_LENGTH)
        {
            event.disallow(Result.KICK_OTHER, "Your username is an invalid length (must be between 3 and 20 characters long).");
            return;
        }

        // Check username characters
        if (!USERNAME_REGEX.matcher(username).find())
        {
            event.disallow(Result.KICK_OTHER, "Your username contains invalid characters.");
            return;
        }

        // Check force-IP match
        if (ConfigEntry.FORCE_IP_ENABLED.getBoolean())
        {
            final String hostname = event.getHostname().replace("\u0000FML\u0000", ""); // Forge fix - https://github.com/TotalFreedom/TotalFreedomMod/issues/493
            final String connectAddress = ConfigEntry.SERVER_ADDRESS.getString();
            final int connectPort = server.getPort();

            if (!hostname.equalsIgnoreCase(connectAddress + ":" + connectPort) && !hostname.equalsIgnoreCase(connectAddress + ".:" + connectPort))
            {
                final int forceIpPort = ConfigEntry.FORCE_IP_PORT.getInteger();
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ConfigEntry.FORCE_IP_KICKMSG.getString()
                        .replace("%address%", ConfigEntry.SERVER_ADDRESS.getString() + (forceIpPort == PlayerListener.DEFAULT_PORT ? "" : ":" + forceIpPort)));
                return;
            }

        }

        // Check if player is admin
        // Not safe to use TFM_Util.isSuperAdmin(player) because player.getAddress() will return a null until after player login.
        final boolean isAdmin = plugin.al.getEntryByIp(ip) != null;

        // Validation below this point
        if (isAdmin) // Player is superadmin
        {
            // Force-allow log in
            event.allow();

            int count = server.getOnlinePlayers().size();
            if (count >= server.getMaxPlayers())
            {
                for (Player onlinePlayer : server.getOnlinePlayers())
                {
                    if (!plugin.al.isAdmin(onlinePlayer))
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
        if (server.getOnlinePlayers().size() >= server.getMaxPlayers())
        {
            event.disallow(Result.KICK_FULL, "Sorry, but this server is full.");
            return;
        }

        // Admin-only mode
        if (ConfigEntry.ADMIN_ONLY_MODE.getBoolean())
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

        // Whitelist
        if (isWhitelisted())
        {
            if (!getWhitelisted().contains(username.toLowerCase()))
            {
                event.disallow(Result.KICK_OTHER, "You are not whitelisted on this server.");
                return;
            }
        }

        // Permbanned IPs
        for (String testIp : plugin.pb.getPermbannedIps())
        {
            if (FUtil.fuzzyIpMatch(testIp, ip, 4))
            {
                event.disallow(Result.KICK_OTHER,
                        ChatColor.RED + "Your IP address is permanently banned from this server.\n"
                        + "Release procedures are available at\n"
                        + ChatColor.GOLD + ConfigEntry.SERVER_PERMBAN_URL.getString());
                return;
            }
        }

        // Permbanned usernames
        for (String testPlayer : plugin.pb.getPermbannedNames())
        {
            if (testPlayer.equalsIgnoreCase(username))
            {
                event.disallow(Result.KICK_OTHER,
                        ChatColor.RED + "Your username is permanently banned from this server.\n"
                        + "Release procedures are available at\n"
                        + ChatColor.GOLD + ConfigEntry.SERVER_PERMBAN_URL.getString());
                return;
            }
        }

        // Regular ban
        FBan ban = plugin.bm.getByUsername(username);
        if (ban == null)
        {
            ban = plugin.bm.getByIp(ip);
        }

        if (ban != null)
        {
            event.disallow(Result.KICK_OTHER, ban.bakeKickMessage());
        }
    }
}
