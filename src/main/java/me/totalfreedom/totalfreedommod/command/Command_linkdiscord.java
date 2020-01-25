package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.discord.Discord;
import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilder;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
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

        String code;

        if (plugin.al.isAdmin(playerSender))
        {
            Admin admin = plugin.al.getAdmin(playerSender);
            if (admin.getDiscordID() != null)
            {
                msg("Your Minecraft account is already linked to a Discord account.", ChatColor.RED);
                return true;
            }

            if (Discord.ADMIN_LINK_CODES.containsValue(admin))
            {
                code = Discord.getCodeForAdmin(admin);
            }
            else
            {
                code = plugin.dc.generateCode(5);
                Discord.ADMIN_LINK_CODES.put(code, admin);
            }
        }
        else if (plugin.mbl.isMasterBuilder(playerSender))
        {
            MasterBuilder masterBuilder = plugin.mbl.getMasterBuilder(playerSender);
            if (masterBuilder.getDiscordID() != null)
            {
                msg("Your Minecraft account is already linked to a Discord account.", ChatColor.RED);
                return true;
            }

            if (Discord.MASTER_BUILDER_LINK_CODES.containsValue(masterBuilder))
            {
                code = Discord.getCodeForMasterBuilder(masterBuilder);
            }
            else
            {
                code = plugin.dc.generateCode(5);
                Discord.MASTER_BUILDER_LINK_CODES.put(code, masterBuilder);
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
                code = Discord.getCodeForPlayer(data);
            }
            else
            {
                code = plugin.dc.generateCode(5);
                Discord.PLAYER_LINK_CODES.put(code, data);
            }
        }
        msg("Your linking code is " + ChatColor.AQUA + code, ChatColor.GREEN);
        msg("Take this code and DM the server bot (" + plugin.dc.formatBotTag() + ") the code (do not put anything else in the message, only the code)");
        return true;
    }
}
