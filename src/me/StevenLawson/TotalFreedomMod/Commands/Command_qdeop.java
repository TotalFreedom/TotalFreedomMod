package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Quick De-Op - deop someone based on a partial name.", usage = "/<command> <partialname>")
public class Command_qdeop extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        boolean silent = false;
        if (args.length == 2)
        {
            silent = args[1].equalsIgnoreCase("-s");
        }

        boolean matched_player = false;

        String targetName = args[0].toLowerCase();

        for (Player player : server.getOnlinePlayers())
        {
            if (player.getName().toLowerCase().indexOf(targetName) != -1 || player.getDisplayName().toLowerCase().indexOf(targetName) != -1)
            {
                matched_player = true;

                if (!silent)
                {
                    TFM_Util.adminAction(sender.getName(), "De-opping " + player.getName(), false);
                }
                player.setOp(false);
                player.sendMessage(TotalFreedomMod.YOU_ARE_NOT_OP);
            }
        }

        if (!matched_player)
        {
            playerMsg("No targets matched.");
        }

        return true;
    }
}
