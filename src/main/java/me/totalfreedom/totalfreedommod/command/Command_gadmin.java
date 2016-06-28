package me.totalfreedom.totalfreedommod.command;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.UUID;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.freeze.FreezeData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(
        description = "Use admin commands on someone by hash. Use mode 'list' to get a player's hash. Other modes are kick, nameban, ipban, ban, op, deop, ci, fr, smite.",
        usage = "/<command> [list | [<kick | nameban | ipban | ban | op | deop | ci | fr | smite> <targethash>] ]")
public class Command_gadmin extends FreedomCommand
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

    public String getPlayerHash(Player player)
    {
        return UUID.nameUUIDFromBytes(player.getName().toLowerCase().getBytes(StandardCharsets.UTF_8)).toString().substring(0, 4);
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        final GadminMode mode = GadminMode.findMode(args[0].toLowerCase());
        if (mode == null)
        {
            msg("Invalid mode: " + args[0], ChatColor.RED);
            return true;
        }

        final Iterator<? extends Player> it = server.getOnlinePlayers().iterator();

        if (mode == GadminMode.LIST)
        {
            msg("[ Real Name ] : [ Display Name ] - Hash:");
            while (it.hasNext())
            {
                final Player player = it.next();
                sender.sendMessage(ChatColor.GRAY + String.format("[ %s ] : [ %s ] - %s",
                        player.getName(),
                        ChatColor.stripColor(player.getDisplayName()),
                        getPlayerHash(player)));
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
            final String hash = getPlayerHash(player);

            if (hash.equalsIgnoreCase(args[1]))
            {
                target = player;
            }
        }

        if (target == null)
        {
            msg("Invalid player hash: " + args[1], ChatColor.RED);
            return true;
        }

        switch (mode)
        {
            case KICK:
            {
                FUtil.adminAction(sender.getName(), String.format("Kicking: %s.", target.getName()), false);
                target.kickPlayer("Kicked by Administrator");

                break;
            }
            case NAMEBAN:
            {
                FUtil.adminAction(sender.getName(), String.format("Banning Name: %s.", target.getName()), true);
                plugin.bm.addBan(Ban.forPlayerName(target, sender, null, null));
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
                FUtil.adminAction(sender.getName(), String.format("Banning IP: %s.", ip), true);
                plugin.bm.addBan(Ban.forPlayerIp(ip, sender, null, null));

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
                FUtil.adminAction(sender.getName(), String.format("Banning Name: %s, IP: %s.", target.getName(), ip), true);

                plugin.bm.addBan(Ban.forPlayer(target, sender));

                target.kickPlayer("IP and username banned by Administrator.");

                break;
            }
            case OP:
            {
                FUtil.adminAction(sender.getName(), String.format("Opping %s.", target.getName()), false);
                target.setOp(true);
                target.sendMessage(FreedomCommand.YOU_ARE_OP);

                break;
            }
            case DEOP:
            {
                FUtil.adminAction(sender.getName(), String.format("Deopping %s.", target.getName()), false);
                target.setOp(false);
                target.sendMessage(FreedomCommand.YOU_ARE_NOT_OP);

                break;
            }
            case CI:
            {
                target.getInventory().clear();

                break;
            }
            case FR:
            {
                FreezeData fd = plugin.pl.getPlayer(target).getFreezeData();
                fd.setFrozen(!fd.isFrozen());

                msg(target.getName() + " has been " + (fd.isFrozen() ? "frozen" : "unfrozen") + ".");
                target.sendMessage(ChatColor.AQUA + "You have been " + (fd.isFrozen() ? "frozen" : "unfrozen") + ".");

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
