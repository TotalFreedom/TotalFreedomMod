package me.totalfreedom.totalfreedommod.command;

import java.util.Random;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.discord.Discord;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Link your Discord account to your Minecraft account", usage = "/<command>")
public class Command_linkdiscord extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.dc.enabled)
        {
            msg("The Discord verification system is currently disabled.", ChatColor.RED);
            return true;
        }

        if (plugin.al.isAdmin(playerSender))
        {
            Admin admin = plugin.al.getAdmin(playerSender);
            if (admin.getDiscordID() != null)
            {
                msg("Your Minecraft account is already linked to a Discord account.", ChatColor.RED);
                return true;
            }

            if (Discord.LINK_CODES.containsValue(admin))
            {
                msg("Your linking code is " + ChatColor.GREEN + Discord.getCodeForAdmin(admin), ChatColor.AQUA);
            }
            else
            {
                String code = "";
                Random random = new Random();
                for (int i = 0; i < 5; i++)
                {
                    code += random.nextInt(10);
                }
                Discord.LINK_CODES.put(code, admin);
                msg("Your linking code is " + ChatColor.GREEN + code, ChatColor.AQUA);
            }
        }
        else
        {
            VPlayer data = plugin.pv.getVerificationPlayer(playerSender);
            if (data.getDiscordId() != null)
            {
                msg("Your Minecraft account is already linked to a Discord account.", ChatColor.RED);
                return true;
            }

            if (Discord.PLAYER_LINK_CODES.containsValue(data))
            {
                msg("Your linking code is " + ChatColor.GREEN + Discord.getCodeForPlayer(data), ChatColor.AQUA);
            }
            else
            {
                String code = "";
                Random random = new Random();
                for (int i = 0; i < 5; i++)
                {
                    code += random.nextInt(10);
                }
                Discord.PLAYER_LINK_CODES.put(code, data);
                msg("Your linking code is " + ChatColor.GREEN + code, ChatColor.AQUA);
            }
        }
        return true;
    }
}
