package me.totalfreedom.totalfreedommod;

import ca.momothereal.mojangson.ex.MojangsonParseException;
import ca.momothereal.mojangson.value.MojangsonCompound;
import ca.momothereal.mojangson.value.MojangsonValue;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class MovementValidator extends FreedomService
{

    public static final int MAX_XYZ_COORD = 29999998;

    public MovementValidator(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        // Check absolute value to account for negatives
        if (Math.abs(event.getTo().getX()) >= MAX_XYZ_COORD || Math.abs(event.getTo().getZ()) >= MAX_XYZ_COORD || Math.abs(event.getTo().getY()) >= MAX_XYZ_COORD)
        {
            event.setCancelled(true); // illegal position, cancel it
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        final Player player = event.getPlayer();

        // Check absolute value to account for negatives
        if (Math.abs(event.getTo().getX()) >= MAX_XYZ_COORD || Math.abs(event.getTo().getZ()) >= MAX_XYZ_COORD || Math.abs(event.getTo().getY()) >= MAX_XYZ_COORD)
        {
            event.setCancelled(true);
            player.teleport(player.getWorld().getSpawnLocation());
        }

        if (exploitItem(event.getPlayer().getInventory().getHelmet()))
        {
            event.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
            event.getPlayer().sendMessage(ChatColor.RED + "An item with both negative infinity and positive infinity attributes was cleared from your helmet slot.");
            event.setCancelled(true);
        }
        if (exploitItem(event.getPlayer().getInventory().getBoots()))
        {
            event.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
            event.getPlayer().sendMessage(ChatColor.RED + "An item with both negative infinity and positive infinity attributes was cleared from your boots slot.");
            event.setCancelled(true);
        }
        if (exploitItem(event.getPlayer().getInventory().getLeggings()))
        {
            event.getPlayer().getInventory().setLeggings(new ItemStack(Material.AIR));
            event.getPlayer().sendMessage(ChatColor.RED + "An item with both negative infinity and positive infinity attributes was cleared from your leggings slot.");
            event.setCancelled(true);
        }
        if (exploitItem(event.getPlayer().getInventory().getChestplate()))
        {
            event.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
            event.getPlayer().sendMessage(ChatColor.RED + "An item with both negative infinity and positive infinity attributes was cleared from your chestplate slot.");
            event.setCancelled(true);
        }
        if (exploitItem(event.getPlayer().getInventory().getItemInMainHand()))
        {
            event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            event.getPlayer().sendMessage(ChatColor.RED + "An item with both negative infinity and positive infinity attributes was cleared from your hand.");
            event.setCancelled(true);
        }
        if (exploitItem(event.getPlayer().getInventory().getItemInOffHand()))
        {
            event.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            event.getPlayer().sendMessage(ChatColor.RED + "An item with both negative infinity and positive infinity attributes was cleared from your offhand.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        final Player player = event.getPlayer();

        // Validate position
        if (Math.abs(player.getLocation().getX()) >= MAX_XYZ_COORD || Math.abs(player.getLocation().getZ()) >= MAX_XYZ_COORD || Math.abs(player.getLocation().getY()) >= MAX_XYZ_COORD)
        {
            player.teleport(player.getWorld().getSpawnLocation()); // Illegal position, teleport to spawn
        }
    }

    @EventHandler
    public void onPlayerHoldItem(PlayerItemHeldEvent event)
    {
        if (exploitItem(event.getPlayer().getInventory().getItemInMainHand()))
        {
            event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            event.getPlayer().sendMessage(ChatColor.RED + "An item with both negative infinity and positive infinity attributes was cleared from your hand.");
        }
        if (exploitItem(event.getPlayer().getInventory().getItemInOffHand()))
        {
            event.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            event.getPlayer().sendMessage(ChatColor.RED + "An item with both negative infinity and positive infinity attributes was cleared from your offhand.");
        }
    }

    private Boolean exploitItem(ItemStack item)
    {
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagList modifiers = getAttributeList(nmsStack);
        MojangsonCompound compound = new MojangsonCompound();
        boolean foundNegative = false;
        boolean foundPositive = false;
        try
        {
            String mod = modifiers.toString();
            String fancy = ("{" + (mod.substring(1, mod.length() - 1).replace("{", "").replace("}", "")) + "}");
            compound.read(fancy);
            for (String key : compound.keySet())
            {
                if (Objects.equals(key, "Amount")) //null-safe .equals()
                {
                    List<MojangsonValue> values = compound.get(key);
                    for (MojangsonValue val : values)
                    {
                        if (val.getValue().toString().equals("Infinityd"))
                        {
                            foundPositive = true;
                        }
                        if (val.getValue().toString().equals("-Infinityd"))
                        {
                            foundNegative = true;
                        }
                    }
                }
            }
        }
        catch (MojangsonParseException e)
        {
            e.printStackTrace();
        }
        return foundNegative && foundPositive;
    }


    private NBTTagList getAttributeList(net.minecraft.server.v1_12_R1.ItemStack stack)
    {
        if (stack.getTag() == null)
        {
            stack.setTag(new NBTTagCompound());
        }
        NBTTagList attr = stack.getTag().getList("AttributeModifiers", 10);
        if (attr == null)
        {
            stack.getTag().set("AttributeModifiers", new NBTTagList());
        }
        return stack.getTag().getList("AttributeModifiers", 10);
    }
}


