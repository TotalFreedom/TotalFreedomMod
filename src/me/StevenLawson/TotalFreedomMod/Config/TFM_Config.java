package me.StevenLawson.TotalFreedomMod.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Represents a definable YAML configuration.
 *
 * @see YamlConfiguration
 */
public class TFM_Config extends YamlConfiguration // BukkitLib @ https://github.com/Pravian/BukkitLib
{
    private final Plugin plugin;
    private final File configFile;
    private final boolean copyDefaults;

    /**
     * Creates a new YamlConfig instance.
     *
     * <p>Example:
     * <pre>
     * YamlConfig config = new YamlConfig(this, "config.yml", true);
     * config.load();
     * </pre></p>
     *
     * @param plugin The plugin to which the config belongs.
     * @param fileName The filename of the config file.
     * @param copyDefaults If the defaults should be copied and/loaded from a config in the plugin jar-file.
     */
    public TFM_Config(Plugin plugin, String fileName, boolean copyDefaults)
    {
        this(plugin, TFM_Util.getPluginFile(plugin, fileName), copyDefaults);
    }

    /**
     * Creates a new YamlConfig instance.
     *
     * <p>Example:
     * <pre>
     * YamlConfig config = new YamlConfig(this, new File(plugin.getDataFolder() + "/players", "Prozza.yml"), false);
     * config.load();
     * </pre></p>
     *
     * @param plugin The plugin to which the config belongs.
     * @param file The file of the config file.
     * @param copyDefaults If the defaults should be copied and/loaded from a config in the plugin jar-file.
     */
    public TFM_Config(Plugin plugin, File file, boolean copyDefaults)
    {
        this.plugin = plugin;
        this.configFile = file;
        this.copyDefaults = copyDefaults;
    }

    /**
     * Validates if the configuration exists.
     *
     * @return True if the configuration exists.
     */
    public boolean exists()
    {
        return configFile.exists();
    }

    /**
     * Saves the configuration to the predefined file.
     *
     * @see #YamlConfig(Plugin, String, boolean)
     */
    public void save()
    {
        try
        {
            super.save(configFile);
        }
        catch (Exception ex)
        {
            plugin.getLogger().severe("Could not save configuration file: " + configFile.getName());
            plugin.getLogger().severe(ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * Loads the configuration from the predefined file.
     *
     * <p>Optionally, if loadDefaults has been set to true, the file will be copied over from the default inside the jar-file of the owning plugin.</p>
     *
     * @see #YamlConfig(Plugin, String, boolean)
     */
    public void load()
    {
        try
        {
            if (copyDefaults)
            {
                if (!configFile.exists())
                {
                    configFile.getParentFile().mkdirs();
                    try
                    {
                        TFM_Util.copy(plugin.getResource(configFile.getName()), configFile);
                    }
                    catch (IOException ex)
                    {
                        plugin.getLogger().severe("Could not write default configuration file: " + configFile.getName());
                        plugin.getLogger().severe(ExceptionUtils.getStackTrace(ex));
                    }
                    plugin.getLogger().info("Installed default configuration " + configFile.getName());
                }

                super.addDefaults(getDefaultConfig());
            }

            if (configFile.exists())
            {
                super.load(configFile);
            }
        }
        catch (Exception ex)
        {
            plugin.getLogger().severe("Could not load configuration file: " + configFile.getName());
            plugin.getLogger().severe(ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * Returns the raw YamlConfiguration this config is based on.
     *
     * @return The YamlConfiguration.
     * @see YamlConfiguration
     */
    public YamlConfiguration getConfig()
    {
        return this;
    }

    /**
     * Returns the default configuration as been stored in the jar-file of the owning plugin.
     * @return The default configuration.
     */
    public YamlConfiguration getDefaultConfig()
    {
        final YamlConfiguration DEFAULT_CONFIG = new YamlConfiguration();
        try
        {
            final InputStreamReader isr = new InputStreamReader(plugin.getResource(configFile.getName()));
            DEFAULT_CONFIG.load(isr);
            isr.close();
        }
        catch (IOException ex)
        {
            plugin.getLogger().severe("Could not load default configuration: " + configFile.getName());
            plugin.getLogger().severe(ExceptionUtils.getStackTrace(ex));
            return null;
        }
        catch (InvalidConfigurationException ex)
        {
            plugin.getLogger().severe("Could not load default configuration: " + configFile.getName());
            plugin.getLogger().severe(ExceptionUtils.getStackTrace(ex));
            return null;
        }
        return DEFAULT_CONFIG;
    }
}
