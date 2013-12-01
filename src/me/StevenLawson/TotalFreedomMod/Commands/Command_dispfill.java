package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Fill nearby dispensers with a set of items of your choice.", usage = "/<command> <radius> <comma,separated,items>")
public class Command_dispfill extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 2)
        {
            int radius;

            try
            {
                radius = Math.max(5, Math.min(25, Integer.parseInt(args[0])));
            }
            catch (NumberFormatException ex)
            {
                sender.sendMessage("Invalid radius.");
                return true;
            }

            String[] items_raw = StringUtils.split(args[1], ",");
            List<ItemStack> items = new ArrayList<ItemStack>();
            for (String search_item : items_raw)
            {
                ItemStack is = null;

                is = new ItemStack(Material.matchMaterial(search_item), 64);

                if (is == null)
                {
                    try
                    {
                        is = new ItemStack(Integer.parseInt(search_item), 64);
                    }
                    catch (NumberFormatException ex)
                    {
                    }
                }

                if (is != null)
                {
                    items.add(is);
                }
                else
                {
                    sender.sendMessage("Skipping invalid item: " + search_item);
                }
            }
            ItemStack[] items_array = items.toArray(new ItemStack[items.size()]);

            int affected = 0;
            Location center_location = sender_p.getLocation();
            Block center_block = center_location.getBlock();
            for (int x_offset = -radius; x_offset <= radius; x_offset++)
            {
                for (int y_offset = -radius; y_offset <= radius; y_offset++)
                {
                    for (int z_offset = -radius; z_offset <= radius; z_offset++)
                    {
                        Block targetBlock = center_block.getRelative(x_offset, y_offset, z_offset);
                        if (targetBlock.getLocation().distanceSquared(center_location) < (radius * radius))
                        {
                            if (targetBlock.getType().equals(Material.DISPENSER))
                            {
                                sender.sendMessage("Filling dispenser @ " + TFM_Util.formatLocation(targetBlock.getLocation()));
                                setDispenserContents(targetBlock, items_array);
                                affected++;
                            }
                        }
                    }
                }
            }

            sender.sendMessage("Done. " + affected + " dispenser(s) filled.");
        }
        else
        {
            return false;
        }

        return true;
    }

    private static void setDispenserContents(Block targetBlock, ItemStack[] items)
    {
        Dispenser dispenser = (Dispenser) targetBlock.getState();
        Inventory disp_inv = dispenser.getInventory();
        disp_inv.clear();
        disp_inv.addItem(items);
    }
}
