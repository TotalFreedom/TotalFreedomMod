package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.command.FreedomCommand;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class CommandSpy extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        for (Player player : server.getOnlinePlayers())
        {
            if (plugin.sl.isStaff(player) && plugin.sl.getAdmin(player).getCommandSpy())
            {
                if (plugin.sl.isStaff(event.getPlayer()) && !plugin.sl.isAdmin(player))
                {
                    continue;
                }
                if (player != event.getPlayer())
                {
                    String s = "//";
                    if (FreedomCommand.getCommandMap().getCommand(s).equals("//"))
                    {
                        FUtil.playerMsg(player, ChatColor.RED + event.getPlayer().getName() + ": " + event.getMessage());
                    }
                    FUtil.playerMsg(player, event.getPlayer().getName() + ": " + event.getMessage());
                }

            }
        }
    }

}
