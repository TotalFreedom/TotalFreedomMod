package me.StevenLawson.TotalFreedomMod;

import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

public class TFM_PlayerEntry
{
    private final UUID uuid;
    private String firstJoinName;
    private String lastJoinName;
    private long firstJoinUnix;
    private long lastJoinUnix;
    private final List<String> ips;

    protected TFM_PlayerEntry(UUID uuid, ConfigurationSection section)
    {
        this(uuid);

        this.firstJoinName = section.getString("firstjoinname");
        this.lastJoinName = section.getString("lastjoinname");

        this.firstJoinUnix = section.getLong("firstjoinunix");
        this.lastJoinUnix = section.getLong("lastjoinunix");

        this.ips.addAll(section.getStringList("ips"));
    }

    protected TFM_PlayerEntry(UUID uuid)
    {
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

    public String getFirstJoinName()
    {
        return firstJoinName;
    }

    public void setFirstJoinName(String firstJoinName)
    {
        this.firstJoinName = firstJoinName;
    }

    public String getLastJoinName()
    {
        return lastJoinName;
    }

    public void setLastJoinName(String lastJoinName)
    {
        this.lastJoinName = lastJoinName;
    }

    public long getFirstJoinUnix()
    {
        return firstJoinUnix;
    }

    public void setFirstJoinUnix(long firstJoinUnix)
    {
        this.firstJoinUnix = firstJoinUnix;
    }

    public long getLastJoinUnix()
    {
        return lastJoinUnix;
    }

    public void setLastJoinUnix(long lastJoinUnix)
    {
        this.lastJoinUnix = lastJoinUnix;
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
                && firstJoinUnix != 0
                && lastJoinUnix != 0
                && !ips.isEmpty();
    }

    public void save()
    {
        if (!isComplete())
        {
            throw new IllegalStateException("Entry is not complete");
        }

        final TFM_Config config = TFM_PlayerList.getInstance().getConfig();
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
        section.set("firstjoinunix", firstJoinUnix);
        section.set("lastjoinunix", lastJoinUnix);
        section.set("ips", ips);

        config.save();
    }
}
