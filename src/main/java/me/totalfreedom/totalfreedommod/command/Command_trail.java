package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.DepreciationAggregator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.RegisteredListener;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Pretty rainbow trails.", usage = "/<command> [off]")
public class Command_trail extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0 && "off".equals(args[0]))
        {
            plugin.tr.remove(playerSender);
            msg("Trail disabled.");
        }
        else
        {
            plugin.tr.add(playerSender);
            msg("Trail enabled. Use \"/trail off\" to disable.");
        }

        return true;
    }

}
