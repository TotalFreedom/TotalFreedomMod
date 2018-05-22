package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilder;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Manage master builders.", usage = "/<command> <list | reload | | <add | remove | info> <username>>")
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
                msg("Master Builders: " + StringUtils.join(plugin.mbl.getMasterBuilderNames(), ", "), ChatColor.GOLD);

                return true;
            }

            case "reload":
            {
                checkRank(Rank.SENIOR_ADMIN);

                FUtil.adminAction(sender.getName(), "Reloading the Master Builder list", true);
                plugin.mbl.load();
                msg("Master Builder list reloaded!");
                return true;
            }

            case "info":
            {
                if (args.length < 2)
                {
                    return false;
                }

                checkRank(Rank.SUPER_ADMIN);

                MasterBuilder masterBuilder = plugin.mbl.getEntryByName(args[1]);

                if (masterBuilder == null)
                {
                    final Player player = getPlayer(args[1]);
                    if (player != null)
                    {
                        masterBuilder = plugin.mbl.getMasterBuilder(player);
                    }
                }

                if (masterBuilder == null)
                {
                    msg("Master Builder not found: " + args[1]);
                }
                else
                {
                    msg(masterBuilder.toString());
                }

                return true;
            }

            case "add":
            {
                if (args.length < 2)
                {
                    return false;
                }

                checkConsole();
                checkRank(Rank.TELNET_ADMIN);

                // Player already on the list?
                final Player player = getPlayer(args[1]);
                if (player != null && plugin.mbl.isMasterBuilder(player))
                {
                    msg("That player is already on the Master Builder list.");
                    return true;
                }

                // Find the entry
                String name = player != null ? player.getName() : args[1];
                MasterBuilder masterBuilder = null;
                for (MasterBuilder loopMasterBuilder : plugin.mbl.getAllMasterBuilders().values())
                {
                    if (loopMasterBuilder.getName().equalsIgnoreCase(name))
                    {
                        masterBuilder = loopMasterBuilder;
                        break;
                    }
                }

                if (masterBuilder == null) // New entry
                {
                    checkRank(Rank.SENIOR_ADMIN);
                    if (!FUtil.isExecutive(sender.getName()))
                    {
                        noPerms();
                    }

                    if (player == null)
                    {
                        msg(FreedomCommand.PLAYER_NOT_FOUND);
                        return true;
                    }

                    FUtil.adminAction(sender.getName(), "Adding " + player.getName() + " to the Master Builder list", true);
                    plugin.mbl.addMasterBuilder(new MasterBuilder(player));
                    if (player != null)
                    {
                        plugin.rm.updateDisplay(player);
                    }
                }
                else // Existing admin
                {
                    FUtil.adminAction(sender.getName(), "Readding " + masterBuilder.getName() + " to the Master Builder list", true);

                    if (player != null)
                    {
                        masterBuilder.setName(player.getName());
                        masterBuilder.addIp(Ips.getIp(player));
                    }

                    masterBuilder.setLastLogin(new Date());

                    plugin.mbl.save();
                    plugin.mbl.updateTables();
                    if (player != null)
                    {
                        plugin.rm.updateDisplay(player);
                    }
                }

                if (player != null)
                {
                    final FPlayer fPlayer = plugin.pl.getPlayer(player);
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg(player.getPlayer(), "You have been unfrozen.");
                    }

                    if (!player.isOp())
                    {
                        player.setOp(true);
                        player.sendMessage(YOU_ARE_OP);
                    }
                    plugin.pv.removeEntry(player.getName()); // master builders can't have player verification entries
                }
                return true;
            }

            case "remove":
            {
                if (args.length < 2)
                {
                    return false;
                }

                checkConsole();
                checkRank(Rank.SENIOR_ADMIN);
                if (!FUtil.isExecutive(sender.getName()))
                {
                    noPerms();
                }

                Player player = getPlayer(args[1]);
                MasterBuilder masterBuilder = player != null ? plugin.mbl.getMasterBuilder(player) : plugin.mbl.getEntryByName(args[1]);

                if (masterBuilder == null)
                {
                    msg("Matser Builder not found: " + args[1]);
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Removing " + masterBuilder.getName() + " from the Master Builder list", true);
                plugin.mbl.removeMasterBuilder(masterBuilder);
                if (player != null)
                {
                    plugin.rm.updateDisplay(player);
                }
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

}
