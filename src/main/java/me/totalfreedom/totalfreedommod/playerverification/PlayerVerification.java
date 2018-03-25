package me.totalfreedom.totalfreedommod.playerverification;

import com.google.common.collect.Maps;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.util.Ips;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.Map;

public class PlayerVerification extends FreedomService
{

    @Getter
    public final Map<String, VPlayer> dataMap = Maps.newHashMap(); // username, data
    private File configFolder;

    public PlayerVerification(TotalFreedomMod plugin)
    {
        super(plugin);
        this.configFolder = new File(plugin.getDataFolder(), "playerverification");
    }

    @Override
    protected void onStart()
    {
        dataMap.clear();
    }

    public void save(VPlayer data)
    {
        YamlConfig config = getConfig(data);
        data.saveTo(config);
        config.save();
    }

    @Override
    protected void onStop()
    {
        //save all (should be saved in theory but to be safe)
        for (VPlayer player : dataMap.values())
        {
            save(player);
        }
    }

    public Boolean isPlayerImpostor(Player player)
    {
        VPlayer vplayer = getVerificationPlayer(player.getName());
        return !plugin.al.isAdmin(player) && vplayer != null && (vplayer.getForumVerificationEnabled() || vplayer.getDiscordVerificationEnabled()) && !vplayer.getIPs().contains(Ips.getIp(player));
    }

    public void verifyPlayer(Player player)
    {
        if (!isPlayerImpostor(player))
        {
            return;
        }
        VPlayer vplayer = getVerificationPlayer(player.getName());
        vplayer.addIp(Ips.getIp(player));
        saveVerificationData(vplayer);
    }

    public void saveVerificationData(VPlayer player)
    {
        if (dataMap.containsKey(player.getName()))
        {
            dataMap.remove(player.getName());
        }
        dataMap.put(player.getName(), player);
        save(player);
    }

    //may not return null
    public VPlayer getVerificationPlayer(Player player)
    {
        VPlayer data = getVerificationPlayer(player.getName());
        if (data != null)
        {
            return data;
        }
        // Create new entry.
        FLog.info("Creating new player verification entry for " + player.getName());

        // Create new player data
        VPlayer newEntry = new VPlayer(player.getName());
        newEntry.addIp(Ips.getIp(player));
        saveVerificationData(newEntry);
        return newEntry;
    }

    //may return null
    public VPlayer getVerificationPlayer(String username)
    {
        if (dataMap.containsKey(username))
        {
            return dataMap.get(username);
        }
        VPlayer player = loadData(username);
        if (player != null)
        {
            return player;
        }

        return null;
    }

    public VPlayer loadData(String username)
    {
        final File configFile = getConfigFile(username);
        if (!configFile.exists())
        {
            return null;
        }

        final VPlayer data = new VPlayer(username);
        data.loadFrom(getConfig(data));

        if (!data.isValid())
        {
            FLog.warning("Could not load player verification entry: " + username + ". Entry is not valid!");
            configFile.delete();
            return null;
        }


        // Only store data in map if the player is online
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
        {
            if (onlinePlayer.getName().equals(username))
            {
                dataMap.put(username, data);
                return data;
            }
        }
        return data;
    }

    public void removeEntry(String username)
    {
        if (getVerificationPlayer(username) != null)
        {
            getConfigFile(username).delete();
            if (dataMap.containsKey(username))
            {
                dataMap.remove(username);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        if (dataMap.containsKey(event.getPlayer().getName()))
        {
            saveVerificationData(dataMap.get(event.getPlayer().getName()));
            dataMap.remove(event.getPlayer().getName());
        }
    }

    protected File getConfigFile(String name)
    {
        return new File(configFolder, name + ".yml");
    }

    protected YamlConfig getConfig(VPlayer data)
    {
        final YamlConfig config = new YamlConfig(plugin, getConfigFile(data.getName().toLowerCase()), false);
        config.load();
        return config;
    }
}
