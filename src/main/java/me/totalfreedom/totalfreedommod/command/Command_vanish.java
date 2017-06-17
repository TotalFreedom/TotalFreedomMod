package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Vanish yourself.", usage = "/<command>")
public class Command_vanish extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        final Displayable display = plugin.rm.getDisplay(playerSender);
        String loginMsg = display.getColoredLoginMessage();
        String displayName = display.getColor() + playerSender.getName();
        Admin admin = getAdmin(playerSender);
        
        final FPlayer fPlayer = plugin.pl.getPlayer(playerSender);
        if (fPlayer.isVanish())
        {
            msg(ChatColor.GOLD + "You have been unvanished.");
            if (admin.hasLoginMessage())
            {
                loginMsg = FUtil.colorize(admin.getLoginMessage());
            }
            FUtil.bcastMsg(ChatColor.AQUA + playerSender.getName() + " is " + loginMsg);

            FUtil.bcastMsg(playerSender.getName() + " joined the game", ChatColor.YELLOW);
            plugin.pl.getPlayer(playerSender).setTag(display.getColoredTag());
            FLog.info(playerSender.getName() + " is no longer vanished.");
            for (Player player : server.getOnlinePlayers())
            {
                player.showPlayer(playerSender);
            }
            plugin.esb.setVanished(playerSender.getName(), false);
            playerSender.setPlayerListName(StringUtils.substring(displayName, 0, 16));
            fPlayer.setVanish(false);

            return true;
        }
        else if (!fPlayer.isVanish())
        {
            msg(ChatColor.GOLD + "You have been vanished.");
            FUtil.bcastMsg(playerSender.getName() + " left the game", ChatColor.YELLOW);
            FLog.info(playerSender.getName() + " is now vanished.");
            for (Player player : server.getOnlinePlayers())
            {
                player.hidePlayer(playerSender);
            }
            plugin.esb.setVanished(playerSender.getName(), true);
            fPlayer.setVanish(true);
            return true;
        }

        return true;
    }
}
