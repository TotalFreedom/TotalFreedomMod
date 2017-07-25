package me.totalfreedom.totalfreedommod.bridge;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.PluginManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.File;
import java.util.List;
import java.util.Arrays;
import me.totalfreedom.totalfreedommod.util.FUtil;

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
        if (ConfigEntry.COREPROTECT_AUTO_WIPING_ENABLED.getBoolean() && getCoreProtect() != null)
        {
            createAutomaticWiper();
        }
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

    // Rollback the specifed player's edits that were in the last 24 hours.
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
    
    // Reverts a rollback for the specifed player's edits that were in the last 24 hours.
    public void undoRollback(final String name)
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

        return(new File(getCoreProtect().getDataFolder(), "database.db"));
    }
    
    private void createAutomaticWiper()
    {
        final long interval = 10 * 20L;
        final File databaseFile = getDatabase();

        wiper = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final CoreProtect coreProtect = getCoreProtect();
                double bytes = databaseFile.length();
                double kilobytes = (bytes / 1024);
                double megabytes = (kilobytes / 1024);
                double gigabytes = (megabytes / 1024);
                if (gigabytes > ConfigEntry.COREPROTECT_FILE_LIMIT.getInteger())
                {
                    FLog.info("The CoreProtect log file has grown too big for the server to cope, the data will be wiped!");
                    PluginManager pluginManager = server.getPluginManager();
                    pluginManager.disablePlugin(coreProtect);
                    FUtil.deleteFolder(databaseFile);
                    pluginManager.enablePlugin(coreProtect);
                }
            }
        }.runTaskTimer(plugin, interval, interval);
    }
    
    // Wipes DB for the specified world
    public void clearDatabase(World world)
    {
        clearDatabase(world, false);
    }

    // Wipes DB for the specified world
    public void clearDatabase(World world, Boolean shutdown)
    {
        final CoreProtect coreProtect = getCoreProtect();

        if (coreProtect == null)
        {
            return;
        }

        /* As CoreProtect doesn't have an api method for deleting all of the data for a specific world
           we have to do this manually via sql */
        File databaseFile = getDatabase();
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
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
                statement.executeUpdate("DELETE FROM " + table + " WHERE wid = " + worldID);
            }

            // This shrinks down the file size
            statement.executeUpdate("VACUUM");

            connection.close();

        }
        catch (SQLException e)
        {
            FLog.warning("Failed to delete the CoreProtect data for the " + world.getName());
        }

        // This exits for flatlands wipes
        if (shutdown)
        {
            server.shutdown();
        }
    }
}
