package me.totalfreedom.totalfreedommod.bridge;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CoreProtectBridge extends FreedomService
{
    private CoreProtectAPI coreProtectAPI = null;

    private final List<String> tables = Arrays.asList("co_sign", "co_session", "co_container", "co_block");

    private BukkitTask wiper;

    public CoreProtectBridge(TotalFreedomMod plugin)
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

    public CoreProtect getCoreProtect()
    {
        CoreProtect coreProtect = null;
        try
        {
            final Plugin coreProtectPlugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");

            if (coreProtectPlugin != null && coreProtectPlugin instanceof CoreProtect)
            {
                coreProtect = (CoreProtect)coreProtectPlugin;
            }
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }
        return coreProtect;
    }

    public CoreProtectAPI getCoreProtectAPI()
    {
        if (coreProtectAPI == null)
        {
            try
            {
                final CoreProtect coreProtect = getCoreProtect();

                coreProtectAPI = coreProtect.getAPI();

                // Check if the plugin or api is not enabled, if so, return null
                if (!coreProtect.isEnabled() || !coreProtectAPI.isEnabled())
                {
                    return null;
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }

        return coreProtectAPI;
    }

    public boolean isEnabled()
    {
        final CoreProtect coreProtect = getCoreProtect();

        return coreProtect != null && coreProtect.isEnabled();
    }

    // Rollback the specified player's edits that were in the last 24 hours.
    public void rollback(final String name)
    {
        final CoreProtectAPI coreProtect = getCoreProtectAPI();

        if (!isEnabled())
        {
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                coreProtect.performRollback(86400, Arrays.asList(name), null, null, null, null, 0, null);
            }
        }.runTaskAsynchronously(plugin);
    }

    // Reverts a rollback for the specified player's edits that were in the last 24 hours.
    public void restore(final String name)
    {
        final CoreProtectAPI coreProtect = getCoreProtectAPI();

        if (!isEnabled())
        {
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                coreProtect.performRestore(86400, Arrays.asList(name), null, null, null, null, 0, null);
            }
        }.runTaskAsynchronously(plugin);
    }

    public File getDatabase()
    {
        if (!isEnabled())
        {
            return null;
        }

        return (new File(getCoreProtect().getDataFolder(), "database.db"));
    }

    public double getDBSize()
    {
        double bytes = getDatabase().length();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        return (megabytes / 1024);
    }

    // Wipes DB for the specified world
    public void clearDatabase(World world)
    {
        clearDatabase(world, false);
    }

    // Wipes DB for the specified world
    public void clearDatabase(World world, Boolean shutdown)
    {
        if (!ConfigEntry.COREPROTECT_MYSQL_ENABLED.getBoolean())
        {
            return;
        }
        final CoreProtect coreProtect = getCoreProtect();

        if (coreProtect == null)
        {
            return;
        }

        /* As CoreProtect doesn't have an API method for deleting all of the data for a specific world
           we have to do this manually via SQL */
        Connection connection = null;
        try
        {
            String host = ConfigEntry.COREPROTECT_MYSQL_HOST.getString();
            String port = ConfigEntry.COREPROTECT_MYSQL_PORT.getString();
            String username = ConfigEntry.COREPROTECT_MYSQL_USERNAME.getString();
            String password = ConfigEntry.COREPROTECT_MYSQL_PASSWORD.getString();
            String database = ConfigEntry.COREPROTECT_MYSQL_DATABASE.getString();
            String url = host + ":" + port + "/" + database + "?user=" + username + "&password=" + password + "&useSSL=false";
            connection = DriverManager.getConnection("jdbc:mysql://" + url);
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // Obtain world ID from CoreProtect database
            ResultSet resultSet = statement.executeQuery("SELECT id FROM co_world WHERE world = '" + world.getName() + "'");
            String worldID = null;
            while (resultSet.next())
            {
                worldID = String.valueOf(resultSet.getInt("id"));
            }

            // Ensure the world ID is not null
            if (worldID == null)
            {
                FLog.warning("Failed to obtain the world ID for the " + world.getName());
                return;
            }

            // Iterate through each table and delete their data if the world ID matches
            for (String table : tables)
            {
                statement.executeQuery("DELETE FROM " + table + " WHERE wid = " + worldID);
            }

            connection.close();

        }
        catch (SQLException e)
        {
            FLog.warning("Failed to delete the CoreProtect data for the " + world.getName());
        }

        // This exits for flatlands wipes
        if (shutdown)
        {
            if (plugin.amp.enabled)
            {
                plugin.amp.restartServer();
            }
            else
            {
                server.shutdown();
            }
        }
    }
}