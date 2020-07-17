package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Toggle the display of Discord messages in-game.", usage = "/<command>", aliases = "tdiscord,tdisc")
public class Command_togglediscord extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        PlayerData data = plugin.pl.getData(playerSender);
        data.setDiscordDisplay(!data.isDiscordDisplay());
        plugin.pl.save(data);
        msg("Discord message will " + (data.isDiscordDisplay() ? "now" : "no longer") + " be shown.");
        return true;
    }
}
