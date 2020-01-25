package me.totalfreedom.totalfreedommod.command;

import java.util.Date;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilder;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.BOTH)
@CommandParameters(description = "Sends a verification code to the player, or the player can input the sent code. Admins can manually verify a player impostor.", usage = "/<command> <code | <playername>>")
public class Command_verify extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (args.length == 1 && plugin.al.isAdmin(playerSender))
        {
            final Player player = getPlayer(args[0]);
            if (player == null)
            {
                msg(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }
            if (!plugin.pv.isPlayerImpostor(player))
            {
                msg("That player is not an impostor.");
                return true;
            }
            FUtil.adminAction(sender.getName(), "Manually verifying player " + player.getName(), false);
            player.setOp(true);
            player.sendMessage(YOU_ARE_OP);
            if (plugin.pl.getPlayer(player).getFreezeData().isFrozen())
            {
                plugin.pl.getPlayer(player).getFreezeData().setFrozen(false);
                player.sendMessage(ChatColor.GRAY + "You have been unfrozen.");
            }
            plugin.pv.verifyPlayer(player, null);
            plugin.rm.updateDisplay(player);
            return true;
        }
        else
        {
            if (!plugin.dc.enabled)
            {
                msg("The Discord verification system is currently disabled", ChatColor.RED);
                return true;
            }

            if (senderIsConsole || plugin.al.isAdmin(playerSender))
            {
                msg("/verify <playername>", ChatColor.WHITE);
                return true;
            }

            if (!plugin.pv.isPlayerImpostor(playerSender) && !plugin.al.isAdminImpostor(playerSender) && !plugin.mbl.isMasterBuilderImpostor(playerSender))
            {
                msg("You are not an impostor, therefore you do not need to verify.", ChatColor.RED);
                return true;
            }

            String discordId = "";

            if (plugin.pv.isPlayerImpostor(playerSender))
            {
                VPlayer vPlayer = plugin.pv.getVerificationPlayer(playerSender);
                if (vPlayer.getDiscordId() == null)
                {
                    msg("You do not have a Discord account linked to your Minecraft account, please verify the manual way.", ChatColor.RED);
                    return true;
                }
                discordId = vPlayer.getDiscordId();
            }
            else if (plugin.al.isAdminImpostor(playerSender))
            {
                Admin admin = plugin.al.getEntryByName(playerSender.getName());
                if (admin.getDiscordID() == null)
                {
                    msg("You do not have a Discord account linked to your Minecraft account, please verify the manual way.", ChatColor.RED);
                    return true;
                }
                discordId = admin.getDiscordID();
            }
            else if (plugin.mbl.isMasterBuilderImpostor(playerSender))
            {
                MasterBuilder masterBuilder = plugin.mbl.getEntryByName(playerSender.getName());
                if (masterBuilder.getDiscordID() == null)
                {
                    msg("You do not have a Discord account linked to your Minecraft account, please verify the manual way.", ChatColor.RED);
                    return true;
                }
                discordId = masterBuilder.getDiscordID();
            }

            if (args.length < 1)
            {
                String code = plugin.dc.generateCode(10);
                if (plugin.pv.isPlayerImpostor(playerSender))
                {
                    VPlayer vPlayer = plugin.pv.getVerificationPlayer(playerSender);
                    plugin.dc.addPlayerVerificationCode(code, vPlayer);
                }
                else if (plugin.al.isAdminImpostor(playerSender))
                {
                    Admin admin = plugin.al.getEntryByName(playerSender.getName());
                    plugin.dc.addAdminVerificationCode(code, admin);
                }
                else if (plugin.mbl.isMasterBuilderImpostor(playerSender))
                {
                    MasterBuilder masterBuilder = plugin.mbl.getEntryByName(playerSender.getName());
                    plugin.dc.addMasterBuilderVerificationCode(code, masterBuilder);
                }
                plugin.dc.bot.getUserById(discordId).openPrivateChannel().complete().sendMessage("A user with the IP `" + Ips.getIp(playerSender) + "` has sent a verification request. Please run the following in-game command: `/verify " + code + "`").complete();
                msg("A verification code has been sent to your account, please copy the code and run /verify <code>", ChatColor.GREEN);
            }
            else
            {
                String code = args[0];
                String backupCode = null;

                if (plugin.pv.isPlayerImpostor(playerSender))
                {
                    VPlayer vPlayer = plugin.pv.getVerificationPlayer(playerSender);
                    VPlayer mapPlayer = plugin.dc.getPlayerVerificationCodes().get(code);
                    if (mapPlayer == null)
                    {
                        if (!vPlayer.getBackupCodes().contains(plugin.dc.getMD5(code)))
                        {
                            msg("You have entered an invalid verification code", ChatColor.RED);
                            return true;
                        }
                        else
                        {
                            backupCode = plugin.dc.getMD5(code);
                        }
                    }
                    else
                    {
                        plugin.dc.removePlayerVerificationCode(code);
                    }

                    final FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
                    FUtil.bcastMsg(playerSender.getName() + " has verified!", ChatColor.GOLD);
                    plugin.rm.updateDisplay(playerSender);
                    playerSender.setOp(true);
                    msg(YOU_ARE_OP);
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg("You have been unfrozen.");
                    }
                    plugin.pv.verifyPlayer(playerSender, backupCode);
                    return true;
                }
                else if (plugin.al.isAdminImpostor(playerSender))
                {
                    Admin admin = plugin.al.getEntryByName(playerSender.getName());
                    Admin mapAdmin = plugin.dc.getAdminVerificationCodes().get(code);
                    if (mapAdmin == null)
                    {
                        if (!admin.getBackupCodes().contains(plugin.dc.getMD5(code)))
                        {
                            msg("You have entered an invalid verification code", ChatColor.RED);
                            return true;
                        }
                        else
                        {
                            backupCode = plugin.dc.getMD5(code);
                        }
                    }
                    else
                    {
                        plugin.dc.removeAdminVerificationCode(code);
                    }

                    FUtil.bcastMsg(playerSender.getName() + " has verified!", ChatColor.GOLD);
                    FUtil.adminAction(ConfigEntry.SERVER_NAME.getString(), "Re-adding " + admin.getName() + " to the admin list", true);

                    admin.setName(playerSender.getName());
                    admin.addIp(Ips.getIp(playerSender));

                    if (backupCode != null)
                    {
                        admin.removeBackupCode(backupCode);
                    }

                    if (!plugin.mbl.isMasterBuilder(playerSender))
                    {
                        MasterBuilder masterBuilder = null;
                        for (MasterBuilder loopMasterBuilder : plugin.mbl.getAllMasterBuilders().values())
                        {
                            if (loopMasterBuilder.getName().equalsIgnoreCase(playerSender.getName()))
                            {
                                masterBuilder = loopMasterBuilder;
                                break;
                            }
                        }

                        if (masterBuilder != null)
                        {
                            masterBuilder.setName(playerSender.getName());
                            masterBuilder.addIp(Ips.getIp(playerSender));

                            masterBuilder.setLastLogin(new Date());

                            plugin.mbl.save();
                            plugin.mbl.updateTables();
                        }
                    }

                    admin.setActive(true);
                    admin.setLastLogin(new Date());
                    plugin.al.save();
                    plugin.al.updateTables();
                    plugin.rm.updateDisplay(playerSender);
                    playerSender.setOp(true);
                    msg(YOU_ARE_OP);
                    final FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg("You have been unfrozen.");
                    }
                    return true;
                }
                else if (plugin.mbl.isMasterBuilderImpostor(playerSender))
                {
                    MasterBuilder masterBuilder = plugin.mbl.getEntryByName(playerSender.getName());
                    MasterBuilder mapMasterBuilder = plugin.dc.getMasterBuilderVerificationCodes().get(code);
                    if (mapMasterBuilder == null)
                    {
                        if (!masterBuilder.getBackupCodes().contains(plugin.dc.getMD5(code)))
                        {
                            msg("You have entered an invalid verification code", ChatColor.RED);
                            return true;
                        }
                        else
                        {
                            backupCode = plugin.dc.getMD5(code);
                        }
                    }
                    else
                    {
                        plugin.dc.removeMasterBuilderVerificationCode(code);
                    }

                    if (backupCode != null)
                    {
                        masterBuilder.removeBackupCode(backupCode);
                    }

                    final FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
                    FUtil.bcastMsg(playerSender.getName() + " has verified!", ChatColor.GOLD);
                    masterBuilder.setLastLogin(new Date());
                    masterBuilder.addIp(Ips.getIp(playerSender));
                    plugin.mbl.save();
                    plugin.mbl.updateTables();
                    plugin.rm.updateDisplay(playerSender);
                    playerSender.setOp(true);
                    msg(YOU_ARE_OP);
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg("You have been unfrozen.");
                    }
                    return true;
                }
            }
        }
        return true;
    }
}