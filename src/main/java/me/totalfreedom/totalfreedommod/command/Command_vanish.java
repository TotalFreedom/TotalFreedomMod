

package me.totalfreedom.totalfreedommod.command;

import java.util.Iterator;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import org.bukkit.potion.PotionEffect;
import org.apache.commons.lang.StringUtils;
import org.bukkit.potion.PotionEffectType;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import me.totalfreedom.totalfreedommod.rank.Rank;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME, blockHostConsole = true)
@CommandParameters(description = "Vanish/unvanish yourself.", usage = "/<command>", aliases = "v")
public class Command_vanish extends FreedomCommand
{
    public static ArrayList<Player> vanished;
    
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        final Displayable display = ((TotalFreedomMod)this.plugin).rm.getDisplay((CommandSender)playerSender);
        String loginMsg = display.getColoredLoginMessage();
        final String displayName = display.getColor() + playerSender.getName();
        final Admin admin = ((TotalFreedomMod)this.plugin).al.getAdmin(playerSender);
        if (Command_vanish.vanished.contains(playerSender)) {
            this.msg(ChatColor.GOLD + "You have been unvanished.");
            if (admin.hasLoginMessage()) {
                loginMsg = FUtil.colorize(admin.getLoginMessage());
            }
            FUtil.bcastMsg(ChatColor.AQUA + playerSender.getName() + " is " + loginMsg);
            FUtil.bcastMsg(playerSender.getName() + " joined the game", ChatColor.YELLOW);
            ((TotalFreedomMod)this.plugin).pl.getPlayer(playerSender).setTag(display.getColoredTag());
            FLog.info(playerSender.getName() + " is no longer vanished.");
            for (final Player player : this.server.getOnlinePlayers()) {
                player.showPlayer(playerSender);
            }
            ((TotalFreedomMod)this.plugin).esb.setVanished(playerSender.getName(), false);
            playerSender.removePotionEffect(PotionEffectType.INVISIBILITY);
            playerSender.setPlayerListName(StringUtils.substring(displayName, 0, 16));
            Command_vanish.vanished.remove(playerSender);
            return true;
        }
        if (!Command_vanish.vanished.contains(playerSender)) {
            this.msg(ChatColor.GOLD + "You have been vanished.");
            FUtil.bcastMsg(playerSender.getName() + " left the game", ChatColor.YELLOW);
            FLog.info(playerSender.getName() + " is now vanished.");
            for (final Player player : this.server.getOnlinePlayers()) {
                player.hidePlayer(playerSender);
            }
            ((TotalFreedomMod)this.plugin).esb.setVanished(playerSender.getName(), true);
            playerSender.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 1000000, true, false));
            Command_vanish.vanished.add(playerSender);
            return true;
        }
        return true;
    }
    
    static {
        Command_vanish.vanished = new ArrayList<Player>();
    }
}
