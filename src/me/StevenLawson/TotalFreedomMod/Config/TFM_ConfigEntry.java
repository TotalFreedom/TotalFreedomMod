package me.StevenLawson.TotalFreedomMod.Config;

import java.util.List;

public enum TFM_ConfigEntry
{
    FORCE_IP_ENABLED(Boolean.class, "forceip.enabled"),
    FORCE_IP_PORT(Integer.class, "forceip.port"),
    FORCE_IP_KICKMSG(String.class, "forceip.kickmsg"),
    //
    ALLOW_EXPLOSIONS(Boolean.class, "allow.explosions"),
    ALLOW_FIRE_PLACE(Boolean.class, "allow.fire_place"),
    ALLOW_FIRE_SPREAD(Boolean.class, "allow.fire_spread"),
    ALLOW_FLUID_SPREAD(Boolean.class, "allow.fluid_spread"),
    ALLOW_LAVA_DAMAGE(Boolean.class, "allow.lava_damage"),
    ALLOW_LAVA_PLACE(Boolean.class, "allow.lava_place"),
    ALLOW_TNT_MINECARTS(Boolean.class, "allow.tnt_minecarts"),
    ALLOW_WATER_PLACE(Boolean.class, "allow.water_place"),
    //
    MOB_LIMITER_ENABLED(Boolean.class, "moblimiter.enabled"),
    MOB_LIMITER_MAX(Integer.class, "moblimiter.max"),
    MOB_LIMITER_DISABLE_DRAGON(Boolean.class, "moblimiter.disable.dragon"),
    MOB_LIMITER_DISABLE_GHAST(Boolean.class, "moblimiter.disable.ghast"),
    MOB_LIMITER_DISABLE_GIANT(Boolean.class, "moblimiter.disable.giant"),
    MOB_LIMITER_DISABLE_SLIME(Boolean.class, "moblimiter.disable.slime"),
    //
    HTTPD_ENABLED(Boolean.class, "httpd.enabled"),
    HTTPD_PORT(Integer.class, "httpd.port"),
    HTTPD_PUBLIC_FOLDER(String.class, "httpd.public_folder"),
    //
    SERVER_COLORFUL_MOTD(Boolean.class, "server.colorful_motd"),
    SERVER_NAME(String.class, "server.name"),
    SERVER_ADDRESS(String.class, "server.address"),
    SERVER_MOTD(String.class, "server.motd"),
    SERVER_OWNERS(List.class, "server.owners"),
    SERVER_BAN_URL(String.class, "server.ban_url"),
    SERVER_PERMBAN_URL(String.class, "server.permban_url"),
    //
    TWITTERBOT_ENABLED(Boolean.class, "twitterbot.enabled"),
    TWITTERBOT_SECRET(String.class, "twitterbot.secret"),
    TWITTERBOT_URL(String.class, "twitterbot.url"),
    //
    DISABLE_NIGHT(Boolean.class, "disable.night"),
    DISABLE_WEATHER(Boolean.class, "disable.weather"),
    //
    ENABLE_PREPROCESS_LOG(Boolean.class, "preprocess_log"),
    ENABLE_PET_PROTECT(Boolean.class, "petprotect.enabled"),
    //
    LANDMINES_ENABLED(Boolean.class, "landmines_enabled"),
    TOSSMOB_ENABLED(Boolean.class, "tossmob_enabled"),
    AUTOKICK_ENABLED(Boolean.class, "autokick.enabled"),
    MP44_ENABLED(Boolean.class, "mp44_enabled"),
    //
    PROTECTAREA_ENABLED(Boolean.class, "protectarea.enabled"),
    PROTECTAREA_SPAWNPOINTS(Boolean.class, "protectarea.auto_protect_spawnpoints"),
    PROTECTAREA_RADIUS(Double.class, "protectarea.auto_protect_radius"),
    //
    NUKE_MONITOR_ENABLED(Boolean.class, "nukemonitor.enabled"),
    NUKE_MONITOR_COUNT_BREAK(Integer.class, "nukemonitor.count_break"),
    NUKE_MONITOR_COUNT_PLACE(Integer.class, "nukemonitor.count_place"),
    NUKE_MONITOR_RANGE(Double.class, "nukemonitor.range"),
    //
    AUTOKICK_THRESHOLD(Double.class, "autokick.threshold"),
    AUTOKICK_TIME(Integer.class, "autokick.time"),
    //
    LOGS_SECRET(String.class, "logs.secret"),
    LOGS_URL(String.class, "logs.url"),
    //
    FLATLANDS_GENERATE(Boolean.class, "flatlands.generate"),
    FLATLANDS_GENERATE_PARAMS(String.class, "flatlands.generate_params"),
    //
    ANNOUNCER_ENABLED(Boolean.class, "announcer.enabled"),
    ANNOUNCER_INTERVAL(Integer.class, "announcer.interval"),
    ANNOUNCER_PREFIX(String.class, "announcer.prefix"),
    ANNOUNCER_ANNOUNCEMENTS(List.class, "announcer.announcements"),
    //
    EXPLOSIVE_RADIUS(Double.class, "explosive_radius"),
    FREECAM_TRIGGER_COUNT(Integer.class, "freecam_trigger_count"),
    SERVICE_CHECKER_URL(String.class, "service_checker_url"),
    BLOCKED_COMMANDS(List.class, "blocked_commands"),
    HOST_SENDER_NAMES(List.class, "host_sender_names"),
    UNBANNABLE_USERNAMES(List.class, "unbannable_usernames"),
    OVERLORD_IPS(List.class, "overlord_ips"),
    NOADMIN_IPS(List.class, "noadmin_ips"),
    ADMIN_ONLY_MODE(Boolean.class, "admin_only_mode"),
    AUTO_ENTITY_WIPE(Boolean.class, "auto_wipe"),
    CONSOLE_IS_SENIOR(Boolean.class, "console_is_senior");
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
        return TFM_MainConfig.getString(this);
    }

    public String setString(String value)
    {
        TFM_MainConfig.setString(this, value);
        return value;
    }

    public Double getDouble()
    {
        return TFM_MainConfig.getDouble(this);
    }

    public Double setDouble(Double value)
    {
        TFM_MainConfig.setDouble(this, value);
        return value;
    }

    public Boolean getBoolean()
    {
        return TFM_MainConfig.getBoolean(this);
    }

    public Boolean setBoolean(Boolean value)
    {
        TFM_MainConfig.setBoolean(this, value);
        return value;
    }

    public Integer getInteger()
    {
        return TFM_MainConfig.getInteger(this);
    }

    public Integer setInteger(Integer value)
    {
        TFM_MainConfig.setInteger(this, value);
        return value;
    }

    public List<?> getList()
    {
        return TFM_MainConfig.getList(this);
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
