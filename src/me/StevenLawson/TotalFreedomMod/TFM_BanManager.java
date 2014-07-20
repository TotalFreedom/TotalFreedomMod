package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import me.StevenLawson.TotalFreedomMod.Config.TFM_Config;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TFM_BanManager
{
    private static final List<TFM_Ban> ipBans;
    private static final List<TFM_Ban> uuidBans;
    private static final List<UUID> unbannableUUIDs;

    static
    {
        ipBans = new ArrayList<TFM_Ban>();
        uuidBans = new ArrayList<TFM_Ban>();
        unbannableUUIDs = new ArrayList<UUID>();
    }

    private TFM_BanManager()
    {
        throw new AssertionError();
    }

    public static void load()
    {
        ipBans.clear();
        uuidBans.clear();
        unbannableUUIDs.clear();

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
        TFM_Log.info("Loaded " + ipBans.size() + " IP bans and " + uuidBans.size() + " UUID bans");

        @SuppressWarnings("unchecked")
        final TFM_UuidResolver resolver = new TFM_UuidResolver((List<String>) TFM_ConfigEntry.UNBANNABLE_USERNAMES.getList());

        for (UUID uuid : resolver.call().values())
        {
            unbannableUUIDs.add(uuid);
        }

        TFM_Log.info("Loaded " + unbannableUUIDs.size() + " unbannable UUIDs");
    }

    public static void save()
    {
        final TFM_Config config = new TFM_Config(TotalFreedomMod.plugin, "bans.yml", true);
        config.load();

        final List<String> newIpBans = new ArrayList<String>();
        final List<String> newUuidBans = new ArrayList<String>();

        for (TFM_Ban savedBan : ipBans)
        {
            if (!savedBan.isExpired())
            {
                newIpBans.add(savedBan.toString());
            }
        }

        for (TFM_Ban savedBan : uuidBans)
        {
            if (!savedBan.isExpired() && !unbannableUUIDs.contains(UUID.fromString(savedBan.getSubject())))
            {
                newUuidBans.add(savedBan.toString());
            }
        }

        config.set("ips", newIpBans);
        config.set("uuids", newUuidBans);

        // Save config
        config.save();
    }

    public static List<TFM_Ban> getIpBanList()
    {
        return Collections.unmodifiableList(uuidBans);
    }

    public static List<TFM_Ban> getUuidBanList()
    {
        return Collections.unmodifiableList(uuidBans);
    }

    public static TFM_Ban getByIp(String ip)
    {
        for (TFM_Ban ban : ipBans)
        {
            if (ban.isExpired())
            {
                continue;
            }

            wildcardCheck:
            if (ban.getSubject().contains("*"))
            {
                final String[] subjectParts = ban.getSubject().split("\\.");
                final String[] ipParts = ip.split("\\.");

                for (int i = 0; i < 4; i++)
                {
                    if (!(subjectParts[i].equals("*") || subjectParts[i].equals(ipParts[i])))
                    {
                        break wildcardCheck;
                    }
                }

                return ban;
            }

            if (ban.getSubject().equals(ip))
            {
                return ban;
            }
        }
        return null;
    }

    public static TFM_Ban getByUuid(UUID uuid)
    {
        for (TFM_Ban ban : uuidBans)
        {
            if (ban.getSubject().equalsIgnoreCase(uuid.toString()))
            {
                if (ban.isExpired())
                {
                    continue;
                }

                return ban;
            }
        }
        return null;
    }

    public static void unbanIp(String ip)
    {
        final TFM_Ban ban = getByIp(ip);

        if (ban == null)
        {
            return;
        }

        removeBan(ban);
        save();
    }

    public static void unbanUuid(UUID uuid)
    {
        final TFM_Ban ban = getByUuid(uuid);

        if (ban == null)
        {
            return;
        }

        removeBan(ban);
    }

    public static boolean isIpBanned(String ip)
    {
        return getByIp(ip) != null;
    }

    public static boolean isUuidBanned(UUID uuid)
    {
        return getByUuid(uuid) != null;
    }

    public static void addUuidBan(Player player)
    {
        addUuidBan(new TFM_Ban(TFM_Util.getUuid(player), player.getName()));
    }

    public static void addUuidBan(TFM_Ban ban)
    {
        if (!ban.isComplete())
        {
            throw new RuntimeException("Could not add UUID ban, Invalid format!");
        }

        if (ban.isExpired())
        {
            return;
        }

        if (unbannableUUIDs.contains(UUID.fromString(ban.getSubject())))
        {
            return;
        }

        uuidBans.add(ban);
        save();
    }

    public static void addIpBan(Player player)
    {
        addIpBan(new TFM_Ban(TFM_Util.getIp(player), player.getName()));
    }

    public static void addIpBan(TFM_Ban ban)
    {
        if (!ban.isComplete())
        {
            throw new RuntimeException("Could not add IP ban, Invalid format!");
        }

        if (ban.isExpired())
        {
            return;
        }

        ipBans.add(ban);
        save();
    }

    public static void removeBan(TFM_Ban ban)
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

        save();
    }

    public static void purgeIpBans()
    {
        ipBans.clear();
        save();
    }

    public static void purgeUuidBans()
    {
        uuidBans.clear();
        save();
    }
}
