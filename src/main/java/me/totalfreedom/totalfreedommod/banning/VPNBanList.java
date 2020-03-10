package me.totalfreedom.totalfreedommod.banning;

import com.google.common.collect.Sets;
import java.util.Set;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.util.Ips;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

public class VPNBanList extends FreedomService
{

    public static final String CONFIG_FILENAME = "vpnbans.yml";

    @Getter
    private final Set<String> vpnIps = Sets.newHashSet();

    public VPNBanList(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        vpnIps.clear();

        final YamlConfig config = new YamlConfig(plugin, CONFIG_FILENAME, true);
        config.load();

        for (String name : config.getKeys(false))
        {
            vpnIps.addAll(config.getStringList(name));
        }

        FLog.info("Loaded " + vpnIps.size() + " VPN ips.");
    }

    @Override
    protected void onStop()
    {
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        final String ip = Ips.getIp(event);

        // Permbanned IPs
        for (String testIp : getVPNIps())
        {
            if (FUtil.fuzzyIpMatch(testIp, ip, 4))
            {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ChatColor.RED + "Your IP address is detected as a VPN\n"
                                + "If you believe this is an error, release procedures are available at\n"
                                + ChatColor.GOLD + ConfigEntry.SERVER_PERMBAN_URL.getString());
                return;
            }
        }

    }

    public Set<String> getVPNIps()
    {
        return this.vpnIps;
    }
}
