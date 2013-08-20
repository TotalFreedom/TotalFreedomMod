package me.StevenLawson.TotalFreedomMod;

import java.util.logging.Logger;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;

public class TFM_Log
{
    private static final Logger logger = Bukkit.getLogger();

    private TFM_Log()
    {
        throw new AssertionError();
    }

    public static void info(String message)
    {
        TotalFreedomMod.logger.info(message);
    }
    
    public static void info(String message, boolean raw)
    {
        if (raw)
        {
            TotalFreedomMod.logger.info(message);
        }
        else
        {
            info(message);
        }
    }

    public static void severe(Object message)
    {
        if (message instanceof Throwable)
        {
            TotalFreedomMod.logger.severe(ExceptionUtils.getFullStackTrace((Throwable) message));
        }
        else
        {
            TotalFreedomMod.logger.severe(String.valueOf(message));
        }
    }

    public static void warning(Object message)
    {
        if (message instanceof Throwable)
        {
            TotalFreedomMod.logger.warning(ExceptionUtils.getFullStackTrace((Throwable) message));
        }
        else
        {
            TotalFreedomMod.logger.warning(String.valueOf(message));
        }
    }
}
