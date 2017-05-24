package me.totalfreedom.totalfreedommod;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class CoreProtectDatabase extends FreedomService
{

    private long interval;
    private boolean enabled;
    private long limit;
    private BukkitTask checker;
    public File file = new File(server.getPluginManager().getPlugin("CoreProtect").getDataFolder(), "database.db");

    public CoreProtectDatabase(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        interval = 10 * 20L;
        enabled = ConfigEntry.COREPROTECT_WIPER_ENABLED.getBoolean();
        limit = ConfigEntry.COREPROTECT_FILE_LIMIT.getInteger();
        if (!enabled)
        {
            return;
        }
        checker = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                double bytes = file.length();
                double kilobytes = (bytes / 1024);
                double megabytes = (kilobytes / 1024);
                double gigabytes = (megabytes / 1024);
                if (gigabytes > limit)
                {
                    FUtil.bcastMsg("The CoreProtect log file has grown too big for the server to cope, it will be wiped.", ChatColor.RED);
                    final PluginManager pm = server.getPluginManager();
                    final Plugin target = pm.getPlugin("CoreProtect");
                    pm.disablePlugin(target);
                    File coreFile = new File(server.getPluginManager().getPlugin("CoreProtect").getDataFolder() + File.separator + "database.db");
                    FUtil.deleteFile(coreFile);
                    pm.enablePlugin(target);
                }

            }
        }.runTaskTimer(plugin, interval, interval);
    }

    @Override
    protected void onStop()
    {
        if (checker == null)
        {
            return;
        }

        FUtil.cancel(checker);
        checker = null;
    }

    public void deleteFlatlandsDatabase()
    {
        Connection connection = null;
        try
        {

            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate("delete from co_sign where wid = 2");
            statement.executeUpdate("delete from co_session where wid = 2");
            statement.executeUpdate("delete from co_container where wid = 2");
            statement.executeUpdate("delete from co_block where wid = 2");

        }
        catch (SQLException e)
        {
            System.out.println("Failed to delete CoreProtect log file.ed to delete CoreProtect log file.");
        }
        finally
        {
            try
            {
                if (connection != null)
                {
                    connection.close();
                }
            }
            catch (SQLException e)
            {
                System.out.println("Failed to delete CoreProtect log file.");
            }
        }
    }

    public void deleteOtherData()
    {
        Connection connection = null;
        try
        {

            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate("delete from co_art_map");
            statement.executeUpdate("delete from co_chat");
            statement.executeUpdate("delete from co_command");
            statement.executeUpdate("delete from co_entity");
            statement.executeUpdate("delete from co_entity_map");
            statement.executeUpdate("delete from co_material_map");
            statement.executeUpdate("delete from co_skull");
            statement.executeUpdate("delete from co_user");
            statement.executeUpdate("delete from co_username_log");

        }
        catch (SQLException e)
        {
            System.out.println("Failed to delete CoreProtect log file.");
        }
        finally
        {
            try
            {
                if (connection != null)
                {
                    connection.close();
                }
            }
            catch (SQLException e)
            {
                System.out.println("Failed to delete CoreProtect log file.");
            }
        }
    }

    public void purgeDatabaseWithoutDeleting()
    {
        Connection connection = null;
        try
        {

            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate("delete from co_sign");
            statement.executeUpdate("delete from co_session");
            statement.executeUpdate("delete from co_container");
            statement.executeUpdate("delete from co_block");
            statement.executeUpdate("delete from co_art_map");
            statement.executeUpdate("delete from co_chat");
            statement.executeUpdate("delete from co_command");
            statement.executeUpdate("delete from co_entity");
            statement.executeUpdate("delete from co_entity_map");
            statement.executeUpdate("delete from co_material_map");
            statement.executeUpdate("delete from co_skull");
            statement.executeUpdate("delete from co_user");
            statement.executeUpdate("delete from co_username_log");

        }
        catch (SQLException e)
        {
            System.out.println("Failed to delete CoreProtect log file.");
        }
        finally
        {
            try
            {
                if (connection != null)
                {
                    connection.close();
                }
            }
            catch (SQLException e)
            {
                System.out.println("Failed to delete CoreProtect log file.");
            }
        }
    }
}
