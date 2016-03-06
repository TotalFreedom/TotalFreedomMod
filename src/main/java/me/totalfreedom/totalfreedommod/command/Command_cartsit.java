package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Sit in nearest minecart. If target is in a minecart already, they will disembark.", usage = "/<command> [partialname]")
public class Command_cartsit extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Player targetPlayer = playerSender;

        if (args.length == 1)
        {

            targetPlayer = getPlayer(args[0]);

            if (targetPlayer == null)
            {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }
        }

        if (senderIsConsole)
        {
            if (targetPlayer == null)
            {
                sender.sendMessage("When used from the console, you must define a target player: /cartsit <player>");
                return true;
            }
        }
        else if (targetPlayer != playerSender && !isAdmin(sender))
        {
            sender.sendMessage("Only superadmins can select another player as a /cartsit target.");
            return true;
        }

        if (targetPlayer.isInsideVehicle())
        {
            targetPlayer.getVehicle().eject();
        }
        else
        {
            Minecart nearest_cart = null;
            for (Minecart cart : targetPlayer.getWorld().getEntitiesByClass(Minecart.class))
            {
                if (cart.isEmpty())
                {
                    if (nearest_cart == null)
                    {
                        nearest_cart = cart;
                    }
                    else
                    {
                        if (cart.getLocation().distanceSquared(targetPlayer.getLocation()) < nearest_cart.getLocation().distanceSquared(targetPlayer.getLocation()))
                        {
                            nearest_cart = cart;
                        }
                    }
                }
            }

            if (nearest_cart != null)
            {
                nearest_cart.setPassenger(targetPlayer);
            }
            else
            {
                sender.sendMessage("There are no empty minecarts in the target world.");
            }
        }

        return true;
    }
}
