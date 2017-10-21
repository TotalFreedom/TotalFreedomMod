package me.totalfreedom.totalfreedommod;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class SignPatch extends FreedomService
{

    public SignPatch(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayePlaceBlock(BlockPlaceEvent event)
    {

        final Player player = event.getPlayer();
        if (event.getBlock().getType().equals(Material.SIGN) || event.getBlock().getType().equals(Material.SIGN_POST) || event.getBlock().getType().equals(Material.WALL_SIGN))
        {

            ItemStack sign = event.getItemInHand();
            net.minecraft.server.v1_12_R1.ItemStack nmsSign = CraftItemStack.asNMSCopy(sign);
            NBTTagCompound compound = (nmsSign.hasTag()) ? nmsSign.getTag() : new NBTTagCompound();
            NBTTagCompound bet = compound.getCompound("BlockEntityTag");
            String line1 = bet.getString("Text1");
            String line2 = bet.getString("Text2");
            String line3 = bet.getString("Text3");
            String line4 = bet.getString("Text4");
            if(line1.contains("run_command") || line2.contains("run_command") || line3.contains("run_command") || line4.contains("run_command"))
            {
                player.sendMessage(ChatColor.GRAY + "You are not allowed to place command signs.");
                event.setCancelled(true);
            }
        }
    }
}
