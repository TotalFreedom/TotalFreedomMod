package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Manage notes for a player", usage = "/<command> <name> <list | add <note> | remove <id> | clear>")
public class Command_notes extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 2)
        {
            return false;
        }

        VPlayer vPlayer;

        final Player player = getPlayer(args[0]);
        if (player == null)
        {
            final PlayerData entry = plugin.pl.getData(args[0]);

            if (entry == null)
            {
                msg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                return true;
            }

            vPlayer = plugin.pv.getVerificationPlayer(entry.getUsername());
        }
        else
        {
            vPlayer = plugin.pv.getVerificationPlayer(player);
        }

        if (args[1].equals("list"))
        {
            final StringBuilder noteList = new StringBuilder();
            noteList.append(ChatColor.GREEN + "Player notes for " + vPlayer.getName() + ":");
            int id = 1;
            for (Map<?, ?> noteMap : vPlayer.getNotes())
            {
                String admin = String.valueOf(noteMap.keySet().toArray()[0]);
                String note = String.valueOf(noteMap.get(admin));
                String noteLine = id + ". " + admin + ": " + note;
                noteList.append("\n" + ChatColor.GOLD + noteLine);
                id++;
            }
            msg(noteList.toString());
            return true;
        }
        else if (args[1].equals("add"))
        {
            if (args.length < 3)
            {
                return false;
            }
            String note = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ");
            vPlayer.addNote(sender.getName(), note);
            plugin.pv.saveVerificationData(vPlayer);
            msg("Note added.", ChatColor.GREEN);
            return true;
        }
        else if (args[1].equals("remove"))
        {
            if (args.length < 3)
            {
                return false;
            }
            int id;
            try
            {
                id = Integer.valueOf(args[2]);
            }
            catch (NumberFormatException e)
            {
                msg("Invalid number: " + args[2], ChatColor.RED);
                return true;
            }
            id--;
            if (vPlayer.removeNote(id))
            {
                plugin.pv.saveVerificationData(vPlayer);
                msg("Note removed.");
            }
            else
            {
                msg("No note with the ID of " + args[2] + "exists.", ChatColor.RED);
            }
            return true;
        }
        else if (args[1].equals("clear"))
        {
            int count = vPlayer.getNotes().size();
            vPlayer.clearNotes();
            plugin.pv.saveVerificationData(vPlayer);
            msg("Cleared " + count + " notes.", ChatColor.GREEN);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            return FUtil.getPlayerList();
        }
        else if (args.length == 2)
        {
            return Arrays.asList("list", "add", "remove", "clear");
        }
        else if (args.length > 2 && (args[1].equals("add")))
        {
            return FUtil.getPlayerList();
        }
        return Collections.emptyList();
    }
}
