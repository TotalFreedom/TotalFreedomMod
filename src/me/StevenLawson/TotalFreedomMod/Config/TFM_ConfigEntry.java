package me.StevenLawson.TotalFreedomMod.Config;

import java.util.List;

public enum TFM_ConfigEntry
{
    ADMIN_ONLY_MODE(Boolean.class, "admin_only_mode"),
    ALLOW_EXPLOSIONS(Boolean.class, "allow.explosions"),
    ALLOW_FIRE_PLACE(Boolean.class, "allow.fire_place"),
    ALLOW_FIRE_SPREAD(Boolean.class, "allow.fire_spread"),
    ALLOW_FLUID_SPREAD(Boolean.class, "allow.fluid_spread"),
    ALLOW_LAVA_DAMAGE(Boolean.class, "allow.lava_damage"),
    ALLOW_LAVA_PLACE(Boolean.class, "allow.lava_place"),
    ALLOW_TNT_MINECARTS(Boolean.class, "allow.tnt_minecarts"),
    ALLOW_WATER_PLACE(Boolean.class, "allow.water_place"),
    AUTO_ENTITY_WIPE(Boolean.class, "auto_wipe"),
    AUTO_PROTECT_SPAWNPOINTS(Boolean.class, "protectarea.auto_protect_spawnpoints"),
    DISABLE_NIGHT(Boolean.class, "disable.night"),
    DISABLE_WEATHER(Boolean.class, "disable.weather"),
    FLATLANDS_GENERATE(Boolean.class, "flatlands.generate"),
    LANDMINES_ENABLED(Boolean.class, "landmines_enabled"),
    MOB_LIMITER_DISABLE_DRAGON(Boolean.class, "moblimiter.disable.dragon"),
    MOB_LIMITER_DISABLE_GHAST(Boolean.class, "moblimiter.disable.ghast"),
    MOB_LIMITER_DISABLE_GIANT(Boolean.class, "moblimiter.disable.giant"),
    MOB_LIMITER_DISABLE_SLIME(Boolean.class, "mob.limiter.disable.slime"),
    MOB_LIMITER_ENABLED(Boolean.class, "mob_.limiter.enabled"),
    MP44_ENABLED(Boolean.class, "mp44_enabled"),
    NUKE_MONITOR_ENABLED(Boolean.class, "nukemonitor.enabled"),
    PET_PROTECT_ENABLED(Boolean.class, "petprotect.enabled"),
    PREPROCESS_LOG_ENABLED(Boolean.class, "preprocess_log"),
    PROTECTED_AREAS_ENABLED(Boolean.class, "protectarea.enabled"),
    TOSSMOB_ENABLED(Boolean.class, "tossmob_enabled"),
    TWITTERBOT_ENABLED(Boolean.class, "twitterbot.enabled"),
    HTTPD_ENABLED(Boolean.class, "httpd_enabled"),
    AUTOKICK_ENABLED(Boolean.class, "autokick.enabled"),
    CONSOLE_IS_SENIOR(Boolean.class, "console_is_senior"),
    //
    AUTO_PROTECT_RADIUS(Double.class, "autoprotect.auto_protect_radius"),
    EXPLOSIVE_RADIUS(Double.class, "explosive_radius"),
    NUKE_MONITOR_RANGE(Double.class, "nukemonitor.range"),
    AUTOKICK_THRESHOLD(Double.class, "autokick.threshold"),
    //
    FREECAM_TRIGGER_COUNT(Integer.class, "freecam_trigger_count"),
    MOB_LIMITER_MAX(Integer.class, "moblimiter.max"),
    NUKE_MONITOR_COUNT_BREAK(Integer.class, "nukemonitor.count_break"),
    NUKE_MONITOR_COUNT_PLACE(Integer.class, "nukemonitor.count_place"),
    HTTPD_PORT(Integer.class, "httpd_port"),
    AUTOKICK_TIME(Integer.class, "autokick.time"),
    //
    FLATLANDS_GENERATE_PARAMS(String.class, "flatlands.generate_params"),
    LOGS_REGISTER_PASSWORD(String.class, "logs_register_password"),
    LOGS_REGISTER_URL(String.class, "logs_register_url"),
    SERVICE_CHECKER_URL(String.class, "service_checker_url"),
    TWITTERBOT_SECRET(String.class, "twitterbot.secret"),
    TWITTERBOT_URL(String.class, "twitterbot.url"),
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
        return TFM_MainConfig.getInstance().getString(this);
    }

    public String setString(String value)
    {
        TFM_MainConfig.getInstance().setString(this, value);
        return value;
    }

    public Double getDouble()
    {
        return TFM_MainConfig.getInstance().getDouble(this);
    }

    public Double setDouble(Double value)
    {
        TFM_MainConfig.getInstance().setDouble(this, value);
        return value;
    }

    public Boolean getBoolean()
    {
        return TFM_MainConfig.getInstance().getBoolean(this);
    }

    public Boolean setBoolean(Boolean value)
    {
        TFM_MainConfig.getInstance().setBoolean(this, value);
        return value;
    }

    public Integer getInteger()
    {
        return TFM_MainConfig.getInstance().getInteger(this);
    }

    public Integer setInteger(Integer value)
    {
        TFM_MainConfig.getInstance().setInteger(this, value);
        return value;
    }

    public List<?> getList()
    {
        return TFM_MainConfig.getInstance().getList(this);
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
