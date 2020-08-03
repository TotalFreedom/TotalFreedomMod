package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Sit at the current place you are at.", usage = "/<command>")
public class Command_sit extends FreedomCommand
{
    public static List<ArmorStand> STANDS = new ArrayList<>();

    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        if (args.length != 0)
        {
            return false;
        }
        ArmorStand stand = (ArmorStand) playerSender.getWorld().spawnEntity(playerSender.getLocation().clone().subtract(0.0, 1.7, 0.0), EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setAI(false);
        stand.setVisible(false);
        stand.setInvulnerable(true);
        stand.addPassenger(playerSender);
        STANDS.add(stand);
        msg("You are now sitting.");
        return true;
    }
}
