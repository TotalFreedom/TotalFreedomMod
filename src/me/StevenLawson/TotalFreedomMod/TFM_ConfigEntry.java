package me.StevenLawson.TotalFreedomMod;

import java.util.List;

public enum TFM_ConfigEntry
{
    ADMIN_ONLY_MODE(Boolean.class, "admin_only_mode"),
    ALLOW_EXPLOSIONS(Boolean.class, "allow_explosions"),
    ALLOW_FIRE_PLACE(Boolean.class, "allow_fire_place"),
    ALLOW_FIRE_SPREAD(Boolean.class, "allow_fire_spread"),
    ALLOW_FLUID_SPREAD(Boolean.class, "allow_fluid_spread"),
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
    HTTPD_ENABLED(Boolean.class, "httpd_enabled"),
    AUTOKICK_ENABLED(Boolean.class, "autokick_enabled"),
    //
    AUTO_PROTECT_RADIUS(Double.class, "auto_protect_radius"),
    EXPLOSIVE_RADIUS(Double.class, "explosive_radius"),
    NUKE_MONITOR_RANGE(Double.class, "nuke_monitor_range"),
    AUTOKICK_THRESHOLD(Double.class, "autokick_threshold"),
    //
    FREECAM_TRIGGER_COUNT(Integer.class, "freecam_trigger_count"),
    MOB_LIMITER_MAX(Integer.class, "mob_limiter_max"),
    NUKE_MONITOR_COUNT_BREAK(Integer.class, "nuke_monitor_count_break"),
    NUKE_MONITOR_COUNT_PLACE(Integer.class, "nuke_monitor_count_place"),
    HTTPD_PORT(Integer.class, "httpd_port"),
    AUTOKICK_TIME(Integer.class, "autokick_time"),
    //
    FLATLANDS_GENERATION_PARAMS(String.class, "flatlands_generation_params"),
    LOGS_REGISTER_PASSWORD(String.class, "logs_register_password"),
    LOGS_REGISTER_URL(String.class, "logs_register_url"),
    SERVICE_CHECKER_URL(String.class, "service_checker_url"),
    TWITTERBOT_SECRET(String.class, "twitterbot_secret"),
    TWITTERBOT_URL(String.class, "twitterbot_url"),
    HTTPD_PUBLIC_FOLDER(String.class, "httpd_public_folder"),
    //
    BLOCKED_COMMANDS(List.class, "blocked_commands"),
    HOST_SENDER_NAMES(List.class, "host_sender_names"),
    UNBANNABLE_USERNAMES(List.class, "unbannable_usernames");
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

    public String getString()
    {
        return TFM_Config.getInstance().getString(this);
    }

    public String setString(String value)
    {
        TFM_Config.getInstance().setString(this, value);
        return value;
    }

    public Double getDouble()
    {
        return TFM_Config.getInstance().getDouble(this);
    }

    public Double setDouble(Double value)
    {
        TFM_Config.getInstance().setDouble(this, value);
        return value;
    }

    public Boolean getBoolean()
    {
        return TFM_Config.getInstance().getBoolean(this);
    }

    public Boolean setBoolean(Boolean value)
    {
        TFM_Config.getInstance().setBoolean(this, value);
        return value;
    }

    public Integer getInteger()
    {
        return TFM_Config.getInstance().getInteger(this);
    }

    public Integer setInteger(Integer value)
    {
        TFM_Config.getInstance().setInteger(this, value);
        return value;
    }

    public List getList()
    {
        return TFM_Config.getInstance().getList(this);
    }

    public static TFM_ConfigEntry findConfigEntry(String name)
    {
        name = name.toLowerCase().replace("_", "");
        for (TFM_ConfigEntry entry : values())
        {
            if (entry.toString().toLowerCase().replace("_", "").equals(name))
            {
                return entry;
            }
        }
        return null;
    }
}
