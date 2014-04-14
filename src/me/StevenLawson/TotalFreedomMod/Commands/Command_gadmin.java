package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Ban;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import me.StevenLawson.TotalFreedomMod.TFM_ServerInterface;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(
        description = "Use admin commands on someone by hash. Use mode 'list' to get a player's hash. Other modes are kick, nameban, ipban, ban, op, deop, ci",
        usage = "/<command> [list | [<kick | nameban | ipban | ban | op | deop | ci | fr> <targethash>] ]")
public class Command_gadmin extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        String mode = args[0].toLowerCase();

        if (mode.equals("list"))
        {
            playerMsg("[ Real Name ] : [ Display Name ] - Hash:");
        }

        for (Player player : server.getOnlinePlayers())
        {
            String hash = player.getUniqueId().toString().substring(0, 4);
            if (mode.equals("list"))
            {
                sender.sendMessage(ChatColor.GRAY + String.format("[ %s ] : [ %s ] - %s",
                        player.getName(),
                        ChatColor.stripColor(player.getDisplayName()),
                        hash));
            }
            else if (hash.equalsIgnoreCase(args[1]))
            {
                if (mode.equals("kick"))
                {
                    TFM_Util.adminAction(sender.getName(), String.format("Kicking: %s.", player.getName()), false);
                    player.kickPlayer("Kicked by Administrator");
                }
                else if (mode.equals("nameban"))
                {
                    TFM_BanManager.getInstance().addUuidBan(new TFM_Ban(player.getUniqueId(), player.getName()));
                    TFM_Util.adminAction(sender.getName(), String.format("Banning Name: %s.", player.getName()), true);
                    player.kickPlayer("Username banned by Administrator.");
                }
                else if (mode.equals("ipban"))
                {
                    String ip = player.getAddress().getAddress().getHostAddress();
                    String[] ip_parts = ip.split("\\.");
                    if (ip_parts.length == 4)
                    {
                        ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
                    }
                    TFM_Util.adminAction(sender.getName(), String.format("Banning IP: %s.", player.getName(), ip), true);
                    TFM_BanManager.getInstance().addIpBan(new TFM_Ban(ip, player.getName()));
                    player.kickPlayer("IP address banned by Administrator.");
                }
                else if (mode.equals("ban"))
                {
                    String ip = player.getAddress().getAddress().getHostAddress();
                    String[] ip_parts = ip.split("\\.");
                    if (ip_parts.length == 4)
                    {
                        ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
                    }
                    TFM_Util.adminAction(sender.getName(), String.format("Banning Name: %s, IP: %s.", player.getName(), ip), true);
                    TFM_BanManager.getInstance().addUuidBan(new TFM_Ban(player.getUniqueId(), player.getName()));
                    TFM_BanManager.getInstance().addIpBan(new TFM_Ban(ip, player.getName()));
                    player.kickPlayer("IP and username banned by Administrator.");
                }
                else if (mode.equals("op"))
                {
                    TFM_Util.adminAction(sender.getName(), String.format("Opping %s.", player.getName()), false);
                    player.setOp(false);
                    player.sendMessage(TotalFreedomMod.YOU_ARE_OP);
                }
                else if (mode.equals("deop"))
                {
                    TFM_Util.adminAction(sender.getName(), String.format("Deopping %s.", player.getName()), false);
                    player.setOp(false);
                    player.sendMessage(TotalFreedomMod.YOU_ARE_NOT_OP);
                }
                else if (mode.equals("ci"))
                {
                    player.getInventory().clear();
                }
                else if (mode.equals("fr"))
                {
                    TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                    playerdata.setFrozen(!playerdata.isFrozen());

                    playerMsg(player.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                    player.sendMessage(ChatColor.AQUA + "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                }

                return true;
            }
        }

        if (!mode.equals("list"))
        {
            playerMsg("Invalid hash.", ChatColor.RED);
        }

        return true;
    }
}
