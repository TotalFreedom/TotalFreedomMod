package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Release parrots from your shoulders.", usage = "/<command>", aliases = "removeparrots")
public class Command_releaseparrots extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Entity leftShoulderEntity = playerSender.getShoulderEntityLeft();
        Entity rightShoulderEntity = playerSender.getShoulderEntityRight();

        if (rightShoulderEntity == null && leftShoulderEntity == null)
        {
            msg("No parrots were detected on either of your shoulders.");
            return true;
        }

        if (leftShoulderEntity != null && leftShoulderEntity.getType().equals(EntityType.PARROT))
        {
            playerSender.setShoulderEntityLeft(null);
            msg("Removed the parrot on your left shoulder.");
        }

        if (rightShoulderEntity != null && rightShoulderEntity.getType().equals(EntityType.PARROT))
        {
            playerSender.setShoulderEntityRight(null);
            msg("Removed the parrot on your right shoulder.");
        }
        return true;
    }
}
