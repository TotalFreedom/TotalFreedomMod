package me.StevenLawson.TotalFreedomMod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class TFM_Config
{
    public static final String CONFIG_FILENAME = "config.yml";
    public static final File CONFIG_FILE = new File(TotalFreedomMod.plugin.getDataFolder(), CONFIG_FILENAME);
    //
    private final EnumMap<TFM_ConfigEntry, Object> configEntryMap = new EnumMap<TFM_ConfigEntry, Object>(TFM_ConfigEntry.class);

    public static enum TFM_ConfigEntry
    {
        ADMIN_ONLY_MODE(Boolean.class, "admin_only_mode"),
        ALLOW_EXPLOSIONS(Boolean.class, "allow_explosions"),
        ALLOW_FIRE_PLACE(Boolean.class, "allow_fire_place"),
        ALLOW_FIRE_SPREAD(Boolean.class, "allow_fire_spread"),
        ALLOW_FLIUD_SPREAD(Boolean.class, "allow_fluid_spread"),
        ALLOW_LAVA_DAMAGE(Boolean.class, "allow_lava_damage"),
        ALLOW_LAVA_PLACE(Boolean.class, "allow_lava_place"),
        ALLOW_TNT_MINECARTS(Boolean.class, "allow_tnt_minecarts"),
        ALLOW_WATER_PLACE(Boolean.class, "allow_water_place"),
        AUTO_ENTITY_WIPE(Boolean.class, "auto_wipe"),
        AUTO_PROTECT_SPAWNPOINTS(Boolean.class, "auto_protect_spawnpoints"),
        DISABLE_NIGHT(Boolean.class, "disable_night"),
        DISABLE_WEATHER(Boolean.class, "disable_weather"),
        GENERATE_FLATLANDS(Boolean.class, "generate_flatlands"),
        LANDMINES_ENABLED(Boolean.class, "landmines_enabled"),
        MOB_LIMITER_DISABLE_DRAGON(Boolean.class, "mob_limiter_disable_dragon"),
        MOB_LIMITER_DISABLE_GHAST(Boolean.class, "mob_limiter_disable_ghast"),
        MOB_LIMITER_DISABLE_GIANT(Boolean.class, "mob_limiter_disable_giant"),
        MOB_LIMITER_DISABLE_SLIME(Boolean.class, "mob_limiter_disable_slime"),
        MOB_LIMITER_ENABLED(Boolean.class, "mob_limiter_enabled"),
        MP44_ENABLED(Boolean.class, "mp44_enabled"),
        NUKE_MONITOR(Boolean.class, "nuke_monitor"),
        PET_PROTECT_ENABLED(Boolean.class, "pet_protect_enabled"),
        PREPROCESS_LOG_ENABLED(Boolean.class, "preprocess_log"),
        PROTECTED_AREAS_ENABLED(Boolean.class, "protected_areas_enabled"),
        TOSSMOB_ENABLED(Boolean.class, "tossmob_enabled"),
        TWITTERBOT_ENABLED(Boolean.class, "twitterbot_enabled"),
        //
        AUTO_PROTECT_RADIUS(Double.class, "auto_protect_radius"),
        EXPLOSIVE_RADIUS(Double.class, "explosiveRadius"),
        NUKE_MONITOR_RANGE(Double.class, "nuke_monitor_range"),
        //
        FREECAM_TRIGGER_COUNT(Integer.class, "freecam_trigger_count"),
        MOB_LIMITER_MAX(Integer.class, "mob_limiter_max"),
        NUKE_MONITOR_COUNT_BREAK(Integer.class, "nuke_monitor_count_break"),
        NUKE_MONITOR_COUNT_PLACE(Integer.class, "nuke_monitor_count_place"),
        //
        FLATLANDS_GENERATION_PARAMS(String.class, "flatlands_generation_params"),
        LOGS_REGISTER_PASSWORD(String.class, "logs_register_password"),
        LOGS_REGISTER_URL(String.class, "logs_register_url"),
        SERVICE_CHECKER_URL(String.class, "service_checker_url"),
        TWITTERBOT_SECRET(String.class, "twitterbot_secret"),
        TWITTERBOT_URL(String.class, "twitterbot_url"),
        //
        BLOCKED_COMMANDS(List.class, "blocked_commands"),
        HOST_SENDER_NAMES(List.class, "host_sender_names");
        //
        private final Class<?> type;
        private final String configName;

        private TFM_ConfigEntry(Class<?> type, String configName)
        {
            this.type = type;
            this.configName = configName;
        }

        public Class<?> getType()
        {
            return type;
        }

        public String getConfigName()
        {
            return configName;
        }
    }

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
            FileUtils.copyInputStreamToFile(getDefaultConfig(), targetFile);
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
