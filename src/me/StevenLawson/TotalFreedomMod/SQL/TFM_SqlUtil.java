package me.StevenLawson.TotalFreedomMod.SQL;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.StevenLawson.TotalFreedomMod.TFM_Log;

public class TFM_SqlUtil
{

    public static boolean hasTable(Connection con, String table)
    {
        try
        {
            final DatabaseMetaData dbm = con.getMetaData();
            final ResultSet tables = dbm.getTables(null, null, table, null);
            return tables.next();
        }
        catch (SQLException ex)
        {
            TFM_Log.severe(ex);
            return false;
        }
    }

    public static ResultSet executeQuery(Connection con, String query)
    {
        try
        {
            return con.createStatement().executeQuery(query);
        }
        catch (SQLException ex)
        {
            TFM_Log.severe(ex);
            return null;
        }
    }

    public static int updateQuery(Connection con, String query)
    {
        try
        {
            return con.createStatement().executeUpdate(query);
        }
        catch (SQLException ex)
        {
            TFM_Log.severe(ex);
            return -1;
        }
    }

    public static boolean createTable(Connection con, String name, String fields)
    {
        try
        {
            con.createStatement().execute("CREATE TABLE " + name + " (" + fields + ");");
            return true;
        }
        catch (SQLException ex)
        {
            TFM_Log.severe(ex);
            return false;
        }
    }

    public static void close(ResultSet result)
    {
        if (result == null)
        {
            return;
        }

        try
        {
            result.close();
        }
        catch (SQLException ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static boolean hasData(ResultSet result)
    {
        if (result == null)
        {
            return false;
        }

        try
        {
            return result.next();
        }
        catch (SQLException ex)
        {
            TFM_Log.severe(ex);
            return false;
        }
    }

}
