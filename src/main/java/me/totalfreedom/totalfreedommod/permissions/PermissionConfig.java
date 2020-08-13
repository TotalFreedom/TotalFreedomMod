package me.totalfreedom.totalfreedommod.permissions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.List;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class PermissionConfig extends FreedomService
{
    public static final String PERMISSIONS_FILENAME = "permissions.yml";
    //
    private final EnumMap<PermissionEntry, Object> entries;
    private final PermissionDefaults defaults;
    public YamlConfiguration configuration;

    public PermissionConfig(TotalFreedomMod plugin)
    {
        entries = new EnumMap<>(PermissionEntry.class);

        PermissionDefaults tempDefaults = null;
        try
        {
            try
            {
                try (InputStream defaultConfig = getDefaultConfig())
                {
                    tempDefaults = new PermissionDefaults(defaultConfig);
                    for (PermissionEntry entry : PermissionEntry.values())
                    {
                        entries.put(entry, tempDefaults.get(entry.getConfigName()));
                    }
                }
            }
            catch (IOException ex)
            {
                FLog.severe(ex);
            }

            copyDefaultConfig(getConfigFile());

            load();
        }
        catch (Exception ex)
        {
            FLog.severe(ex);
        }

        defaults = tempDefaults;
    }

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public void load()
    {
        try
        {
            YamlConfiguration config = new YamlConfiguration();

            config.load(getConfigFile());

            configuration = config;

            for (PermissionEntry entry : PermissionEntry.values())
            {
                String path = entry.getConfigName();
                if (config.contains(path))
                {
                    Object value = config.get(path);
                    if (value != null)
                    {
                        entries.put(entry, value);
                    }
                }
                else
                {
                    FLog.warning("Missing permission entry " + entry.getConfigName() + ". Using default value.");
                }
            }
        }
        catch (IOException | InvalidConfigurationException ex)
        {
            FLog.severe(ex);
        }
    }

    private File getConfigFile()
    {
        return new File(plugin.getDataFolder(), PERMISSIONS_FILENAME);
    }

    public List getList(PermissionEntry entry)
    {
        try
        {
            return get(entry, List.class);
        }
        catch (IllegalArgumentException ex)
        {
            FLog.severe(ex);
        }
        return null;
    }

    public <T> T get(PermissionEntry entry, Class<T> type) throws IllegalArgumentException
    {
        Object value = entries.get(entry);
        try
        {
            return type.cast(value);
        }
        catch (ClassCastException ex)
        {
            throw new IllegalArgumentException(entry.name() + " is not of type " + type.getSimpleName());
        }
    }

    public <T> void set(PermissionEntry entry, T value)
    {
        entries.put(entry, value);
    }

    private void copyDefaultConfig(File targetFile)
    {
        if (targetFile.exists())
        {
            return;
        }

        FLog.info("Installing default permission file template: " + targetFile.getPath());

        try
        {
            try (InputStream defaultConfig = getDefaultConfig())
            {
                FileUtils.copyInputStreamToFile(defaultConfig, targetFile);
            }
        }
        catch (IOException ex)
        {
            FLog.severe(ex);
        }
    }

    private InputStream getDefaultConfig()
    {
        return plugin.getResource(PERMISSIONS_FILENAME);
    }

    public static class PermissionDefaults
    {
        private YamlConfiguration defaults = null;

        private PermissionDefaults(InputStream defaultConfig)
        {
            try
            {
                defaults = new YamlConfiguration();
                final InputStreamReader isr = new InputStreamReader(defaultConfig);
                defaults.load(isr);
                isr.close();
            }
            catch (IOException | InvalidConfigurationException ex)
            {
                FLog.severe(ex);
            }
        }

        public Object get(String path)
        {
            return defaults.get(path);
        }
    }
}
