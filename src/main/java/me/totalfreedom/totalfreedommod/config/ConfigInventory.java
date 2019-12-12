package me.totalfreedom.totalfreedommod.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ConfigInventory
{
    @Getter
    @Setter
    private Map<Integer, ItemStack> inventoryItems;

    public ConfigInventory(Inventory inv)
    {
        updateInventory(inv);
    }

    public void set(int location, Material material, int amount, NBTTagCompound nbt)
    {
        ItemStack stack = new ItemStack(material, amount);
        /*
        net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        nmsStack.setTag(nbt);
        stack = CraftItemStack.asBukkitCopy(nmsStack);
        */
        inventoryItems.put(location, stack);
    }

    public void set(int location, ItemStack stack)
    {
        inventoryItems.put(location, stack);
    }

    public ItemStack get(int location)
    {
        return inventoryItems.get(location);
    }

    public boolean hasNBT(int location)
    {
        return CraftItemStack.asNMSCopy(inventoryItems.get(location)).hasTag();
    }

    public NBTTagCompound getNBT(int location)
    {
        return CraftItemStack.asNMSCopy(inventoryItems.get(location)).getTag();
    }

    public void updateInventory(Inventory inv)
    {
        inventoryItems = new HashMap<>();
        if (inv == null)
        {
            return;
        }
        for (int i = 0; i < inv.getSize(); i++)
        {
            inventoryItems.put(i, inv.getItem(i));
        }
    }

    public void save(ConfigurationSection cs)
    {
        for (int i = 0; i < inventoryItems.size(); i++)
        {
            ItemStack currentStack = inventoryItems.get(i);
            net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(currentStack);
            if (currentStack == null)
            {
                ItemStack air = new ItemStack(Material.AIR, 1);
                inventoryItems.put(i, air);
                currentStack = air;
            }
            cs.set("inventory." + i + ".type", currentStack.getType().name());
            cs.set("inventory." + i + ".amount", currentStack.getAmount());
            if (nmsStack.hasTag())
            {
                cs.set("inventory." + i + ".nbt", nmsStack.getTag().toString());
            }
        }
    }

    public static ConfigInventory createInventoryFromConfig(ConfigurationSection cs)
    {
        ConfigInventory configInventory = new ConfigInventory(null);
        for (int i = 0; i < 40; i++)
        {
            configInventory.set(i, Material.valueOf(cs.getString("inventory." + i + ".type")),
                    cs.getInt("inventory." + i + ".amount"), null);//,
                    //(NBTTagCompound) cs.get("inventory." + i + ".nbt"));
        }
        return configInventory;
    }
}
