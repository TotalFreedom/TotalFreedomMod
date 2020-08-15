package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.BOTH)
@CommandParameters(description = "Sends a verification code to the player, or the player can input the sent code. Staff can manually verify a player impostor.", usage = "/<command> <code | <playername>>")
public class Command_verify extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.dc.enabled)
        {
            msg("The Discord verification system is currently disabled", ChatColor.RED);
            return true;
        }

        if (senderIsConsole)
        {
            msg("/manuallyverify <playername>", ChatColor.WHITE);
            return true;
        }

        if (!plugin.pl.isImposter(playerSender))
        {
            msg("You are not an impostor, therefore you do not need to verify.", ChatColor.RED);
            return true;
        }

        PlayerData playerData = plugin.pl.getData(playerSender);

        String discordId = playerData.getDiscordID();

        if (playerData.getDiscordID() == null)
        {
            msg("You do not have a Discord account linked to your Minecraft account, please verify the manual way.", ChatColor.RED);
            return true;
        }

        if (args.length == 0)
        {
            String code = plugin.dc.generateCode(10);
            plugin.dc.addVerificationCode(code, playerData);
            plugin.dc.bot.getUserById(discordId).openPrivateChannel().complete().sendMessage("A user with the IP `" + FUtil.getIp(playerSender) + "` has sent a verification request. Please run the following in-game command: `/verify " + code + "`").complete();
            msg("A verification code has been sent to your account, please copy the code and run /verify <code>", ChatColor.GREEN);
            return true;
        }

        String code = args[0];
        String backupCode = null;

        if (plugin.pl.isImposter(playerSender))
        {
            PlayerData mapPlayer = plugin.dc.getVerificationCodes().get(code);
            if (mapPlayer == null)
            {
                if (!playerData.getBackupCodes().contains(plugin.dc.getMD5(code)))
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
                plugin.dc.removeVerificationCode(code);
            }

            final FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
            if (fPlayer.getFreezeData().isFrozen())
            {
                fPlayer.getFreezeData().setFrozen(false);
                msg("You have been unfrozen.");
            }
            FUtil.bcastMsg(playerSender.getName() + " has verified!", ChatColor.GOLD);
            playerSender.setOp(true);
            plugin.pl.verify(playerSender, backupCode);
            return true;
        }
        return true;
    }
}