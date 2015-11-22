package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.NON_OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Teleport to the spawn point for the current world.", usage = "/<command>", aliases = "worldspawn,gotospawn")
public class Command_localspawn extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        playerSender.teleport(playerSender.getWorld().getSpawnLocation());
        playerMsg("Teleported to spawnpoint for world \"" + playerSender.getWorld().getName() + "\".");
        return true;
    }
}
