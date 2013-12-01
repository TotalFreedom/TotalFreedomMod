package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class TFM_Config
{
    public static final String CONFIG_FILENAME = "config.yml";
    public static final File CONFIG_FILE = new File(TotalFreedomMod.plugin.getDataFolder(), CONFIG_FILENAME);
    //
    private final EnumMap<TFM_ConfigEntry, Object> configEntryMap = new EnumMap<TFM_ConfigEntry, Object>(TFM_ConfigEntry.class);

    private TFM_Config()
    {
        try
        {
            try
            {
                InputStream defaultConfig = getDefaultConfig();
                TFM_Config_DefaultsLoader defaultsLoader = new TFM_Config_DefaultsLoader(defaultConfig);
                for (TFM_ConfigEntry entry : TFM_ConfigEntry.values())
                {
                    configEntryMap.put(entry, defaultsLoader.get(entry.getConfigName()));
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
    }

    public final void load()
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
                        configEntryMap.put(entry, value);
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

    public String getString(TFM_ConfigEntry entry)
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

    public void setString(TFM_ConfigEntry entry, String value)
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

    public Double getDouble(TFM_ConfigEntry entry)
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

    public void setDouble(TFM_ConfigEntry entry, Double value)
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

    public Boolean getBoolean(TFM_ConfigEntry entry)
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

    public void setBoolean(TFM_ConfigEntry entry, Boolean value)
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

    public Integer getInteger(TFM_ConfigEntry entry)
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

    public void setInteger(TFM_ConfigEntry entry, Integer value)
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

    public List getList(TFM_ConfigEntry entry)
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

    public <T> T get(TFM_ConfigEntry entry, Class<T> type) throws IllegalArgumentException
    {
        Object value = configEntryMap.get(entry);
        try
        {
            return type.cast(value);
        }
        catch (ClassCastException ex)
        {
            throw new IllegalArgumentException(entry.name() + " is not of type " + type.getSimpleName());
        }
    }

    public <T> void set(TFM_ConfigEntry entry, T value, Class<T> type) throws IllegalArgumentException
    {
        if (!type.isAssignableFrom(entry.getType()))
        {
            throw new IllegalArgumentException(entry.name() + " is not of type " + type.getSimpleName());
        }
        if (value != null && !type.isAssignableFrom(value.getClass()))
        {
            throw new IllegalArgumentException("Value is not of type " + type.getSimpleName());
        }
        configEntryMap.put(entry, value);
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
        return TotalFreedomMod.plugin.getResource(CONFIG_FILENAME);
    }

    private static class TFM_Config_DefaultsLoader
    {
        private YamlConfiguration defaults = null;

        public TFM_Config_DefaultsLoader(InputStream defaultConfig)
        {
            try
            {
                defaults = new YamlConfiguration();
                defaults.load(defaultConfig);
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

    public static TFM_Config getInstance()
    {
        return TFM_ConfigHolder.INSTANCE;
    }

    private static class TFM_ConfigHolder
    {
        private static final TFM_Config INSTANCE = new TFM_Config();
    }
}
