package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Quickly change your own gamemode to spectator.", usage = "/<command>", aliases = "gmsp")
public class Command_spectator extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Player player;
        if (args.length == 0)
        {
            player = sender_p;
        }
        else
        {
            if (!(senderIsConsole || TFM_AdminList.isSuperAdmin(sender)))
            {
                playerMsg("You cannot change other players to 'Spectator' mode.");
                return true;
            }
            player = getPlayer(args[0]);
        }
        sender.sendMessage("You set your game mode to 'Spectator'.");
        player.setGameMode(GameMode.SPECTATOR);

        return true;
    }
}
