package me.StevenLawson.TotalFreedomMod;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TFM_Log
{
    private static final Logger FALLBACK_LOGGER = Logger.getLogger("Minecraft-Server");
    private static Logger serverLogger = null;
    private static Logger pluginLogger = null;

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
        getLogger(raw).log(level, message);
    }

    private static void log(Level level, Throwable throwable)
    {
        getLogger(false).log(level, null, throwable);
    }

    public static void setServerLogger(Logger logger)
    {
        serverLogger = logger;
    }

    public static void setPluginLogger(Logger logger)
    {
        pluginLogger = logger;
    }

    private static Logger getLogger(boolean raw)
    {
        if (raw || pluginLogger == null)
        {
            return (serverLogger != null ? serverLogger : FALLBACK_LOGGER);
        }
        else
        {
            return pluginLogger;
        }
    }
}
