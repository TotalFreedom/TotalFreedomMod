package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import me.totalfreedom.totalfreedommod.*;
import me.totalfreedom.totalfreedommod.util.*;
import org.apache.commons.lang3.*;
import org.bukkit.*;
import me.totalfreedom.totalfreedommod.player.*;
import java.util.*;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Sets yourself a prefix", usage = "/<command> <set <tag..> | off | clear <player> | clearall>")
public class Command_tag extends FreedomCommand
{
    public static final List<String> FORBIDDEN_WORDS;
    
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        if (args.length == 1) {
            if ("list".equalsIgnoreCase(args[0])) {
                this.msg("Tags for all online players:");
                for (final Player player : this.server.getOnlinePlayers()) {
                    final FPlayer playerdata = ((TotalFreedomMod)this.plugin).pl.getPlayer(player);
                    if (playerdata.getTag() != null) {
                        this.msg(player.getName() + ": " + playerdata.getTag());
                    }
                }
                return true;
            }
            if ("clearall".equalsIgnoreCase(args[0])) {
                if (!((TotalFreedomMod)this.plugin).al.isAdmin(sender)) {
                    this.noPerms();
                    return true;
                }
                FUtil.adminAction(sender.getName(), "Removing all tags", false);
                int count = 0;
                for (final Player player2 : this.server.getOnlinePlayers()) {
                    final FPlayer playerdata2 = ((TotalFreedomMod)this.plugin).pl.getPlayer(player2);
                    if (playerdata2.getTag() != null) {
                        ++count;
                        playerdata2.setTag(null);
                    }
                }
                this.msg(count + " tag(s) removed.");
                return true;
            }
            else {
                if ("off".equalsIgnoreCase(args[0])) {
                    if (senderIsConsole) {
                        this.msg("\"/tag off\" can't be used from the console. Use \"/tag clear <player>\" or \"/tag clearall\" instead.");
                    }
                    else {
                        ((TotalFreedomMod)this.plugin).pl.getPlayer(playerSender).setTag(null);
                        this.msg("Your tag has been removed.");
                    }
                    return true;
                }
                return false;
            }
        }
        else {
            if (args.length < 2) {
                return false;
            }
            if ("clear".equalsIgnoreCase(args[0])) {
                if (!((TotalFreedomMod)this.plugin).al.isAdmin(sender)) {
                    this.noPerms();
                    return true;
                }
                final Player player3 = this.getPlayer(args[1]);
                if (player3 == null) {
                    this.msg(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }
                ((TotalFreedomMod)this.plugin).pl.getPlayer(player3).setTag(null);
                this.msg("Removed " + player3.getName() + "'s tag.");
                return true;
            }
            else {
                if ("set".equalsIgnoreCase(args[0])) {
                    final String inputTag = StringUtils.join((Object[])args, " ", 1, args.length);
                    final String outputTag = FUtil.colorize(StringUtils.replaceEachRepeatedly(StringUtils.strip(inputTag), new String[] { "§", "&k" }, new String[] { "", "" })) + ChatColor.RESET;
                    if (!((TotalFreedomMod)this.plugin).al.isAdmin(sender)) {
                        final String rawTag = ChatColor.stripColor(outputTag).toLowerCase();
                        if (rawTag.length() > 20) {
                            this.msg("That tag is too long (Max is 20 characters).");
                            return true;
                        }
                        for (final String word : Command_tag.FORBIDDEN_WORDS) {
                            if (rawTag.contains(word)) {
                                this.msg("That tag contains a forbidden word.");
                                return true;
                            }
                        }
                    }
                    ((TotalFreedomMod)this.plugin).pl.getPlayer(playerSender).setTag(outputTag);
                    this.msg("Tag set to '" + outputTag + "'.");
                    return true;
                }
                return false;
            }
        }
    }
    
    static {
        FORBIDDEN_WORDS = Arrays.asList("admin", "owner", "moderator", "developer", "console","MOD", "CONSOLE", "SA", "STA", "SrA");
    }
}
