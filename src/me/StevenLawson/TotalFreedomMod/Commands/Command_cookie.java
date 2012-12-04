package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Random;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = true)
public class Command_cookie extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        StringBuilder output = new StringBuilder();
        Random randomGenerator = new Random();

        for (String word : TotalFreedomMod.CAKE_LYRICS.replaceAll("cake", "cookies").split(" "))
        {
            String color_code = Integer.toHexString(1 + randomGenerator.nextInt(14));
            output.append("§").append(color_code).append(word).append(" ");
        }

        for (Player p : server.getOnlinePlayers())
        {
            ItemStack heldItem = new ItemStack(Material.COOKIE, 1);
            p.getInventory().setItem(p.getInventory().firstEmpty(), heldItem);
        }

        TFM_Util.bcastMsg(output.toString());
        return true;
    }
}
