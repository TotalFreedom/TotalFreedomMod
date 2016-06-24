package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "You'll never even see it coming.", usage = "/<command> <on [radius (default=25)] | off>")
public class Command_fuckoff extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        FPlayer player = plugin.pl.getPlayer(playerSender);

        if (!args[0].equals("on"))
        {
            player.disableFuckoff();
        }
        else
        {

            double radius = 25.0;
            if (args.length >= 2)
            {
                try
                {
                    radius = Math.max(5.0, Math.min(50, Double.parseDouble(args[1])));
                }
                catch (NumberFormatException ex)
                {
                }
            }

            player.setFuckoff(radius);
        }

        msg("Fuckoff " + (player.isFuckOff() ? ("enabled. Radius: " + player.getFuckoffRadius() + ".") : "disabled."));

        return true;
    }
}
