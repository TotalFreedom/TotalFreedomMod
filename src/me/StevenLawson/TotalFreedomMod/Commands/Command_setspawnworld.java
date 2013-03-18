package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ProtectedArea;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME, ignore_permissions = false)
public class Command_setspawnworld extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Location pos = sender_p.getLocation();
        sender_p.getWorld().setSpawnLocation(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        sender.sendMessage(ChatColor.GRAY + "Spawn location for this world set to: " + TFM_Util.formatLocation(sender_p.getWorld().getSpawnLocation()));

        if (TotalFreedomMod.protectedAreasEnabled && TotalFreedomMod.autoProtectSpawnpoints)
        {
            TFM_ProtectedArea.addProtectedArea("spawn_" + sender_p.getWorld().getName(), pos, TotalFreedomMod.autoProtectRadius);
        }

        return true;
    }
}
