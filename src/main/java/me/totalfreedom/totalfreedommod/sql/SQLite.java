package me.totalfreedom.totalfreedommod.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;

public class SQLite extends FreedomService
{
    private final String FILE_NAME = "database.db";

    private Connection connection;

    public SQLite(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        connect();
        checkTables();
    }

    @Override
    protected void onStop()
    {
        disconnect();
    }

    public void connect()
    {

        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/" + FILE_NAME);
            FLog.info("Successfully connected to the database.");
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to connect to the database: " + e.getMessage());
            FLog.info("Successfully disconnected from the database.");
        }
    }

    public void disconnect()
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
            FLog.severe("Failed to disconnect from the database: " + e.getMessage());
        }
    }

    public void checkTables()
    {
        try
        {
            DatabaseMetaData meta = connection.getMetaData();
            if (!tableExists(meta, "bans"))
            {
                try
                {
                    connection.createStatement().execute("CREATE TABLE `bans` ( `name` VARCHAR NOT NULL, `ips` VARCHAR, `by` VARCHAR NOT NULL, `at` LONG NOT NULL, `expires` LONG, `reason` VARCHAR );");
                }
                catch (SQLException e)
                {
                    FLog.severe("Failed to create the bans table: " + e.getMessage());
                }
            }

            if (!tableExists(meta, "admins"))
            {
                try
                {
                    connection.createStatement().execute("CREATE TABLE `admins` (`username` VARCHAR NOT NULL, `ips` VARCHAR NOT NULL, `rank` VARCHAR NOT NULL, `active` BOOLEAN NOT NULL, `last_login` LONG NOT NULL, `login_message` VARCHAR, `tag` VARCHAR, `discord_id` VARCHAR, `backup_codes` VARCHAR, `command_spy` BOOLEAN NOT NULL, `potion_spy` BOOLEAN NOT NULL, `ac_format` VARCHAR, `old_tags` BOOLEAN NOT NULL, `log_stick` BOOLEAN NOT NULL, `discord_chat` BOOLEAN NOT NULL);");
                }
                catch (SQLException e)
                {
                    FLog.severe("Failed to create the admins table: " + e.getMessage());
                }
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to check tables on database: " + e.getMessage());
        }
    }

    public void truncate(String table)
    {
        try
        {
            connection.createStatement().execute("TRUNCATE TABLE " + table);
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to truncate " + table + ": " + e.getMessage());
        }
    }

    public ResultSet getBanList() throws SQLException
    {
        return connection.createStatement().executeQuery("SELECT * FROM bans");
    }


    public ResultSet getAdminList() throws SQLException
    {
        return connection.createStatement().executeQuery("SELECT * FROM admins");
    }

    public void setAdminValue(Admin admin, String key, Object value)
    {
        try
        {
            Object[] data = {key, admin.getName()};
            PreparedStatement statement = connection.prepareStatement(MessageFormat.format("UPDATE admins SET {0}=? WHERE username=''{1}''", data));
            statement = setUnknownType(statement, 1, value);
            statement.executeUpdate();

        }
        catch (SQLException e)
        {
            FLog.severe("Failed to update value: " + e.getMessage());
        }
    }

    public void updateAdminName(String oldName, String newName)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(MessageFormat.format("UPDATE admins SET username=? WHERE username=''{0}''", oldName));
            statement = setUnknownType(statement, 1, newName);
            statement.executeUpdate();

        }
        catch (SQLException e)
        {
            FLog.severe("Failed to update value: " + e.getMessage());
        }
    }

    public PreparedStatement setUnknownType(PreparedStatement statement, int index, Object value) throws SQLException
    {
        if (value.getClass().equals(String.class))
        {
            String v = (String)value;
            statement.setString(index, v);
        }
        else if (value.getClass().equals(Integer.class))
        {
            int v = (int)value;
            statement.setInt(index, v);
        }
        else if (value.getClass().equals(Boolean.class))
        {
            boolean v = (boolean)value;
            statement.setBoolean(index, v);
        }
        else if (value.getClass().equals(Long.class))
        {
            long v = (long)value;
            statement.setLong(index, v);
        }
        return statement;
    }

    public Object getValue(ResultSet resultSet, String key, Object value) throws SQLException
    {
        Object result = null;
        if (value instanceof String)
        {
            result = resultSet.getString(key);
        }
        else if (value instanceof Integer)
        {
            result = resultSet.getInt(key);
        }
        else if (value instanceof Boolean)
        {
            result = resultSet.getBoolean(key);
        }
        else if (value instanceof Long)
        {
            result = resultSet.getLong(key);
        }
        return result;
    }

    public void addAdmin(Admin admin)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO admins VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, admin.getName());
            statement.setString(2, FUtil.listToString(admin.getIps()));
            statement.setString(3, admin.getRank().toString());
            statement.setBoolean(4, admin.isActive());
            statement.setLong(5, admin.getLastLogin().getTime());
            statement.setString(6, admin.getLoginMessage());
            statement.setString(7, admin.getTag());
            statement.setString(8, admin.getDiscordID());
            statement.setString(9, FUtil.listToString(admin.getBackupCodes()));
            statement.setBoolean(10, admin.getCommandSpy());
            statement.setBoolean(11, admin.getPotionSpy());
            statement.setString(12, admin.getAcFormat());
            statement.setBoolean(13, admin.getOldTags());
            statement.setBoolean(14, admin.getLogStick());
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to add admin: " + e.getMessage());
        }
    }

    public ResultSet getAdminByName(String name)
    {
        try
        {
            ResultSet resultSet = connection.createStatement().executeQuery(MessageFormat.format("SELECT * FROM admins WHERE username=''{0}''", name));
            if (resultSet.next())
            {
                return resultSet;
            }
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to get admin by name: " + e.getMessage());
        }

        return null;
    }

    public void removeAdmin(Admin admin)
    {
        Object[] data = {admin.getName(), FUtil.listToString(admin.getBackupCodes())};
        try
        {
            connection.createStatement().executeUpdate(MessageFormat.format("DELETE FROM bans where name=''{0}'' and ips=''{1}''", data));
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to remove admin: " + e.getMessage());
        }
    }

    public void addBan(Ban ban)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO bans VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, ban.getUsername());
            statement.setString(2, FUtil.listToString(ban.getIps()));
            statement.setString(3, ban.getBy());
            statement.setLong(4, ban.getAt().getTime());
            statement.setLong(5, ban.getExpiryUnix());
            statement.setString(6, ban.getReason());
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to add ban: " + e.getMessage());
        }
    }

    public void removeBan(Ban ban)
    {
        Object[] data = {ban.getUsername(), String.join(", ", ban.getIps())};
        try
        {
            connection.createStatement().executeUpdate(MessageFormat.format("DELETE FROM bans where name=''{0}'' and ips=''{1}''", data));
        }
        catch (SQLException e)
        {
            FLog.severe("Failed to remove ban: " + e.getMessage());
        }
    }

    public boolean tableExists(DatabaseMetaData meta, String name) throws SQLException
    {
        return meta.getTables(null, null, name, null).next();
    }
}