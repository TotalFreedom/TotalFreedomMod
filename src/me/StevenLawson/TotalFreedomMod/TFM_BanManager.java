package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;

public class TFM_BanManager
{
    private static final TFM_BanManager INSTANCE;
    private final List<TFM_Ban> ipBans;
    private final List<TFM_Ban> uuidBans;

    static
    {
        INSTANCE = new TFM_BanManager();
    }

    private TFM_BanManager()
    {
        ipBans = new ArrayList<TFM_Ban>();
        uuidBans = new ArrayList<TFM_Ban>();
    }

    public void load()
    {
        ipBans.clear();
        uuidBans.clear();

        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, "bans.yml", true);
        config.load();

        for (String banString : config.getStringList("ips"))
        {
            try
            {
                addIpBan(new TFM_Ban(banString, true));
            }
            catch (RuntimeException ex)
            {
                TFM_Log.warning("Could not load IP ban: " + banString);
            }
        }

        for (String banString : config.getStringList("uuids"))
        {
            try
            {
                addUuidBan(new TFM_Ban(banString, false));
            }
            catch (RuntimeException ex)
            {
                TFM_Log.warning("Could not load UUID ban: " + banString);
            }
        }

        // Save the config
        save();
    }

    public void save()
    {
        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, "bans.yml", true);
        config.load();

        final List<String> newIpBans = new ArrayList<String>();
        final List<String> newUuidBans = new ArrayList<String>();

        for (TFM_Ban savedBan : ipBans)
        {
            newIpBans.add(savedBan.toString());
        }

        for (TFM_Ban savedBan : uuidBans)
        {
            newUuidBans.add(savedBan.toString());
        }

        config.set("ips", newIpBans);
        config.set("uuids", newUuidBans);

        // Save config
        config.save();
    }

    public List<TFM_Ban> getIpBanList()
    {
        return Collections.unmodifiableList(uuidBans);
    }

    public List<TFM_Ban> getUuidBanList()
    {
        return Collections.unmodifiableList(uuidBans);
    }

    public TFM_Ban getByIp(String ip)
    {
        for (TFM_Ban ban : ipBans)
        {
            if (ban.getSubject().equals(ip))
            {
                return ban;
            }
        }
        return null;
    }

    public TFM_Ban getByUuid(UUID uuid)
    {
        for (TFM_Ban ban : uuidBans)
        {
            if (ban.getSubject().equalsIgnoreCase(uuid.toString()))
            {
                return ban;
            }
        }
        return null;
    }

    public void unbanIp(String ip)
    {
        final TFM_Ban ban = getByIp(ip);

        if (ban == null)
        {
            TFM_Log.warning("Could not unban IP: " + ip + ", IP is not banned!");
            return;
        }

        removeBan(ban);
    }

    public void unbanUuid(UUID uuid)
    {
        final TFM_Ban ban = getByUuid(uuid);

        if (ban == null)
        {
            TFM_Log.warning("Could not unban UUID: " + uuid + ", UUID is not banned!");
            return;
        }

        removeBan(ban);
    }

    public boolean isIpBanned(String ip)
    {
        return getByIp(ip) != null;
    }

    public boolean isUuidBanned(UUID uuid)
    {
        return getByUuid(uuid) != null;
    }

    public void addUuidBan(TFM_Ban ban)
    {
        if (!ban.isComplete())
        {
            throw new RuntimeException("Could not load IP ban, Invalid format!");
        }

        if (ban.isExpired())
        {
            return;
        }

        uuidBans.add(ban);
    }

    public void addIpBan(TFM_Ban ban)
    {
        if (!ban.isComplete())
        {
            throw new RuntimeException("Could not load UUID ban, Invalid format!");
        }

        if (ban.isExpired())
        {
            return;
        }

        ipBans.add(ban);
    }

    public void removeBan(TFM_Ban ban)
    {
        final Iterator<TFM_Ban> ips = ipBans.iterator();
        while (ips.hasNext())
        {
            if (ips.next().getSubject().equalsIgnoreCase(ban.getSubject()))
            {
                ips.remove();
            }
        }

        final Iterator<TFM_Ban> uuids = uuidBans.iterator();
        while (uuids.hasNext())
        {
            if (uuids.next().getSubject().equalsIgnoreCase(ban.getSubject()))
            {
                uuids.remove();
            }
        }
    }

    public void purgeIpBans()
    {
        ipBans.clear();
    }

    public void purgeUuidBans()
    {
        uuidBans.clear();
    }

    public static TFM_BanManager getInstance()
    {
        return INSTANCE;
    }
}
