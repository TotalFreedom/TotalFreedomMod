package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, block_web_console = false, ignore_permissions = false)
public class Command_ro extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if(args.length < 1 || args.length > 3) {
            return false;
        }

        int radius = 50;
        Player target_player = null;
        Material target_block = Material.matchMaterial(args[0]);

        if (target_block == null)
        {
            TFM_Util.playerMsg(sender, "Invalid block!");
            return true;
        }

        if(args.length >= 2)
        {
            try
            {
                radius = Integer.parseInt(args[1]);
            }
            catch(NumberFormatException nfex)
            {
                TFM_Util.playerMsg(sender, nfex.getMessage());
                return true;
            }
            if(radius > 3000)
            {
                TFM_Util.playerMsg(sender, "What the hell are you trying to do, you stupid idiot!", ChatColor.RED);
                return true;
            }
        }

        if(args.length == 3)
        {
            try
            {
               target_player = getPlayer(args[2]);
            }
            catch (CantFindPlayerException ex)
            {
                sender.sendMessage(ex.getMessage());
                return true;
            }
        }


        if(target_player == null)
        {
            for(Player p : server.getOnlinePlayers())
            {
                boolean is_Op = p.isOp();
                p.setOp(true);
                server.dispatchCommand(p, "/removenear " + target_block.getId() + " " + radius);
                p.setOp(is_Op);
            }
        }
        else
        {
            boolean is_Op = target_player.isOp();
            target_player.setOp(true);
            server.dispatchCommand(target_player, "/removenear " + target_block.getId() + " " + radius);
            target_player.setOp(is_Op);
        }
        return true;
    }
}