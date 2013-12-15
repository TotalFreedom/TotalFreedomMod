package me.StevenLawson.TotalFreedomMod;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class TFM_LogFile
{
    public static final int MAX_LOG_SIZE = 1024 * 1024; // Bytes
    private final Logger logger;
    private final SimpleDateFormat date;

    private TFM_LogFile()
    {
        this.logger = TotalFreedomMod.server.getLogger();
        this.date = new SimpleDateFormat("HH:mm:ss");
    }

    public void start()
    {
        try
        {
            logger.addHandler(getHandler());
        }
        catch (Exception ex)
        {
            TFM_Log.warning("Failed to register log handler!");
            TFM_Log.warning(TotalFreedomMod.pluginName + " will not log to /server.log!");
            TFM_Log.warning(ex);
        }
    }

    private FileHandler getHandler() throws SecurityException, IOException
    {
        final FileHandler handler = new FileHandler("server.log", MAX_LOG_SIZE, 1);
        handler.setLevel(Level.ALL);
        handler.setFormatter(getFormatter());
        return handler;
    }

    private Formatter getFormatter()
    {
        return new Formatter()
        {
            @Override
            public String format(LogRecord record)  // org.bukkit.craftbukkit.util.ShortConsoleFormatter
            {
                StringBuilder builder = new StringBuilder();
                Throwable ex = record.getThrown();

                builder.append(date.format(record.getMillis()));
                builder.append(" [");
                builder.append(record.getLevel().getLocalizedName().toUpperCase());
                builder.append("] ");
                builder.append(formatMessage(record));
                builder.append('\n');

                if (ex != null)
                {
                    StringWriter writer = new StringWriter();
                    ex.printStackTrace(new PrintWriter(writer));
                    builder.append(writer);
                }

                return builder.toString();
            }
        };
    }

    public static TFM_LogFile getInstance()
    {
        return TFM_LogFileHolder.INSTANCE;
    }

    private static class TFM_LogFileHolder
    {
        private static final TFM_LogFile INSTANCE = new TFM_LogFile();
    }
}
