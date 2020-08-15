package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Stops all sounds or a specified sound.", usage = "/<command> [sound]")
public class Command_stopsound extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0)
        {
            Sound sound = null;

            for (Sound loop : Sound.values())
            {
                if (loop != null && loop.name().equalsIgnoreCase(args[0]))
                {
                    sound = Sound.valueOf(args[0].toUpperCase());
                    break;
                }
            }

            if (sound == null)
            {
                msg(args[0] + " is not a valid sound.", ChatColor.RED);
                return true;
            }

            playerSender.stopSound(sound);
            msg("Stopped all " + sound.name() + " sounds", ChatColor.GREEN);
            return true;
        }

        for (Sound sound : Sound.values())
        {
            playerSender.stopSound(sound);
        }

        msg("Stopped all sounds.", ChatColor.GREEN);
        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            return soundList();
        }
        return Collections.emptyList();
    }

    public List<String> soundList()
    {
        List<String> sounds = new ArrayList<>();
        for (Sound sound : Sound.values())
        {
            sounds.add(sound.name());
        }
        return sounds;
    }
}