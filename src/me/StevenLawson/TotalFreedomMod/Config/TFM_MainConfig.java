package me.StevenLawson.TotalFreedomMod.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class TFM_MainConfig
{
    public static final File CONFIG_FILE = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.CONFIG_FILENAME);
    //
    private static final EnumMap<TFM_ConfigEntry, Object> ENTRY_MAP;
    private static final TFM_Defaults DEFAULTS;

    static
    {
        ENTRY_MAP = new EnumMap<TFM_ConfigEntry, Object>(TFM_ConfigEntry.class);

        TFM_Defaults tempDefaults = null;
        try
        {
            try
            {
                InputStream defaultConfig = getDefaultConfig();
                tempDefaults = new TFM_Defaults(defaultConfig);
                for (TFM_ConfigEntry entry : TFM_ConfigEntry.values())
                {
                    ENTRY_MAP.put(entry, tempDefaults.get(entry.getConfigName()));
                }
                defaultConfig.close();
            }
            catch (IOException ex)
            {
                TFM_Log.severe(ex);
            }

            copyDefaultConfig(CONFIG_FILE);

            load();
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }

        DEFAULTS = tempDefaults;
    }

    private TFM_MainConfig()
    {
        throw new AssertionError();
    }

    public static void load()
    {
        try
        {
            YamlConfiguration config = new YamlConfiguration();

            config.load(CONFIG_FILE);

            for (TFM_ConfigEntry entry : TFM_ConfigEntry.values())
            {
                String path = entry.getConfigName();
                if (config.contains(path))
                {
                    Object value = config.get(path);
                    if (value == null || entry.getType().isAssignableFrom(value.getClass()))
                    {
                        ENTRY_MAP.put(entry, value);
                    }
                    else
                    {
                        TFM_Log.warning("Value for " + entry.getConfigName() + " is of type " + value.getClass().getSimpleName() + ". Needs to be " + entry.getType().getSimpleName() + ". Using default value.");
                    }
                }
                else
                {
                    TFM_Log.warning("Missing configuration entry " + entry.getConfigName() + ". Using default value.");
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            TFM_Log.severe(ex);
        }
        catch (IOException ex)
        {
            TFM_Log.severe(ex);
        }
        catch (InvalidConfigurationException ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static String getString(TFM_ConfigEntry entry)
    {
        try
        {
            return get(entry, String.class);
        }
        catch (IllegalArgumentException ex)
        {
            TFM_Log.severe(ex);
        }
        return null;
    }

    public static void setString(TFM_ConfigEntry entry, String value)
    {
        try
        {
            set(entry, value, String.class);
        }
        catch (IllegalArgumentException ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static Double getDouble(TFM_ConfigEntry entry)
    {
        try
        {
            return get(entry, Double.class);
        }
        catch (IllegalArgumentException ex)
        {
            TFM_Log.severe(ex);
        }
        return null;
    }

    public static void setDouble(TFM_ConfigEntry entry, Double value)
    {
        try
        {
            set(entry, value, Double.class);
        }
        catch (IllegalArgumentException ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static Boolean getBoolean(TFM_ConfigEntry entry)
    {
        try
        {
            return get(entry, Boolean.class);
        }
        catch (IllegalArgumentException ex)
        {
            TFM_Log.severe(ex);
        }
        return null;
    }

    public static void setBoolean(TFM_ConfigEntry entry, Boolean value)
    {
        try
        {
            set(entry, value, Boolean.class);
        }
        catch (IllegalArgumentException ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static Integer getInteger(TFM_ConfigEntry entry)
    {
        try
        {
            return get(entry, Integer.class);
        }
        catch (IllegalArgumentException ex)
        {
            TFM_Log.severe(ex);
        }
        return null;
    }

    public static void setInteger(TFM_ConfigEntry entry, Integer value)
    {
        try
        {
            set(entry, value, Integer.class);
        }
        catch (IllegalArgumentException ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static List getList(TFM_ConfigEntry entry)
    {
        try
        {
            return get(entry, List.class);
        }
        catch (IllegalArgumentException ex)
        {
            TFM_Log.severe(ex);
        }
        return null;
    }

    public static <T> T get(TFM_ConfigEntry entry, Class<T> type) throws IllegalArgumentException
    {
        Object value = ENTRY_MAP.get(entry);
        try
        {
            return type.cast(value);
        }
        catch (ClassCastException ex)
        {
            throw new IllegalArgumentException(entry.name() + " is not of type " + type.getSimpleName());
        }
    }

    public static <T> void set(TFM_ConfigEntry entry, T value, Class<T> type) throws IllegalArgumentException
    {
        if (!type.isAssignableFrom(entry.getType()))
        {
            throw new IllegalArgumentException(entry.name() + " is not of type " + type.getSimpleName());
        }
        if (value != null && !type.isAssignableFrom(value.getClass()))
        {
            throw new IllegalArgumentException("Value is not of type " + type.getSimpleName());
        }
        ENTRY_MAP.put(entry, value);
    }

    private static void copyDefaultConfig(File targetFile)
    {
        if (targetFile.exists())
        {
            return;
        }

        TFM_Log.info("Installing default configuration file template: " + targetFile.getPath());

        try
        {
            InputStream defaultConfig = getDefaultConfig();
            FileUtils.copyInputStreamToFile(defaultConfig, targetFile);
            defaultConfig.close();
        }
        catch (IOException ex)
        {
            TFM_Log.severe(ex);
        }
    }

    private static InputStream getDefaultConfig()
    {
        return TotalFreedomMod.plugin.getResource(TotalFreedomMod.CONFIG_FILENAME);
    }

    public static TFM_Defaults getDefaults()
    {
        return DEFAULTS;
    }

    public static class TFM_Defaults
    {
        private YamlConfiguration defaults = null;

        private TFM_Defaults(InputStream defaultConfig)
        {
            try
            {
                defaults = new YamlConfiguration();
                final InputStreamReader isr = new InputStreamReader(defaultConfig);
                defaults.load(isr);
                isr.close();
            }
            catch (IOException ex)
            {
                TFM_Log.severe(ex);
            }
            catch (InvalidConfigurationException ex)
            {
                TFM_Log.severe(ex);
            }
        }

        public Object get(String path)
        {
            return defaults.get(path);
        }
    }
}
