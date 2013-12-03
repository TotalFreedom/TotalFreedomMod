package me.StevenLawson.TotalFreedomMod;

import static me.StevenLawson.TotalFreedomMod.TFM_Util.DEVELOPERS;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum TFM_PlayerRank
{
    IMPOSTOR("an " + ChatColor.YELLOW + ChatColor.UNDERLINE + "Impostor", ChatColor.YELLOW.toString() + ChatColor.UNDERLINE + "[IMP]"),
    NON_OP("a " + ChatColor.GREEN + "Non-OP", ChatColor.GREEN.toString()),
    OP("an " + ChatColor.RED + "OP", ChatColor.RED + "[OP]"),
    SUPER("a " + ChatColor.GOLD + "Super Admin", ChatColor.GOLD + "[SA]"),
    TELNET("a " + ChatColor.DARK_GREEN + "Super Telnet Admin", ChatColor.DARK_GREEN + "[STA]"),
    SENIOR("a " + ChatColor.LIGHT_PURPLE + "Senior Admin", ChatColor.LIGHT_PURPLE + "[SrA]"),
    OWNER("the " + ChatColor.BLUE + "Owner", ChatColor.BLUE + "[Owner]"),
    CONSOLE("The " + ChatColor.DARK_PURPLE + "Console", ChatColor.DARK_PURPLE + "[Console]");
    private String loginMessage;
    private String prefix;

    private TFM_PlayerRank(String loginMessage, String prefix)
    {
        this.loginMessage = loginMessage;
        this.prefix = prefix;
    }

    public static TFM_PlayerRank fromSender(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return CONSOLE;
        }

        if (TFM_SuperadminList.isSuperadminImpostor(sender))
        {
            return IMPOSTOR;
        }

        final TFM_Superadmin entry = TFM_SuperadminList.getAdminEntry((Player) sender);

        final TFM_PlayerRank rank;

        if (entry != null && entry.isActivated())
        {
            if (sender.getName().equals("markbyron"))
            {
                return OWNER;
            }

            if (entry.isSeniorAdmin())
            {
                rank = SENIOR;
            }
            else if (entry.isTelnetAdmin())
            {
                rank = TELNET;
            }
            else
            {
                rank = SUPER;
            }

            final String loginMessage = entry.getCustomLoginMessage();

            if (loginMessage != null && !loginMessage.isEmpty())
            {
                rank.setLoginMessage(ChatColor.translateAlternateColorCodes('&', loginMessage));
            }
            else
            {
                if (DEVELOPERS.contains(sender.getName()))
                {
                    rank.setLoginMessage("a " + ChatColor.DARK_PURPLE + "Developer");
                }
            }
        }
        else
        {
            if (sender.isOp())
            {
                rank = OP;
            }
            else
            {
                rank = NON_OP;
            }

            if (DEVELOPERS.contains(sender.getName()))
            {
                rank.setLoginMessage("a " + ChatColor.DARK_PURPLE + "Developer");
            }

        }

        if (DEVELOPERS.contains(sender.getName()))
        {
            rank.setPrefix(ChatColor.DARK_PURPLE + "[Dev]");
        }

        return rank;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public void setLoginMessage(String rank)
    {
        this.loginMessage = rank;
    }

    public String getLoginMessage()
    {
        return loginMessage;
    }
}
