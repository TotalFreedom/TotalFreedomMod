package me.totalfreedom.totalfreedommod.banning;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.config.IConfig;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.configuration.ConfigurationSection;

public class IndefiniteBan implements IConfig
{

    @Getter
    @Setter
    private String username = null;
    @Getter
    @Setter
    private UUID uuid = null;
    @Getter
    private final List<String> ips = Lists.newArrayList();
    @Getter
    @Setter
    private String reason = null;

    public IndefiniteBan()
    {
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        this.username = cs.getName();
        try
        {
            String strUUID = cs.getString("uuid", null);
            if (strUUID != null)
            {
                UUID uuid = UUID.fromString(strUUID);
                this.uuid = uuid;
            }
        }
        catch (IllegalArgumentException e)
        {
            FLog.warning("Failed to load indefinite banned UUID for " + this.username + ". Make sure the UUID is in the correct format with dashes.");
        }
        this.ips.clear();
        this.ips.addAll(cs.getStringList("ips"));
        this.reason = cs.getString("reason", null);
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
    }

    @Override
    public boolean isValid()
    {
        return username != null;
    }
}
