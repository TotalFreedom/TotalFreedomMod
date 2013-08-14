package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Random;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "For the people that are still alive.", usage = "/<command>")
public class Command_cake extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        StringBuilder output = new StringBuilder();
        Random randomGenerator = new Random();

        String[] words = TotalFreedomMod.CAKE_LYRICS.split(" ");
        for (String word : words)
        {
            String color_code = Integer.toHexString(1 + randomGenerator.nextInt(14));
            output.append(ChatColor.COLOR_CHAR).append(color_code).append(word).append(" ");
        }

        for (Player player : server.getOnlinePlayers())
        {
            ItemStack heldItem = new ItemStack(Material.CAKE, 1);
            player.getInventory().setItem(player.getInventory().firstEmpty(), heldItem);
            player.awardAchievement(Achievement.MINE_WOOD);
            player.awardAchievement(Achievement.BUILD_WORKBENCH);
            player.awardAchievement(Achievement.BUILD_HOE);
            player.awardAchievement(Achievement.BAKE_CAKE);
        }

        TFM_Util.bcastMsg(output.toString());
        return true;
    }
}
