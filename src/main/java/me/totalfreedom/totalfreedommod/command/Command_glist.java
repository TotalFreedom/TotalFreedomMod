package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.banning.BanManager;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.player.PlayerList;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level=Rank.SUPER_ADMIN, source=SourceType.BOTH, blockHostConsole=true)
@CommandParameters(description="Bans or unbans any player, even those who are not logged in anymore.", usage="/<command> <purge | ban <username> [reason] | unban <username> | banip <ip> <reason> | unbanip <ip>>")
public class Command_glist
  extends FreedomCommand
{
  public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
  {
    if (args.length < 1) {
      return false;
    }
    if (args.length == 1)
    {
      if ("purge".equals(args[0]))
      {
        checkRank(Rank.SENIOR_ADMIN);
        ((TotalFreedomMod)this.plugin).pl.purgeAllData();
        msg("Purged playerbase.");
        
        return true;
      }
      return false;
    }
    if (args.length < 2) {
      return false;
    }
    List<String> ips = new ArrayList();
    
    Player player = getPlayer(args[1]);
    String username;
    if (player == null)
    {
      PlayerData entry = ((TotalFreedomMod)this.plugin).pl.getData(args[1]);
      if (entry == null)
      {
        msg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
        return true;
      }
      String username = entry.getUsername();
      ips.addAll(entry.getIps());
    }
    else
    {
      PlayerData entry = ((TotalFreedomMod)this.plugin).pl.getData(player);
      username = player.getName();
      ips.addAll(entry.getIps());
    }
    String reason;
    if ("ban".equals(args[0]))
    {
      reason = args.length > 2 ? StringUtils.join(args, " ", 2, args.length) : null;
      Ban ban = Ban.forPlayerName(username, sender, null, reason);
      for (String ip : ips)
      {
        ban.addIp(ip);
        ban.addIp(FUtil.getFuzzyIp(ip));
      }
      FUtil.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
      
      ((TotalFreedomMod)this.plugin).bm.addBan(ban);
      if (player != null) {
        player.kickPlayer(ban.bakeKickMessage());
      }
      return true;
    }
    if ("unban".equals(args[0]))
    {
      FUtil.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
      ((TotalFreedomMod)this.plugin).bm.removeBan(((TotalFreedomMod)this.plugin).bm.getByUsername(username));
      for (String ip : ips)
      {
        Ban ban = ((TotalFreedomMod)this.plugin).bm.getByIp(ip);
        if (ban != null) {
          ((TotalFreedomMod)this.plugin).bm.removeBan(ban);
        }
        ban = ((TotalFreedomMod)this.plugin).bm.getByIp(FUtil.getFuzzyIp(ip));
        if (ban != null) {
          ((TotalFreedomMod)this.plugin).bm.removeBan(ban);
        }
      }
      return true;
    }
    if ("banip".equals(args[0]))
    {
      String ip = args[2];
      if (ip == null) {
        msg("Please specify an IP");
      }
      String reason = args.length > 2 ? StringUtils.join(args, " ", 2, args.length) : null;
      Ban ban = Ban.forPlayerIp(ip, sender, null, reason);
      ((TotalFreedomMod)this.plugin).bm.addBan(ban);
      FUtil.adminAction(sender.getName(), "Banning IPs: " + StringUtils.join(ips, ", "), true);
      return true;
    }
    if ("unbanip".equals(args[0]))
    {
      String ip = args[2];
      if (ip == null) {
        msg("Please specify an IP");
      }
      FUtil.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
      ((TotalFreedomMod)this.plugin).bm.removeBan(((TotalFreedomMod)this.plugin).bm.getByUsername(username));
      
      Ban ban = ((TotalFreedomMod)this.plugin).bm.getByIp(ip);
      if (ban != null)
      {
        ((TotalFreedomMod)this.plugin).bm.removeBan(ban);
        ((TotalFreedomMod)this.plugin).bm.unbanIp(ip);
      }
      ban = ((TotalFreedomMod)this.plugin).bm.getByIp(FUtil.getFuzzyIp(ip));
      if (ban != null)
      {
        ((TotalFreedomMod)this.plugin).bm.removeBan(ban);
        ((TotalFreedomMod)this.plugin).bm.unbanIp(ip);
      }
      return true;
    }
    return true;
  }
}
