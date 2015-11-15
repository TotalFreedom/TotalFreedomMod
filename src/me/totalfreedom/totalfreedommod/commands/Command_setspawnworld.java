package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Set world spawnpoint.", usage = "/<command>")
public class Command_setspawnworld extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Location pos = sender_p.getLocation();
        sender_p.getWorld().setSpawnLocation(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        playerMsg("Spawn location for this world set to: " + FUtil.formatLocation(sender_p.getWorld().getSpawnLocation()));

        if (ConfigEntry.PROTECTAREA_ENABLED.getBoolean() && ConfigEntry.PROTECTAREA_SPAWNPOINTS.getBoolean())
        {
            plugin.pa.addProtectedArea("spawn_" + sender_p.getWorld().getName(), pos, ConfigEntry.PROTECTAREA_RADIUS.getDouble());
        }

        return true;
    }
}
