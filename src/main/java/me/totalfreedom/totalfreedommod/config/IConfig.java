package me.totalfreedom.totalfreedommod.config;

import org.bukkit.configuration.ConfigurationSection;

public interface IConfig
{
    void loadFrom(ConfigurationSection cs);
    void saveTo(ConfigurationSection cs);
    boolean isValid();
}
