package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Unloads chunks not currently in use", usage = "/<command>", aliases = "rc")
public class Command_unloadchunks extends FreedomCommand {

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        FUtil.adminAction(sender.getName(), "Unloading unused chunks", false);

        int numChunks = 0;

        for (World world : server.getWorlds()) {
            numChunks += unloadUnusedChunks(world);
        }

        FUtil.playerMsg(sender, numChunks + " chunks unloaded.");
        return true;
    }

    private int unloadUnusedChunks(World world) {
        int numChunks = 0;

        for (Chunk loadedChunk : world.getLoadedChunks()) {
            if (!world.isChunkInUse(loadedChunk.getX(), loadedChunk.getZ())) {
                if (world.unloadChunk(loadedChunk)) {
                    numChunks++;
                }
            }
        }

        return numChunks;
    }
}
