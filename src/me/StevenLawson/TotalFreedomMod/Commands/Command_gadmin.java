package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import java.util.Iterator;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(
        description = "Use admin commands on someone by hash. Use mode 'list' to get a player's hash. Other modes are kick, nameban, ipban, ban, op, deop, ci, fr, smite.",
        usage = "/<command> [list | [<kick | nameban | ipban | ban | op | deop | ci | fr | smite> <targethash>] ]")
public class Command_gadmin extends TFM_Command
{
    private enum GadminMode
    {
        LIST("list"),
        KICK("kick"),
        NAMEBAN("nameban"),
        IPBAN("ipban"),
        BAN("ban"),
        OP("op"),
        DEOP("deop"),
        CI("ci"),
        FR("fr"),
        SMITE("smite");

        private final String modeName;

        private GadminMode(String command)
        {
            this.modeName = command;
        }

        public String getModeName()
        {
            return modeName;
        }

        public static GadminMode findMode(String needle)
        {
            for (final GadminMode mode : GadminMode.values())
            {
                if (needle.equalsIgnoreCase(mode.getModeName()))
                {
                    return mode;
                }
            }
            return null;
        }
    }

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        final GadminMode mode = GadminMode.findMode(args[0].toLowerCase());
        if (mode == null)
        {
            playerMsg("Invalid mode: " + args[0], ChatColor.RED);
            return true;
        }

        final Iterator<Player> it = Arrays.asList(server.getOnlinePlayers()).iterator();

        if (mode == GadminMode.LIST)
        {
            playerMsg("[ Real Name ] : [ Display Name ] - Hash:");
            while (it.hasNext())
            {
                final Player player = it.next();
                final String hash = TFM_Util.getUuid(player).toString().substring(0, 4);
                sender.sendMessage(ChatColor.GRAY + String.format("[ %s ] : [ %s ] - %s",
                        player.getName(),
                        ChatColor.stripColor(player.getDisplayName()),
                        hash
                ));
            }
            return true;
        }

        if (args.length < 2)
        {
            return false;
        }

        Player target = null;
        while (it.hasNext() && target == null)
        {
            final Player player = it.next();
            final String hash = TFM_Util.getUuid(player).toString().substring(0, 4);

            if (hash.equalsIgnoreCase(args[1]))
            {
                target = player;
            }
        }

        if (target == null)
        {
            playerMsg("Invalid player hash: " + args[1], ChatColor.RED);
            return true;
        }

        switch (mode)
        {
            case KICK:
            {
                TFM_Util.adminAction(sender.getName(), String.format("Kicking: %s.", target.getName()), false);
                target.kickPlayer("Kicked by Administrator");

                break;
            }
            case NAMEBAN:
            {
                TFM_BanManager.addUuidBan(target);

                TFM_Util.adminAction(sender.getName(), String.format("Banning Name: %s.", target.getName()), true);
                target.kickPlayer("Username banned by Administrator.");

                break;
            }
            case IPBAN:
            {
                String ip = target.getAddress().getAddress().getHostAddress();
                String[] ip_parts = ip.split("\\.");
                if (ip_parts.length == 4)
                {
                    ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
                }
                TFM_Util.adminAction(sender.getName(), String.format("Banning IP: %s.", ip), true);
                TFM_BanManager.addIpBan(target);

                target.kickPlayer("IP address banned by Administrator.");

                break;
            }
            case BAN:
            {
                String ip = target.getAddress().getAddress().getHostAddress();
                String[] ip_parts = ip.split("\\.");
                if (ip_parts.length == 4)
                {
                    ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
                }
                TFM_Util.adminAction(sender.getName(), String.format("Banning Name: %s, IP: %s.", target.getName(), ip), true);

                TFM_BanManager.addUuidBan(target);
                TFM_BanManager.addIpBan(target);

                target.kickPlayer("IP and username banned by Administrator.");

                break;
            }
            case OP:
            {
                TFM_Util.adminAction(sender.getName(), String.format("Opping %s.", target.getName()), false);
                target.setOp(false);
                target.sendMessage(TotalFreedomMod.YOU_ARE_OP);

                break;
            }
            case DEOP:
            {
                TFM_Util.adminAction(sender.getName(), String.format("Deopping %s.", target.getName()), false);
                target.setOp(false);
                target.sendMessage(TotalFreedomMod.YOU_ARE_NOT_OP);

                break;
            }
            case CI:
            {
                target.getInventory().clear();

                break;
            }
            case FR:
            {
                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(target);
                playerdata.setFrozen(!playerdata.isFrozen());

                playerMsg(target.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                target.sendMessage(ChatColor.AQUA + "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");

                break;
            }
            case SMITE:
            {
                Command_smite.smite(target);

                break;
            }
        }

        return true;
    }
}
