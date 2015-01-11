package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;

public class TFM_Player
{
    private final UUID uuid;
    private String firstJoinName;
    private String lastJoinName;
    private long firstJoinUnix;
    private long lastJoinUnix;
    private final List<String> ips;

    protected TFM_Player(UUID uuid, ConfigurationSection section)
    {
        this(uuid);

        this.firstJoinName = section.getString("firstjoinname");
        this.lastJoinName = section.getString("lastjoinname");

        this.firstJoinUnix = section.getLong("firstjoinunix");
        this.lastJoinUnix = section.getLong("lastjoinunix");

        this.ips.addAll(section.getStringList("ips"));
    }

    protected TFM_Player(UUID uuid, String firstJoinName, String lastJoinName, long firstJoinUnix, long lastJoinUnix, List<String> ips)
    {
        this(uuid);

        this.firstJoinName = firstJoinName;
        this.lastJoinName = lastJoinName;

        this.firstJoinUnix = firstJoinUnix;
        this.lastJoinUnix = lastJoinUnix;

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
        return firstJoinUnix;
    }

    public void setFirstLoginUnix(long firstJoinUnix)
    {
        this.firstJoinUnix = firstJoinUnix;
    }

    public long getLastLoginUnix()
    {
        return lastJoinUnix;
    }

    public void setLastLoginUnix(long lastJoinUnix)
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

    public final boolean isComplete()
    {
        return firstJoinName != null
                && lastJoinName != null
                && firstJoinUnix != 0
                && lastJoinUnix != 0
                && !ips.isEmpty();
    }

    public void save()
    {
        TFM_PlayerList.save(this);
    }
}
