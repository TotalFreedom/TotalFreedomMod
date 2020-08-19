package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Vanish/unvanish yourself.", usage = "/<command> [-s[ilent]]", aliases = "v")
public class Command_vanish extends FreedomCommand
{

    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        Displayable display = plugin.rm.getDisplay(playerSender);
        String displayName = display.getColor() + playerSender.getName();
        String tag = display.getColoredTag();
        boolean silent = false;
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("-s") || args[0].equalsIgnoreCase("-silent"))
            {
                silent = true;
            }
        }

        if (plugin.sl.isVanished(playerSender.getName()))
        {
            if (silent)
            {
                msg(ChatColor.GOLD + "Silently unvanished.");
            }
            else
            {
                msg("You have unvanished.", ChatColor.GOLD);
                FUtil.bcastMsg(plugin.rm.craftLoginMessage(playerSender, null));
                FUtil.bcastMsg(playerSender.getName() + " joined the game.", ChatColor.YELLOW);
                plugin.dc.messageChatChannel("**" + playerSender.getName() + " joined the server" + "**");
            }

            PlayerData playerData = plugin.pl.getData(playerSender);
            if (playerData.getTag() != null)
            {
                tag = FUtil.colorize(playerData.getTag());
            }

            plugin.pl.getData(playerSender).setTag(tag);
            FLog.info(playerSender.getName() + " is no longer vanished.");
            plugin.sl.messageAllStaff(ChatColor.YELLOW + sender.getName() + " has unvanished and is now visible to everyone.");

            for (Player player : server.getOnlinePlayers())
            {
                if (!plugin.sl.isStaff(player))
                {
                    player.showPlayer(plugin, playerSender);
                }
            }
            plugin.esb.setVanished(playerSender.getName(), false);
            playerSender.setPlayerListName(StringUtils.substring(displayName, 0, 16));
            plugin.sl.vanished.remove(playerSender.getName());
        }
        else
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (plugin.sl.isVanished(playerSender.getName()))
                    {
                        playerSender.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "You are hidden from other players."));
                    }
                }
            }.runTaskTimer(plugin, 0L, 4L);

            if (silent)
            {
                msg("Silently vanished.", ChatColor.GOLD);
            }
            else
            {
                msg ("You have vanished.", ChatColor.GOLD);
                FUtil.bcastMsg(playerSender.getName() + " left the game.", ChatColor.YELLOW);
                plugin.dc.messageChatChannel("**" + playerSender.getName() + " left the server" + "**");
            }

            FLog.info(playerSender.getName() + " is now vanished.");
            plugin.sl.messageAllStaff(ChatColor.YELLOW + sender.getName() + " has vanished and is now only visible to staff members.");

            for (Player player : server.getOnlinePlayers())
            {
                if (!plugin.sl.isStaff(player))
                {
                    player.hidePlayer(plugin, playerSender);
                }
            }

            plugin.esb.setVanished(playerSender.getName(), true);
            plugin.sl.vanished.add(playerSender.getName());
        }
        return true;
    }
}