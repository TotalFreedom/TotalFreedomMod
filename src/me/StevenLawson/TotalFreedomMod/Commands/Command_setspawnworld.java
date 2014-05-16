package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_ProtectedArea;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Set world spawnpoint.", usage = "/<command>")
public class Command_setspawnworld extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Location pos = sender_p.getLocation();
        sender_p.getWorld().setSpawnLocation(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        playerMsg("Spawn location for this world set to: " + TFM_Util.formatLocation(sender_p.getWorld().getSpawnLocation()));

        if (TFM_ConfigEntry.PROTECTAREA_ENABLED.getBoolean() && TFM_ConfigEntry.PROTECTAREA_SPAWNPOINTS.getBoolean())
        {
            TFM_ProtectedArea.addProtectedArea("spawn_" + sender_p.getWorld().getName(), pos, TFM_ConfigEntry.PROTECTAREA_RADIUS.getDouble());
        }

        return true;
    }
}
