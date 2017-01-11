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
        playerMsg(ChatColor.YELLOW + "Here is a List of What You are not allowed to do!");
        playerMsg(ChatColor.DARK_RED + "Dark Red Means It Will result in a Perm Ban!!!");
        playerMsg(ChatColor.DARK_RED + "1. Do not try and Crash the Server.");
        playerMsg(ChatColor.DARK_RED + "2. Spambot is not allowed in TotalFreedom.");
        playerMsg(ChatColor.DARK_RED + "3. Serial griefer or troll.");
        playerMsg(ChatColor.DARK_RED + "4. Logging in as the owner, admin, or known player OP more than once");
        playerMsg(ChatColor.DARK_RED + "5. Using an exploit that crashes individual clients");
        playerMsg(ChatColor.DARK_RED + "6. Filing a false griefer report on the forum in order to get innocent players banned.");
        playerMsg(ChatColor.DARK_RED + "7. Confirmed use of brush sphere to grief");
        playerMsg(ChatColor.RED + "Light Red Means 1 day Ban");
        playerMsg(ChatColor.RED + "1. World edits greater than 100000 blocks without obtaining notice / permission or spamming small world edits that clearly have no building purpose or are purely for trolling.");        playerMsg(ChatColor.RED + "2. Damaging other players builds and failing to repair ");
        playerMsg(ChatColor.RED + "2. Damaging other players builds and failing to repair.");
        playerMsg(ChatColor.RED + "3. Attempting to damage the spawn and failing to repair, prevent other players from spawning, and/or repeated spawn killing.");
        playerMsg(ChatColor.RED + "4. Intentionally and falsely accusing other players of banning offense while on the server.");
        playerMsg(ChatColor.RED + "5. Posting links to malware or phishing sites.");
        playerMsg(ChatColor.RED + "6. Using invisibility to commit griefing offenses");
        playerMsg(ChatColor.RED + "7. Repeated offenses under Section 3, esp when player fails to heed admin warnings.");
        playerMsg(ChatColor.RED + "8. Self-harm or suicide threats.");
        playerMsg(ChatColor.BLUE + "Blue Means an administrative control action);
        playerMsg(ChatColor.BLUE + "1. Personal juvenile insults of other players or admins and failure to apologize.");
        playerMsg(ChatColor.BLUE + "2. Racist or sexist attacks or harassment.");
        playerMsg(ChatColor.BLUE + "3. Invading privacy of other players when asked not to. ");
        playerMsg(ChatColor.BLUE + "4. Posting links to scam, porn, or other sites that are designed to troll or engage in criminal activity. ");
        playerMsg(ChatColor.BLUE + "5. Creating or pasting builds that are designed to troll (e.g. Nazi symbols)");
        playerMsg(ChatColor.BLUE + "6. Changing other players nicks.");
        playerMsg(ChatColor.BLUE + "7. Attempting to interfere with an admin in the performance of their duties.");
        playerMsg(ChatColor.BLUE + "8. Teleporting (TPing) other players without their permission.");
        playerMsg(ChatColor.BLUE + "9. Player uses a forbidden block the server will alert it");
        playerMsg(ChatColor.BLUE + "10. Non-admins attempting to deop or ban other players.");
        playerMsg(ChatColor.BLUE + "11. Chat spamming to the point of being a nuisance to other players. ");
        playerMsg(ChatColor.BLUE + "12. Clearing or spamming other players inventory.");
        playerMsg(ChatColor.BLUE + "13. Using potions to grief other players.");
        playerMsg(ChatColor.BLUE + "14. hanging the world edit limit without asking permission. ");
        playerMsg(ChatColor.GREEN + "Green Means What You can do :)");
        playerMsg(ChatColor.GREEN + "1. Players can OP other players.");
        playerMsg(ChatColor.GREEN + "2. Players can use client mods, hacks, or cheats, but not harmful ones like nucker");
        playerMsg(ChatColor.GREEN + "3. Players can login with a non-premium account provided they’re not posing as other regular players, admins, or owner.");
        playerMsg(ChatColor.GREEN + "4. Players can use /socialspy and its not considered a violation of privacy.");
        playerMsg(ChatColor.GREEN + "5. Players can use invisibility although admins may sometimes remove it in mass (inc. /invis smite) for security purposes.");
        playerMsg(ChatColor.GREEN + "6. Players can pose as famous players such as Notch");
        playerMsg(ChatColor.GREEN + "7. Players or admins can speak in languages besides English as long as it’s in private (/msg) or in public for a a short term duration");
        playerMsg(ChatColor.GREEN + "8. Players or admins can advertise other servers (or a legitimate service) provided it’s done once in any given 10 minute period.");
        playerMsg(ChatColor.GRAY + "Visit the TotalFreedom Forum for more info");
            
            return true;
    }
}
