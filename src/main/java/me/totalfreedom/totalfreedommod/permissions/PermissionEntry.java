package me.totalfreedom.totalfreedommod.permissions;

import java.util.List;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;


public enum PermissionEntry
{
    REMOVE("remove"),
    OPERATORS("operators"),
    MASTER_BUILDERS("master_builders"),
    TRIAL_MODS("trial_mods"),
    MODS("mods"),
    ADMINS("admins");


    private final String configName;

    PermissionEntry(String configName)
    {
        this.configName = configName;
    }

    public String getConfigName()
    {
        return configName;
    }

    public List<?> getList()
    {
        return getConfig().getList(this);
    }

    @SuppressWarnings("unchecked")
    public List<String> getEntry()
    {
        return (List<String>)getList();
    }

    private PermissionConfig getConfig()
    {
        return TotalFreedomMod.plugin().permissions;
    }
}
