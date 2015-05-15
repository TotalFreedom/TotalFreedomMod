package me.StevenLawson.TotalFreedomMod.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.husky.Database;
import me.husky.sqlite.SQLite;

public class TFM_SqliteDatabase
{

    private final Database sql;
    private final String table;
    private final String fields;
    private final List<Statement> statements;

    public TFM_SqliteDatabase(String filename, String table, String fields)
    {
        this.sql = new SQLite(TotalFreedomMod.plugin, filename);
        this.table = table;
        this.fields = fields;
        this.statements = new ArrayList<Statement>();
    }

    public Statement addPreparedStatement(String query)
    {
        if (sql.checkConnection())
        {
            throw new IllegalStateException("Can not add prepared statements after connecting!");
        }

        final Statement statement = new Statement(query);
        statements.add(statement);
        return statement;
    }

    @Deprecated
    public Database db()
    {
        return sql;
    }

    public boolean connect()
    {
        if (sql.checkConnection())
        {
            return true;
        }

        final Connection con = sql.openConnection();
        if (con == null)
        {
            return false;
        }

        if (!TFM_SqlUtil.hasTable(con, table))
        {
            TFM_Log.info("Creating table: " + table);

            if (!TFM_SqlUtil.createTable(con, table, fields))
            {
                TFM_Log.severe("Could not create table: " + table);
                return false;
            }
        }

        // Prepare statements
        for (Statement statement : statements)
        {
            if (!statement.prepare())
            {
                return false;
            }
        }

        return true;
    }

    public void close()
    {
        sql.closeConnection();
    }

    public int purge()
    {
        if (!connect())
        {
            return 0;
        }

        TFM_Log.warning("Truncating table: " + table);

        final int result = TFM_SqlUtil.updateQuery(sql.getConnection(), "DELETE FROM " + table + ";");

        if (result == -1)
        {
            TFM_Log.warning("Could not truncate table: " + table);
        }

        return result;
    }

    public class Statement
    {
        private final String query;
        private PreparedStatement statement;

        private Statement(String query)
        {
            this.query = query;
        }

        private boolean prepare()
        {
            try
            {
                statement = sql.getConnection().prepareStatement(query);
                return true;
            }
            catch (SQLException ex)
            {
                TFM_Log.severe("Could not prepare statement: " + query);
                TFM_Log.severe(ex);
                return false;
            }
        }

        public void invalidate()
        {
            statement = null;
            statements.remove(this);
        }

        public PreparedStatement getStatement()
        {
            return statement;
        }
    }
}
