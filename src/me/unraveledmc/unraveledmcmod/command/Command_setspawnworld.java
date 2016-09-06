package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Set world spawnpoint.", usage = "/<command>")
public class Command_setspawnworld extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Location pos = playerSender.getLocation();
        playerSender.getWorld().setSpawnLocation(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        msg("Spawn location for this world set to: " + FUtil.formatLocation(playerSender.getWorld().getSpawnLocation()));

        if (ConfigEntry.PROTECTAREA_ENABLED.getBoolean() && ConfigEntry.PROTECTAREA_SPAWNPOINTS.getBoolean())
        {
            plugin.pa.addProtectedArea("spawn_" + playerSender.getWorld().getName(), pos, ConfigEntry.PROTECTAREA_RADIUS.getDouble());
        }

        return true;
    }
}
