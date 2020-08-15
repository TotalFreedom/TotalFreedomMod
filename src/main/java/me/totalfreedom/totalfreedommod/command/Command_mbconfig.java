package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "List, add, or remove master builders, reload the master builder list, or view the info of master builders.", usage = "/<command> <list | <<add | remove> <username>>>")
public class Command_mbconfig extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        switch (args[0])
        {
            case "list":
            {
                msg("Master Builders: " + StringUtils.join(plugin.pl.getMasterBuilderNames(), ", "), ChatColor.GOLD);
                return true;
            }

            case "add":
            {
                if (args.length < 2)
                {
                    return false;
                }

                if (!plugin.pl.canManageMasterBuilders(sender.getName()))
                {
                    return noPerms();
                }

                final Player player = getPlayer(args[1]);

                PlayerData data;

                if (player == null)
                {
                    data = plugin.pl.getData(args[1]);
                    if (data == null)
                    {
                        msg(PLAYER_NOT_FOUND);
                        return true;
                    }
                }
                else
                {
                    data = plugin.pl.getData(player);
                }

                if (data.isMasterBuilder() && plugin.pl.isPlayerImpostor(player))
                {
                    FUtil.staffAction(sender.getName(), "Re-adding " + player.getName() + " to the Master Builder list", true);
                    player.setOp(true);
                    player.sendMessage(YOU_ARE_OP);

                    if (plugin.pl.getPlayer(player).getFreezeData().isFrozen())
                    {
                        plugin.pl.getPlayer(player).getFreezeData().setFrozen(false);
                        player.sendMessage(ChatColor.GRAY + "You have been unfrozen.");
                    }
                    plugin.pl.verify(player, null);
                    plugin.rm.updateDisplay(player);
                }
                else if (!data.isMasterBuilder())
                {
                    FUtil.staffAction(sender.getName(), "Adding " + player.getName() + " to the Master Builder list", true);
                    data.setMasterBuilder(true);
                    data.setVerification(true);
                    plugin.pl.save(data);
                    plugin.rm.updateDisplay(player);
                    return true;
                }
                else
                {
                    msg("That player is already on the Master Builder list.");
                    return true;
                }
            }
            case "remove":
            {
                if (args.length < 2)
                {
                    return false;
                }

                if (!plugin.pl.canManageMasterBuilders(sender.getName()))
                {
                    return noPerms();
                }

                Player player = getPlayer(args[1]);
                PlayerData data = plugin.pl.getData(player);

                if (!data.isMasterBuilder())
                {
                    msg("Master Builder not found: " + args[1]);
                    return true;
                }

                FUtil.staffAction(sender.getName(), "Removing " + data.getName() + " from the Master Builder list", true);
                data.setMasterBuilder(false);
                if (data.getDiscordID() == null)
                {
                    data.setVerification(false);
                }
                plugin.pl.save(data);
                plugin.rm.updateDisplay(player);
                return true;
            }

            default:
            {
                return false;
            }
        }
    }
    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            return Arrays.asList("add", "remove", "list");
        }
        else if (args.length == 2)
        {
            if (args[0].equals("add"))
            {
                return FUtil.getPlayerList();
            }
            else if (args[0].equals("remove"))
            {
                return plugin.pl.getMasterBuilderNames();
            }
        }
        return Collections.emptyList();
    }


}
