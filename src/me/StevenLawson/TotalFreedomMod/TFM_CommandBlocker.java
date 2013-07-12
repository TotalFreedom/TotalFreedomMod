package me.StevenLawson.TotalFreedomMod;

import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandLoader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TFM_CommandBlocker
{
    public static boolean isCommandBlocked(String usedcommand, CommandSender sender)
    {

        String name = sender.getName();
        usedcommand = usedcommand.toLowerCase().trim();

        for (String blocked_command : TotalFreedomMod.blockedCommands)
        {
            String[] parts = blocked_command.split(":");
            if (parts.length < 3 || parts.length > 4)
            {
                continue;
            }

            if (!(usedcommand + " ").startsWith(parts[2] + " "))
            {

                CommandMap commandMap = TFM_CommandLoader.commandMap;
                if (commandMap == null)
                {
                    continue;
                }

                Command command = commandMap.getCommand(parts[2].replaceAll("/", ""));
                if (command == null)
                {
                    continue;
                }

                boolean block = false;
                for (String alias : command.getAliases())
                {
                    if (usedcommand.replaceAll("/", "").startsWith(alias))
                    {
                        block = true;
                        break;
                    }
                }

                if (!block)
                {
                    continue;
                }
            }


            if (SenderRank.hasPermissions(sender, parts[0]))
            {
                continue;
            }

            // Past this line indicates that the command is blocked.

            // Optional: Send a message
            if (parts.length == 4)
            {
                if ("_".equals(parts[3]))
                {
                    sender.sendMessage(ChatColor.GRAY + "That command is blocked.");
                }
                else
                {
                    sender.sendMessage(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', parts[3]));
                }
            }

            // Action
            if ("b".equals(parts[1]))
            {
                return true;
            }
            else if ("a".equals(parts[1]))
            {
                if (SenderRank.getSenderRank(sender).rank < 2) // Only auto-eject Ops and non-ops
                {
                    TFM_Util.autoEject((Player) sender, "You used a prohibited command: " + usedcommand);
                    TFM_Util.bcastMsg(name + " was automatically kicked for using harmful commands.", ChatColor.RED);
                }
                return true;
            }
            else if ("u".equals(parts[1]))
            {
                sender.sendMessage("Unknown command. Type \"help\" for help.");
                return true;
            }
            return false;
        }

        return false;
    }

    public enum SenderRank
    {
        ANYONE("a", 0),
        OP("o", 1),
        SUPER("s", 2),
        TELNET("t", 3),
        SENIOR("c", 4),
        NOBODY("n", 5);
        private String letter = "n";
        private int rank = 5;

        SenderRank(String letter, int rank)
        {
            this.letter = letter;
            this.rank = rank;
        }

        public static boolean hasPermissions(CommandSender sender, String letter)
        {
            return (getSenderRank(sender).rank >= getSenderRankByLetter(letter).rank);
        }

        public static SenderRank getSenderRank(CommandSender sender)
        {
            if (!(sender instanceof Player))
            {
                if (TFM_SuperadminList.isSeniorAdmin(sender))
                {
                    return SenderRank.SENIOR;
                }
                else
                {
                    return SenderRank.TELNET;
                }
            }

            if (TFM_SuperadminList.isUserSuperadmin(sender))
            {
                return SenderRank.SUPER;
            }

            if (sender.isOp())
            {
                return SenderRank.OP;
            }

            return SenderRank.ANYONE;
        }

        public static SenderRank getSenderRankByLetter(String letter)
        {
            for (SenderRank rank : SenderRank.values())
            {
                if (letter.equals(rank.letter))
                {
                    return rank;
                }
            }
            return SenderRank.NOBODY;
        }
    }
}
