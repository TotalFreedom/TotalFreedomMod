package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Random;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Command_cake extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            StringBuilder output = new StringBuilder();
            Random randomGenerator = new Random();

            for (String word : TotalFreedomMod.CAKE_LYRICS.split(" "))
            {
                String color_code = Integer.toHexString(1 + randomGenerator.nextInt(14));
                output.append("§").append(color_code).append(word).append(" ");
            }

            for (Player p : Bukkit.getOnlinePlayers())
            {
                ItemStack heldItem = new ItemStack(Material.CAKE, 1);
                p.getInventory().setItem(p.getInventory().firstEmpty(), heldItem);
            }

            TFM_Util.bcastMsg(output.toString());
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
