package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME, ignore_permissions = false)
public class Command_mp44 extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!TotalFreedomMod.mp44Enabled)
        {
            sender.sendMessage(ChatColor.GREEN + "The mp44 is currently disabled.");
            return true;
        }

        if (args.length == 0)
        {
            return false;
        }

        TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(sender_p);

        if (args[0].equalsIgnoreCase("draw"))
        {
            playerdata.armMP44();

            sender.sendMessage(ChatColor.GREEN + "mp44 is ARMED! Left click with gunpowder to start firing, left click again to quit.");
            sender.sendMessage(ChatColor.GREEN + "Type /mp44 sling to disable.  -by Madgeek1450");

            sender_p.setItemInHand(new ItemStack(Material.SULPHUR, 1));
        }
        else
        {
            playerdata.disarmMP44();

            sender.sendMessage(ChatColor.GREEN + "mp44 Disarmed.");
        }

        return true;
    }
}
