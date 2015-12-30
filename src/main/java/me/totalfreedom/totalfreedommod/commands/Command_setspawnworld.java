package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Set world spawnpoint.", usage = "/<command> [-b]")
public class Command_setspawnworld extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Location pos = playerSender.getLocation();
        playerSender.getWorld().setSpawnLocation(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        playerMsg("Spawn location for this world set to: " + FUtil.formatLocation(playerSender.getWorld().getSpawnLocation()));
 
        if (ConfigEntry.PROTECTAREA_ENABLED.getBoolean() && ConfigEntry.PROTECTAREA_SPAWNPOINTS.getBoolean())
        {
            plugin.pa.addProtectedArea("spawn_" + playerSender.getWorld().getName(), pos, ConfigEntry.PROTECTAREA_RADIUS.getDouble());
        }
        if (args.length = 0)
        {
            return true;
        }
        else
        {
            if (args[0].equals("-b"))
            {
                TFM_Util.bcastMsg(String.format("[Server:%s] %s", sender.getName(), " New spawn!"), ChatColor.LIGHT_PURPLE);
            }
        return true;
    }
}
