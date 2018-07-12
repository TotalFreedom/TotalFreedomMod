package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.discord.Discord;
import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilder;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Random;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Sends a verification code to the player, or the player can input the sent code.", usage = "/<command> [code]")
public class Command_verify extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (!plugin.dc.enabled)
        {
            msg("The discord verification system is currently disabled", ChatColor.RED);
            return true;
        }

        if (!plugin.al.isAdminImpostor(playerSender) && !plugin.pv.isPlayerImpostor(playerSender))
        {
            msg("You are not an imposter, therefore you do not need to verify.", ChatColor.RED);
            return true;
        }

        String discordId = "";

        if (plugin.al.isAdminImpostor(playerSender))
        {
            Admin admin = plugin.al.getEntryByName(playerSender.getName());
            if (admin.getDiscordID() == null)
            {
                msg("You do not have a discord account linked to your minecraft account, please verify the manual way.", ChatColor.RED);
                return true;
            }
            discordId = admin.getDiscordID();
        }
        else
        {
            if (plugin.pv.isPlayerImpostor(playerSender))
            {
                if (plugin.pv.getVerificationPlayer(playerSender).getDiscordId() == null)
                {
                    msg("You do not have a discord account linked to your minecraft account, please verify the manual way.", ChatColor.RED);
                    return true;
                }
                discordId = plugin.pv.getVerificationPlayer(playerSender).getDiscordId();
            }
        }

        if (args.length < 1)
        {
            String code = "";
            Random random = new Random();
            for (int i = 0; i < 10; i++)
            {
                code += random.nextInt(10);
            }
            Discord.VERIFY_CODES.add(code);
            Discord.bot.getUserById(discordId).openPrivateChannel().complete().sendMessage("A user with the ip `" + Ips.getIp(playerSender) + "` has sent a verification request. Please run the following in-game command: `/verify " + code + "`").complete();
            msg("A verification code has been sent to your account, please copy the code and run /verify <code>", ChatColor.GREEN);
        }
        else
        {
            String code = args[0];
            if (!Discord.VERIFY_CODES.contains(code))
            {
                msg("You have entered an invalid verification code", ChatColor.RED);
                return true;
            }
            else
            {
                if (plugin.al.isAdminImpostor(playerSender))
                {
                    Admin admin = plugin.al.getEntryByName(playerSender.getName());
                    Discord.VERIFY_CODES.remove(code);
                    FUtil.bcastMsg(playerSender.getName() + " has verified themself!", ChatColor.GOLD);
                    FUtil.adminAction(ConfigEntry.SERVER_NAME.getString(), "Readding " + admin.getName() + " to the admin list", true);
                    if (playerSender != null)
                    {
                        admin.setName(playerSender.getName());
                        admin.addIp(Ips.getIp(playerSender));
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
                            if (playerSender != null)
                            {
                                masterBuilder.setName(playerSender.getName());
                                masterBuilder.addIp(Ips.getIp(playerSender));
                            }

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
                    if (playerSender != null)
                    {
                        plugin.rm.updateDisplay(playerSender);
                    }
                    playerSender.setOp(true);
                    msg(YOU_ARE_OP);
                    final FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg("You have been unfrozen.");
                    }
                }
                else
                {
                    final FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
                    FUtil.bcastMsg(playerSender.getName() + " has verified themself!", ChatColor.GOLD);
                    if (playerSender != null)
                    {
                        plugin.rm.updateDisplay(playerSender);
                    }
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg("You have been unfrozen.");
                    }
                    plugin.pv.verifyPlayer(playerSender);
                }
            }
            return true;
        }
        return true;
    }
}