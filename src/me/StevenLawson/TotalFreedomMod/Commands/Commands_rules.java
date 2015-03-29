package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Rules for players!", usage = "/<command>")
public class Command_rules extends TFM_Command
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        playerMsg(ChatColor.GOLD + "Here are the Rules of TotalFreedom, Make sure you Follow and Read all of them! :)");
        playerMsg(ChatColor.YELLOW + "Here is a List of What You are not allowed to do);
        playerMsg(ChatColor.RED + "1. You may not grief.);
        playerMsg(ChatColor.RED + "2. You may not Spawn Kill.);
        playerMsg(ChatColor.RED + "3. You may not be invisable or in creative, god during pvping);
        playerMsg(ChatColor.RED + "4. Yoy may not Imposter Admins or Ops. );
        playerMsg(ChatColor.GOLD + "Green is what you can do );
        playerMsg(ChatColor.GREEN + "1. Players can OP other players.");
        playerMsg(ChatColor.GREEN + "2. Players can use client mods, hacks, or cheats, but not harmful ones like nucker");
        playerMsg(ChatColor.GREEN + "3. Players can login with a non-premium account provided they’re not posing as other regular players, admins, or owner.");
        playerMsg(ChatColor.GREEN + "4. Players can use /socialspy and its not considered a violation of privacy.");
        playerMsg(ChatColor.GREEN + "5. Players can use invisibility although admins may sometimes remove it in mass (inc. /invis smite) for security purposes.");
        playerMsg(ChatColor.GREEN + "6. Players can pose as famous players such as Notch");
        playerMsg(ChatColor.GREEN + "7. Players or admins can speak in languages besides English as long as it’s in private (/msg) or in public for a a short term duration");
        playerMsg(ChatColor.GREEN + "8. Players or admins can advertise other servers (or a legitimate service) provided it’s done once in any given 10 minute period.");
        playerMsg(ChatColor.GRAY + "here are some funny jokes");
        playerMsg(ChatColor.GRAY + "7x3 = 21");
        playerMsg(ChatColor.GRAY + "If apple made a car would it have Windows?"
        playerMsg(ChatColor.GREEN + "If you want somehthing to be added mail it to Alex33856Xd@Gmail.com or in game mail Alex33856);
        playerMsg(ChatColor.GRAY + "Visit the TotalFreedom Forum for more info");
            
            return true;
    }
}
