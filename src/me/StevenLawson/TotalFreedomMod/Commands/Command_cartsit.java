package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Sit in nearest minecart. If target is in a minecart already, they will disembark.", usage = "/<command> [partialname]")
public class Command_cartsit extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Player target_player = sender_p;

        if (args.length == 1)
        {
            try
            {
                target_player = getPlayer(args[0]);
            }
            catch (PlayerNotFoundException ex)
            {
                sender.sendMessage(ex.getMessage());
                return true;
            }
        }

        if (senderIsConsole)
        {
            if (target_player == null)
            {
                sender.sendMessage("When used from the console, you must define a target player: /cartsit <player>");
                return true;
            }
        }
        else if (target_player != sender_p && !TFM_SuperadminList.isUserSuperadmin(sender))
        {
            sender.sendMessage("Only superadmins can select another player as a /cartsit target.");
            return true;
        }

        if (target_player.isInsideVehicle())
        {
            target_player.getVehicle().eject();
        }
        else
        {
            Minecart nearest_cart = null;
            for (Minecart cart : target_player.getWorld().getEntitiesByClass(Minecart.class))
            {
                if (cart.isEmpty())
                {
                    if (nearest_cart == null)
                    {
                        nearest_cart = cart;
                    }
                    else
                    {
                        if (cart.getLocation().distanceSquared(target_player.getLocation()) < nearest_cart.getLocation().distanceSquared(target_player.getLocation()))
                        {
                            nearest_cart = cart;
                        }
                    }
                }
            }

            if (nearest_cart != null)
            {
                nearest_cart.setPassenger(target_player);
            }
            else
            {
                sender.sendMessage("There are no empty minecarts in the target world.");
            }
        }

        return true;
    }
}
