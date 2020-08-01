package me.totalfreedom.totalfreedommod.bridge;

import me.totalfreedom.tfguilds.Common;
import me.totalfreedom.tfguilds.TFGuilds;
import me.totalfreedom.totalfreedommod.FreedomService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TFGuildsBridge extends FreedomService
{

    public boolean enabled = false;

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public boolean isTFGuildsEnabled()
    {
        if (enabled)
        {
            return true;
        }

        try
        {
            final Plugin tfGuilds = server.getPluginManager().getPlugin("TFGuilds");
            if (tfGuilds != null && tfGuilds.isEnabled())
            {
                if (tfGuilds instanceof TFGuilds)
                {
                    enabled = true;
                    return true;
                }
            }
        }
        catch (NoClassDefFoundError ex)
        {
            return false;
        }

        return false;
    }

    public boolean inGuildChat(Player player)
    {
        if (!isTFGuildsEnabled())
        {
            return false;
        }
        return Common.IN_GUILD_CHAT.contains(player);
    }
}
