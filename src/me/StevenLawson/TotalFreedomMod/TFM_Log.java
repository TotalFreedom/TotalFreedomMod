package me.StevenLawson.TotalFreedomMod;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

public class TFM_Log
{
    private TFM_Log()
    {
        throw new AssertionError();
    }

    public static void info(Object... params)
    {
        prepareLogMessage(Level.INFO, params);
    }

    public static void warning(Object... params)
    {
        prepareLogMessage(Level.WARNING, params);
    }

    public static void severe(Object... params)
    {
        prepareLogMessage(Level.SEVERE, params);
    }

    private static void prepareLogMessage(Level level, Object... params)
    {
        if (params.length == 0)
        {
            return;
        }

        Object payload = params[0];

        if (payload instanceof Throwable)
        {
            log(level, (Throwable) payload);
        }
        else
        {
            log(level, payload.toString(), params.length >= 2 && params[1] instanceof Boolean ? (Boolean) params[1] : false);
        }
    }

    private static void log(Level level, String message, boolean raw)
    {
        LoggerType.getLogger(raw).log(level, message);
    }

    private static void log(Level level, Throwable throwable)
    {
        LoggerType.SERVER.getLogger().log(level, null, throwable);
    }

    private static enum LoggerType
    {
        SERVER(Bukkit.getLogger()),
        PLUGIN(TotalFreedomMod.plugin.getLogger());
        //
        private final Logger logger;

        private LoggerType(Logger logger)
        {
            this.logger = logger;
        }

        public Logger getLogger()
        {
            return logger;
        }

        public static Logger getLogger(boolean getRawLogger)
        {
            return (getRawLogger ? SERVER.getLogger() : PLUGIN.getLogger());
        }
    }
}
