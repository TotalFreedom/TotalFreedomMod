package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Freeze players (toggles on and off).", usage = "/<command> [target | purge]")
public class Command_fr extends FreedomCommand
{
    private static boolean allFrozen = false;

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            allFrozen = !allFrozen;

            if (allFrozen)
            {
                FUtil.adminAction(sender.getName(), "Freezing all players", false);

                setAllFrozen(true);
                playerMsg("Players are now frozen.");

                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (!isAdmin(player))
                    {
                        playerMsg(player, "You have been frozen due to rulebreakers, you will be unfrozen soon.", ChatColor.RED);
                    }
                }
            }
            else
            {
                FUtil.adminAction(sender.getName(), "Unfreezing all players", false);
                setAllFrozen(false);
                playerMsg("Players are now free to move.");
            }
        }
        else
        {
            if (args[0].toLowerCase().equals("purge"))
            {
                setAllFrozen(false);
                FUtil.adminAction(sender.getName(), "Unfreezing all players", false);
            }
            else
            {
                final Player player = getPlayer(args[0]);

                if (player == null)
                {
                    playerMsg(FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
                    return true;
                }

                final FPlayer playerdata = plugin.pl.getPlayer(player);
                playerdata.setFrozen(!playerdata.isFrozen());

                playerMsg(player.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                playerMsg(player, "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".", ChatColor.AQUA);
            }
        }

        return true;
    }

    public static void setAllFrozen(boolean freeze)
    {
        allFrozen = freeze;
        for (FPlayer data : TotalFreedomMod.plugin.pl.playerMap.values())
        {
            data.setFrozen(freeze);
        }
    }
}
