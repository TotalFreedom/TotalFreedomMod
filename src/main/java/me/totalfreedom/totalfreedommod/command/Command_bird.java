package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import java.util.Random;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = ":goodbird:", usage = "/<command>")
public class Command_bird extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Location location = playerSender.getTargetBlock(null, 15).getLocation().add(0, 1, 0);
        playerSender.getWorld().spawnEntity(location, getRandomFish());
        msg(":goodbird:");
        return true;
    }

    public EntityType getRandomFish()
    {
        List<EntityType> fishTypes = Arrays.asList(EntityType.COD, EntityType.SALMON, EntityType.PUFFERFISH, EntityType.TROPICAL_FISH);
        Random random = new Random();
        return fishTypes.get(random.nextInt(fishTypes.size()));
    }
}
