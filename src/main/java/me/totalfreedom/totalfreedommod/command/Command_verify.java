package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.discord.Discord;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.Random;
import java.util.Date;
import net.pravian.aero.util.Ips;

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

        if (!plugin.al.isAdminImpostor(playerSender))
        {
            msg("You are not an imposter, therefore you do not need to verify.", ChatColor.RED);
            return true;
        }

        Admin admin = plugin.al.getEntryByName(playerSender.getName());

        if (admin.getDiscordID() == null)
        {
            msg("You do not have a discord account linked to your minecraft account, please verify the manual way.", ChatColor.RED);
            return true;
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
            Discord.bot.getUserById(admin.getDiscordID()).openPrivateChannel().complete().sendMessage("A user with the ip `" + Ips.getIp(playerSender) + "` has sent a verification request. Please run the following in-game command: `/verify " + code + "`").complete();
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
                Discord.VERIFY_CODES.remove(code);
                FUtil.bcastMsg(playerSender.getName() + " has verified themself!", ChatColor.GOLD);
                FUtil.adminAction(ConfigEntry.SERVER_NAME.getString(), "Readding " + admin.getName() + " to the admin list", true);
                admin.setName(playerSender.getName());
                admin.addIp(Ips.getIp(playerSender));
                admin.setActive(true);
                admin.setLastLogin(new Date());
                plugin.al.save();
                plugin.al.updateTables();
                final FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
                if (fPlayer.getFreezeData().isFrozen())
                {
                    fPlayer.getFreezeData().setFrozen(false);
                    msg("You have been unfrozen.");
                }
            }
        }
        return true;
    }
}