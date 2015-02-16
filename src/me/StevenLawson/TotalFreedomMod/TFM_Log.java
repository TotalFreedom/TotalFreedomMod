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

    // Level.INFO:
    public static void info(String message)
    {
        info(message, false);
    }

    public static void info(String message, Boolean raw)
    {
        log(Level.INFO, message, raw);
    }

    public static void info(Throwable ex)
    {
        log(Level.INFO, ex);
    }

    // Level.WARNING:
    public static void warning(String message)
    {
        warning(message, false);
    }

    public static void warning(String message, Boolean raw)
    {
        log(Level.WARNING, message, raw);
    }

    public static void warning(Throwable ex)
    {
        log(Level.WARNING, ex);
    }

    // Level.SEVERE:
    public static void severe(String message)
    {
        severe(message, false);
    }

    public static void severe(String message, Boolean raw)
    {
        log(Level.SEVERE, message, raw);
    }

    public static void severe(Throwable ex)
    {
        log(Level.SEVERE, ex);
    }

    // Utility
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

    public static Logger getPluginLogger()
    {
        return (pluginLogger != null ? pluginLogger : FALLBACK_LOGGER);
    }

    public static Logger getServerLogger()
    {
        return (serverLogger != null ? serverLogger : FALLBACK_LOGGER);
    }
}
