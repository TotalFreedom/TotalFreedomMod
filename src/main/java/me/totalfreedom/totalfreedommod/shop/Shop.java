package me.totalfreedom.totalfreedommod.shop;

import com.google.common.collect.Maps;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.util.Ips;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public class Shop extends FreedomService
{
    @Getter
    public final Map<String, ShopData> dataMap = Maps.newHashMap(); // ip,dataMap
    @Getter
    private final File configFolder;

    public Shop(TotalFreedomMod plugin)
    {
        super(plugin);

        this.configFolder = new File(plugin.getDataFolder(), "shopdata");
    }

    @Override
    protected void onStart()
    {
        dataMap.clear();
    }

    @Override
    protected void onStop()
    {
        for (ShopData sd : dataMap.values())
        {
            save(sd);
        }
    }
    
    public void save(ShopData data)
    {
        YamlConfig config = getConfig(data);
        data.saveTo(config);
        config.save();
    }

    public String getIp(OfflinePlayer player)
    {
        if (player.isOnline())
        {
            return Ips.getIp(player.getPlayer());
        }

        final ShopData entry = getData(player.getName());

        return (entry == null ? null : entry.getIps().iterator().next());
    }
    
    public String getShopPrefix()
    {
        return FUtil.colorize(ConfigEntry.SHOP_PREFIX.getString() + " ");
    }

    // May not return null
    public ShopData getData(Player player)
    {
        // Check already loaded
        ShopData data = dataMap.get(Ips.getIp(player));
        if (data != null)
        {
            return data;
        }

        // Load data
        data = getData(player.getName());

        // Create data if nonexistent
        if (data == null)
        {
            FLog.info("Creating new shop data entry for " + player.getName());

            // Create new player
            final long unix = FUtil.getUnixTime();
            data = new ShopData(player);
            data.addIp(Ips.getIp(player));
            
            // Set defaults
            data.setCoins(0);

            // Store player
            dataMap.put(player.getName().toLowerCase(), data);

            // Save player
            YamlConfig config = getConfig(data);
            data.saveTo(config);
            config.save();
        }

        return data;
    }

    // May return null
    public ShopData getData(String username)
    {
        username = username.toLowerCase();

        // Check if the player is a known player
        final File configFile = getConfigFile(username);
        if (!configFile.exists())
        {
            return null;
        }

        // Create and load entry
        final ShopData data = new ShopData(username);
        data.loadFrom(getConfig(data));

        if (!data.isValid())
        {
            FLog.warning("Could not load shop data entry: " + username + ". Entry is not valid!");
            configFile.delete();
            return null;
        }

        // Only store data if the player is online
        for (String ip : data.getIps())
        {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            {
                if (Ips.getIp(onlinePlayer).equals(ip))
                {
                    dataMap.put(ip, data);
                    return data;
                }
            }
        }

        return data;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        final String ip = Ips.getIp(event.getPlayer());
        dataMap.remove(ip);
    }

    public Collection<ShopData> getLoadedData()
    {
        return dataMap.values();
    }

    protected File getConfigFile(String name)
    {
        return new File(getConfigFolder(), name + ".yml");
    }

    protected YamlConfig getConfig(ShopData data)
    {
        final YamlConfig config = new YamlConfig(plugin, getConfigFile(data.getUsername().toLowerCase()), false);
        config.load();
        return config;
    }
}