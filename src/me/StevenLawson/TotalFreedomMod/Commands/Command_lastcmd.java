package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Show the last command that someone used.", usage = "/<command> <player>")
public class Command_lastcmd extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        Player player;
        try
        {
            player = getPlayer(args[0]);
        }
        catch (PlayerNotFoundException ex)
        {
            playerMsg(ex.getMessage(), ChatColor.RED);
            return true;
        }

        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);

        if (playerdata != null)
        {
            String last_command = playerdata.getLastCommand();
            if (last_command.isEmpty())
            {
                last_command = "(none)";
            }
            playerMsg(player.getName() + " - Last Command: " + last_command, ChatColor.GRAY);
        }

        return true;
    }
}
