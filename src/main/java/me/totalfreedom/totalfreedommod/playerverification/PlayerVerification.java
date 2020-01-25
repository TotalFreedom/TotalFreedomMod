package me.totalfreedom.totalfreedommod.playerverification;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.Map;
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

public class PlayerVerification extends FreedomService
{

    @Getter
    public final Map<String, VPlayer> dataMap = Maps.newHashMap(); // username, data
    @Getter
    private final File configFolder;

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

    @Override
    protected void onStop()
    {
        save();
    }

    public Boolean isPlayerImpostor(Player player)
    {
        VPlayer vPlayer = getVerificationPlayer(player);
        return !plugin.al.isAdmin(player)
                && (vPlayer.getEnabled())
                && !vPlayer.getIps().contains(Ips.getIp(player));
    }

    public void verifyPlayer(Player player, String backupCode)
    {
        if (!isPlayerImpostor(player))
        {
            return;
        }

        VPlayer vPlayer = getVerificationPlayer(player);
        if (backupCode != null)
        {
            vPlayer.removeBackupCode(backupCode);
        }
        vPlayer.addIp(Ips.getIp(player));
        dataMap.put(player.getName(), vPlayer);
        YamlConfig config = getConfig(vPlayer);
        vPlayer.saveTo(config);
        config.save();
    }

    public void saveVerificationData(VPlayer player)
    {
        YamlConfig config = getConfig(player);
        player.saveTo(config);
        config.save();
        dataMap.put(player.getName(), player);
    }

    public void removeEntry(String name)
    {
        name = name.toLowerCase(); // Configuration files are saved in lowercase
        if (getVerificationPlayer(name) != null)
        {
            getConfigFile(name).delete();
            dataMap.remove(name);
        }
    }

    public void save()
    {
        for (VPlayer vPlayer : dataMap.values())
        {
            YamlConfig config = getConfig(vPlayer);
            vPlayer.saveTo(config);
            config.save();
        }
    }

    // May not return null
    public VPlayer getVerificationPlayer(Player player)
    {
        // Check for existing data
        VPlayer vPlayer = dataMap.get(player.getName());
        if (vPlayer != null)
        {
            return vPlayer;
        }

        // Load data
        vPlayer = getVerificationPlayer(player.getName());

        // Create new data if nonexistent
        if (vPlayer == null)
        {
            FLog.info("Creating new player verification entry for " + player.getName());

            // Create new player
            vPlayer = new VPlayer(player);
            vPlayer.addIp(Ips.getIp(player));

            // Store player
            dataMap.put(player.getName(), vPlayer);

            // Save player
            YamlConfig config = getConfig(vPlayer);
            vPlayer.saveTo(config);
            config.save();
        }

        return vPlayer;
    }

    // May return null
    public VPlayer getVerificationPlayer(String username)
    {
        username = username.toLowerCase();

        final File configFile = getConfigFile(username);
        if (!configFile.exists())
        {
            return null;
        }

        final VPlayer vPlayer = new VPlayer(username);
        vPlayer.loadFrom(getConfig(vPlayer));

        if (!vPlayer.isValid())
        {
            FLog.warning("Could not load player verification entry for " + username + ". Entry is not valid!");
            configFile.delete();
            return null;
        }

        // Only store data in map if the player is online
        for (Player players : Bukkit.getOnlinePlayers())
        {
            if (players.getName().equals(username))
            {
                dataMap.put(username, vPlayer);
                return vPlayer;
            }
        }

        return vPlayer;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        dataMap.remove(event.getPlayer().getName());
    }

    protected File getConfigFile(String name)
    {
        return new File(getConfigFolder(), name + ".yml");
    }

    protected YamlConfig getConfig(VPlayer player)
    {
        final YamlConfig config = new YamlConfig(plugin, getConfigFile(player.getName().toLowerCase()), false);
        config.load();

        // Convert discordEnabled to enabled, and remove forumEnabled.
        if (config.get("discordEnabled") != null)
        {
            config.set("enabled", config.getBoolean("discordEnabled"));
            config.set("discordEnabled", null);
            config.set("forumEnabled", null);
            try
            {
                config.save(getConfigFile(player.getName().toLowerCase()));
            }
            catch (IOException e)
            {
                FLog.warning("Failed to convert player verification entry for " + player.getName());
            }
        }

        return config;
    }
}
