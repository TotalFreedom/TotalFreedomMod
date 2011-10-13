package me.StevenLawson.TotalFreedomMod;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TFM_Heartbeat implements Runnable
{
    private TotalFreedomMod plugin;

    TFM_Heartbeat(TotalFreedomMod instance)
    {
        this.plugin = instance;
    }

    @Override
    public void run()
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            TFM_UserInfo playerdata = plugin.userinfo.get(p);
            if (playerdata != null)
            {
                playerdata.resetMsgCount();
                playerdata.resetBlockDestroyCount();
            }
        }

        if (plugin.autoEntityWipe)
        {
            TFM_Util.wipeDropEntities(plugin);
        }

        if (plugin.disableNight)
        {
            for (World world : Bukkit.getWorlds())
            {
                if (world.getTime() > 12000L)
                {
                    TFM_Util.setWorldTime(world, 1000L);
                }
            }
        }
    }
}
