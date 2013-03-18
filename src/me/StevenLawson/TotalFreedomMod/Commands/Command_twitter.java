package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Command_twitter
{
    @CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME, ignore_permissions = false)
    public class Command_cmdlist extends TFM_Command
    {
        @Override
        public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
        {
            if (args.length < 1) {
                return false;
            }
            return true;
        }
    }
}
