package me.totalfreedom.totalfreedommod.bridge;

import me.totalfreedom.tfguilds.Common;
import me.totalfreedom.tfguilds.TFGuilds;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TFGuildsBridge extends FreedomService
{

    private TFGuilds tfGuildsPlugin = null;

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public TFGuilds getTfGuildsPlugin()
    {
        if (tfGuildsPlugin == null)
        {
            try
            {
                final Plugin tfGuilds = server.getPluginManager().getPlugin("TFGuilds");
                if (tfGuilds != null)
                {
                    if (tfGuilds instanceof TFGuilds)
                    {
                        tfGuildsPlugin = (TFGuilds)tfGuilds;
                    }
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }
        return tfGuildsPlugin;
    }

    public boolean inGuildChat(Player player)
    {
        return Common.IN_GUILD_CHAT.contains(player);
    }
}
