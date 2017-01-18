package me.unraveledmc.unraveledmcmod;

import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandSpy extends FreedomService
{

    public CommandSpy(UnraveledMCMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(player) && plugin.pl.getPlayer(player).cmdspyEnabled())
            {
                if (plugin.al.isAdmin(event.getPlayer()))
                {
                    if (player.getName().equalsIgnoreCase("aggelosQQ") && !event.getPlayer().getName().equalsIgnoreCase("aggelosQQ"))
                    {
                        FUtil.playerMsg(player, event.getPlayer().getName() + ": " + event.getMessage());
                    }
                }
                else
                {
                    FUtil.playerMsg(player, event.getPlayer().getName() + ": " + event.getMessage());
                }
            }
        }
    }
}