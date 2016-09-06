package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Gives player a full set of armor.", usage = "/<command>")
public class Command_disco extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        playerSender.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
        playerSender.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
        playerSender.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
        playerSender.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
        msg("You have been given disco armor!", ChatColor.GREEN);
        return true;
    }
}
