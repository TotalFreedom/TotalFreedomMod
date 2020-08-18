package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "List, add, remove, or set the rank of staff, clean or reload the staff list, or view the info of staff.", usage = "/<command> <list | clean | reload | | setrank <username> <rank> | <add | remove | info> <username>>")
public class Command_slconfig extends FreedomCommand
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
                msg("Staff: " + StringUtils.join(plugin.sl.getAdminNames(), ", "), ChatColor.GOLD);
                return true;
            }

            case "clean":
            {
                checkConsole();
                checkRank(Rank.ADMIN);

                FUtil.staffAction(sender.getName(), "Cleaning staff list", true);
                plugin.sl.deactivateOldEntries(true);
                msg("Staff: " + StringUtils.join(plugin.sl.getAdminNames(), ", "), ChatColor.GOLD);

                return true;
            }

            case "reload":
            {
                checkRank(Rank.ADMIN);

                FUtil.staffAction(sender.getName(), "Reloading the staff list", true);
                plugin.sl.load();
                msg("Staff list reloaded!");
                return true;
            }

            case "setrank":
            {
                checkConsole();
                checkRank(Rank.ADMIN);

                if (args.length < 3)
                {
                    return false;
                }

                Rank rank = Rank.findRank(args[2]);
                if (rank == null)
                {
                    msg("Unknown rank: " + args[2]);
                    return true;
                }

                if (rank.isConsole())
                {
                    msg("You cannot set players to a console rank");
                    return true;
                }

                if (!rank.isAtLeast(Rank.TRIAL_MOD))
                {
                    msg("Rank must be Trial Mod or higher.", ChatColor.RED);
                    return true;
                }

                StaffMember staffMember = plugin.sl.getEntryByName(args[1]);
                if (staffMember == null)
                {
                    msg("Unknown staff member: " + args[1]);
                    return true;
                }

                FUtil.staffAction(sender.getName(), "Setting " + staffMember.getName() + "'s rank to " + rank.getName(), true);

                staffMember.setRank(rank);
                plugin.sl.save(staffMember);

                Player player = getPlayer(staffMember.getName());
                if (player != null)
                {
                    plugin.rm.updateDisplay(player);
                }

                if (plugin.dc.enabled && ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                {
                    plugin.dc.syncRoles(staffMember, plugin.pl.getData(staffMember.getName()).getDiscordID());
                }

                plugin.amp.updateAccountStatus(staffMember);

                msg("Set " + staffMember.getName() + "'s rank to " + rank.getName());
                return true;
            }

            case "info":
            {
                if (args.length < 2)
                {
                    return false;
                }

                checkRank(Rank.TRIAL_MOD);

                StaffMember staffMember = plugin.sl.getEntryByName(args[1]);

                if (staffMember == null)
                {
                    final Player player = getPlayer(args[1]);
                    if (player != null)
                    {
                        staffMember = plugin.sl.getAdmin(player);
                    }
                }

                if (staffMember == null)
                {
                    msg("Staff member not found: " + args[1]);
                }
                else
                {
                    msg(staffMember.toString());
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
                checkRank(Rank.MOD);

                // Player already staff?
                final Player player = getPlayer(args[1]);

                if (player == null)
                {
                    msg(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }

                if (plugin.sl.isStaff(player))
                {
                    msg("That player is already a staff member.");
                    return true;
                }

                // Find the old staff entry
                String name = player != null ? player.getName() : args[1];
                StaffMember staffMember = null;
                for (StaffMember loopStaffMember : plugin.sl.getAllStaffMembers())
                {
                    if (loopStaffMember.getName().equalsIgnoreCase(name) || loopStaffMember.getIps().contains(FUtil.getIp(player)))
                    {
                        staffMember = loopStaffMember;
                        break;
                    }
                }

                if (plugin.pl.isPlayerImpostor(player))
                {
                    msg("This player was labeled as a Player impostor and is not a staff member, therefore they cannot be added to the staff list.", ChatColor.RED);
                    return true;
                }

                if (staffMember == null) // New staff member
                {
                    if (player == null)
                    {
                        msg(FreedomCommand.PLAYER_NOT_FOUND);
                        return true;
                    }

                    FUtil.staffAction(sender.getName(), "Adding " + player.getName() + " to the staff list", true);
                    staffMember = new StaffMember(player);

                    plugin.sl.addAdmin(staffMember);
                    plugin.rm.updateDisplay(player);
                    plugin.amp.updateAccountStatus(staffMember);
                }
                else // Existing staff member
                {
                    FUtil.staffAction(sender.getName(), "Re-adding " + player.getName() + " to the staff list", true);

                    if (player != null)
                    {
                        String oldName = staffMember.getName();
                        if (oldName != player.getName())
                        {
                            staffMember.setName(player.getName());
                            plugin.sql.updateStaffMemberName(oldName, staffMember.getName());
                        }
                        staffMember.addIp(FUtil.getIp(player));
                    }

                    staffMember.setActive(true);
                    staffMember.setLastLogin(new Date());

                    if (plugin.sl.isVerifiedStaff(player))
                    {
                        plugin.sl.verifiedNoStaff.remove(player.getName());
                        plugin.sl.verifiedNoStaffIps.remove(player.getName());
                    }

                    plugin.sl.save(staffMember);
                    plugin.sl.updateTables();
                    if (player != null)
                    {
                        plugin.rm.updateDisplay(player);
                    }

                    if (plugin.dc.enabled && ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                    {
                        plugin.dc.syncRoles(staffMember, plugin.pl.getData(player).getDiscordID());
                    }
                    plugin.amp.updateAccountStatus(staffMember);
                }

                if (player != null)
                {
                    final FPlayer fPlayer = plugin.pl.getPlayer(player);
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg(player, "You have been unfrozen.");
                    }

                    if (!player.isOp())
                    {
                        player.setOp(true);
                        player.sendMessage(YOU_ARE_OP);
                    }
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
                checkRank(Rank.MOD);

                Player player = getPlayer(args[1]);
                StaffMember staffMember = player != null ? plugin.sl.getAdmin(player) : plugin.sl.getEntryByName(args[1]);

                if (staffMember == null)
                {
                    msg("Staff member not found: " + args[1]);
                    return true;
                }

                FUtil.staffAction(sender.getName(), "Removing " + staffMember.getName() + " from the staff list", true);
                staffMember.setActive(false);

                if (plugin.pl.getPlayer(player).inStaffChat())
                {
                    plugin.pl.getPlayer(player).setStaffChat(false);
                }

                plugin.sl.save(staffMember);
                plugin.sl.updateTables();
                if (player != null)
                {
                    plugin.rm.updateDisplay(player);
                }

                if (plugin.dc.enabled && ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                {
                    plugin.dc.syncRoles(staffMember, plugin.pl.getData(staffMember.getName()).getDiscordID());
                }

                plugin.amp.updateAccountStatus(staffMember);

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
            List<String> arguments = new ArrayList<>();
            arguments.add("list");
            if (plugin.sl.isStaff(sender))
            {
                arguments.add("info");
            }
            if (plugin.sl.isMod(sender))
            {
                arguments.add("add");
                arguments.add("remove");
            }
            if (plugin.sl.isAdmin(sender))
            {
                arguments.add("reload");
                arguments.add("clean");
                arguments.add("setrank");
            }
            return arguments;
        }
        if (args.length == 2 && (args[0].equals("add") || args[0].equals("remove") || args[0].equals("setrank") || args[0].equals("info")))
        {
            return FUtil.getPlayerList();
        }
        if (args.length == 3 && args[0].equals("setrank"))
        {
            return Arrays.asList("TRIAL_MOD", "MOD", "ADMIN");
        }

        return Collections.emptyList();
    }
}