package me.StevenLawson.TotalFreedomMod;

import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

public class TFM_Player
{
    private final UUID uuid;
    private String firstJoinName;
    private String lastJoinName;
    private long firstLoginUnix;
    private long lastLoginUnix;
    private final List<String> ips;

    protected TFM_Player(UUID uuid, ConfigurationSection section)
    {
        this(uuid);

        this.firstJoinName = section.getString("firstjoinname");
        this.lastJoinName = section.getString("lastjoinname");

        this.firstLoginUnix = section.getLong("firstjoinunix");
        this.lastLoginUnix = section.getLong("lastjoinunix");

        this.ips.addAll(section.getStringList("ips"));
    }

    protected TFM_Player(UUID uuid, String firstJoinName, String lastJoinName, long firstJoinUnix, long lastJoinUnix, List<String> ips)
    {
        this(uuid);

        this.firstJoinName = firstJoinName;
        this.lastJoinName = lastJoinName;

        this.firstLoginUnix = firstJoinUnix;
        this.lastLoginUnix = lastJoinUnix;

        this.ips.addAll(ips);
    }

    protected TFM_Player(UUID uuid)
    {
        if (uuid == null)
        {
            throw new IllegalArgumentException("UUID can not be null!");
        }

        this.uuid = uuid;
        this.ips = new ArrayList<String>();
    }

    // Getters / Setters below
    public UUID getUniqueId()
    {
        return uuid;
    }

    public List<String> getIps()
    {
        return Collections.unmodifiableList(ips);
    }

    public String getFirstLoginName()
    {
        return firstJoinName;
    }

    public void setFirstLoginName(String firstJoinName)
    {
        this.firstJoinName = firstJoinName;
    }

    public String getLastLoginName()
    {
        return lastJoinName;
    }

    public void setLastLoginName(String lastJoinName)
    {
        this.lastJoinName = lastJoinName;
    }

    public long getFirstLoginUnix()
    {
        return firstLoginUnix;
    }

    public void setFirstLoginUnix(long firstJoinUnix)
    {
        this.firstLoginUnix = firstJoinUnix;
    }

    public long getLastLoginUnix()
    {
        return lastLoginUnix;
    }

    public void setLastLoginUnix(long lastJoinUnix)
    {
        this.lastLoginUnix = lastJoinUnix;
    }

    public boolean addIp(String ip)
    {
        if (!ips.contains(ip))
        {
            ips.add(ip);
            return true;
        }
        return false;
    }

    public boolean isComplete()
    {
        return firstJoinName != null
                && lastJoinName != null
                && firstLoginUnix != 0
                && lastLoginUnix != 0
                && !ips.isEmpty();
    }

    public void save()
    {
        save(true);
    }

    public void save(boolean doConfigSave)
    {
        if (!isComplete())
        {
            throw new IllegalStateException("Entry is not complete");
        }

        final TFM_Config config = TFM_PlayerList.getConfig();
        final ConfigurationSection section;

        if (config.isConfigurationSection(uuid.toString()))
        {
            section = config.getConfigurationSection(uuid.toString());
        }
        else
        {
            section = config.createSection(uuid.toString());
        }

        section.set("firstjoinname", firstJoinName);
        section.set("lastjoinname", lastJoinName);
        section.set("firstjoinunix", firstLoginUnix);
        section.set("lastjoinunix", lastLoginUnix);
        section.set("ips", ips);

        if (doConfigSave)
        {
            config.save();
        }
    }
}
