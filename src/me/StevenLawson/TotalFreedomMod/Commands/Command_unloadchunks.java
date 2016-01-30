package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Unloads chunks not currently in use.", usage = "/<command>", aliases = "ul,rc,removechunks")
public class Command_unloadchunks extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        int numChunks = 0;
        for (World world : server.getWorlds())
        {
            numChunks += unloadUnusedChunks(world);
        }
        TFM_Util.adminAction(sender.getName(), "Unloading unused chunks", true);
        
        if (!senderIsConsole)
        {
            sender_p.sendMessage(numChunks + " chunks unloaded.");
        }
        TFM_Log.info(numChunks + " chunks unloaded.");
        return true;
    }

    private int unloadUnusedChunks(World world)
    {
        int numChunks = 0;
        for (Chunk loadedChunk : world.getLoadedChunks())
        {
            if (!world.isChunkInUse(loadedChunk.getX(), loadedChunk.getZ()))
            {
                if (world.unloadChunk(loadedChunk))
                {
                    numChunks++;
                }
            }
        }
        return numChunks;
    }
}
