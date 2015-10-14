package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.BOTH)
@CommandParameters(description = "Somebody talking about suicide?   Send them an I love you message <3", usage = "/<command> [playername]")
public class Command_shelp extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            playerMsg(TFM_Command.PLAYER_NOT_FOUND);
            return true;
        }

        shelp(player);

        return true;
    }

    public static void shelp(final Player player)
    {
        TFM_Util.playerMsg(player, "                ,-\"\"-,-\"       \"-,-\"\"-,", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "               /,-' , .-'-.7.-'-. , '-,\\", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "               \\(    /  _     _  \\    )/", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "                '-,  { (0)   (0) }  ,-'", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "                 /    >  .---.  <    \\", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "                |/ .-'   \\___/   '-. \\|", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "                {, /  ,_       _,  \\ ,}", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "                \\ {,    \\     /    ,} /", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "                 ',\\.    '---'    ./,'", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "             _.-\"\"\"\"\"\"-._     _.-\"\"\"\"\"\"-._", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "           .'            `._.`            '.", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "         _/_               _                \\", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "      .'`   `\\            | |                \\", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "     /        |           | |                 ;", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "     |        /           |_|                 |", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "     \\  ;'---'    _    ___  _  _  ___         ;", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "      '. ;       | |  /   \\| || ||  _|     _ ;", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "        `-\\      | |_ | | || |/ /|  _|   .' `,", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "           `\\    |___|\\___/ \\__/ |___|  |     \\", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "             \\            _ _           \\     |", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "         jgs  `\\         | | |         /`   _/", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "    ,-\"\"-.    .'`\\       | | |       /`-,-'` .-\"\"-,", ChatColor.WHITE);
		TFM_Util.playerMsg(player, "   /      `\\.'    `\\      \\___/     /`    './`      \\", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "  ;  .--.   \\       '\\           /'       /   .--.  ;", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "  | (    \\   |,       '\\       /'        |   /    ) |", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "   \\ ;    }             ;\\   /;         `   {    ; /", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "    `;\\   \\         _.-'  \\ /  `-._         /   /;`", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "      \\ \\__.'   _.-'       Y       `-._    '.__//", ChatColor.WHITE);
        TFM_Util.playerMsg(player, "       '.___,.-'                       `-.,___.'", ChatColor.WHITE);
        TFM_Util.playerMsg(player, player.getName() + ", We love you, please, call for help!", ChatColor.AQUA);
        TFM_Util.playerMsg(player, "1-800-273-TALK (8255) | +44 (0) 8457 90 90 90 ", ChatColor.AQUA);
        player.setGameMode(GameMode.CREATIVE);
    }
}
