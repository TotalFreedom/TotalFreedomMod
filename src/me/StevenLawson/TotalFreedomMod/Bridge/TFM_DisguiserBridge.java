package me.StevenLawson.TotalFreedomMod.Bridge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerRank;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class TFM_DisguiserBridge
{
    private TFM_DisguiserBridge()
    {
    }

    public static boolean undisguisePlayer(Player player)
    {
        if (!disguiseCraftEnabled())
        {
            return false;
        }

        try
        {
            DisguiseCraftAPI api = DisguiseCraft.getAPI();
            if (api != null)
            {
                return api.undisguisePlayer(player);
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }

        return false;
    }

    public static void undisguiseAllPlayers()
    {
        if (!disguiseCraftEnabled())
        {
            return;
        }

        try
        {
            DisguiseCraftAPI api = DisguiseCraft.getAPI();
            if (api != null)
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    api.undisguisePlayer(player);
                }
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    @SuppressWarnings("IncompatibleEquals")
    public static String getDisguise(Player player)
    {
            DisguiseCraftAPI api = DisguiseCraft.getAPI();
            if (api != null)
            {
                Disguise disguise = api.getDisguise(player);

                if (disguise == null)
                {
                    return "None";
                }

                if (disguise.equals(DisguiseType.Cow))
                {
                    return "Cow";
                }

                if (disguise.equals(DisguiseType.ArmorStand))
                {
                    return "Armour Stand";
                }

                if (disguise.equals(DisguiseType.Bat))
                {
                    return "Bat";
                }

                if (disguise.equals(DisguiseType.Blaze))
                {
                    return "Blaze";
                }

                if (disguise.equals(DisguiseType.Boat))
                {
                    return "Boat";
                }

                if (disguise.equals(DisguiseType.CaveSpider))
                {
                    return "Cave Spider";
                }

                if (disguise.equals(DisguiseType.Chicken))
                {
                    return "Chicken";
                }

                if (disguise.equals(DisguiseType.Creeper))
                {
                    return "Creeper";
                }

                if (disguise.equals(DisguiseType.EnderCrystal))
                {
                    return "Ender Crystal";
                }

                if (disguise.equals(DisguiseType.EnderDragon))
                {
                    return "Ender Dragon";
                }
                if (disguise.equals(DisguiseType.Enderman))
                {
                    return "Ender Man";
                }
                if (disguise.equals(DisguiseType.Endermite))
                {
                    return "Endermite";
                }
                if (disguise.equals(DisguiseType.FallingBlock))
                {
                    return "Falling Block";
                }
                if (disguise.equals(DisguiseType.Ghast))
                {
                    return "Ghast";
                }
                if (disguise.equals(DisguiseType.Giant))
                {
                    return "Giant";
                }
                if (disguise.equals(DisguiseType.Guardian))
                {
                    return "Guardian";
                }
                if (disguise.equals(DisguiseType.Horse))
                {
                    return "Horse";
                }
                if (disguise.equals(DisguiseType.IronGolem))
                {
                    return "Iron Golem";
                }
                if (disguise.equals(DisguiseType.MagmaCube))
                {
                    return "Magma Cube";
                }
                if (disguise.equals(DisguiseType.Minecart))
                {
                    return "Minecart";
                }
                if (disguise.equals(DisguiseType.MushroomCow))
                {
                    return "Mushroom Cow";
                }
                if (disguise.equals(DisguiseType.Ocelot))
                {
                    return "Ocelot";
                }
                if (disguise.equals(DisguiseType.Pig))
                {
                    return "Pig";
                }
                if (disguise.equals(DisguiseType.PigZombie))
                {
                    return "Pig Zombie";
                }
                if (disguise.equals(DisguiseType.Player))
                {
                    return "Player";
                }
                if (disguise.equals(DisguiseType.Rabbit))
                {
                    return "Rabbit";
                }
                if (disguise.equals(DisguiseType.Sheep))
                {
                    return "Sheep";
                }
                if (disguise.equals(DisguiseType.Silverfish))
                {
                    return "Silverfish";
                }
                if (disguise.equals(DisguiseType.Skeleton))
                {
                    return "Skeleton";
                }
                if (disguise.equals(DisguiseType.Slime))
                {
                    return "Slime";
                }
                if (disguise.equals(DisguiseType.Snowman))
                {
                    return "Snowman";
                }
                if (disguise.equals(DisguiseType.Spider))
                {
                    return "Spider";
                }
                if (disguise.equals(DisguiseType.Squid))
                {
                    return "Squid";
                }
                if (disguise.equals(DisguiseType.TNTPrimed))
                {
                    return "TNT Primed";
                }
                if (disguise.equals(DisguiseType.Villager))
                {
                    return "Villager";
                }
                if (disguise.equals(DisguiseType.Witch))
                {
                    return "Witch";
                }
                if (disguise.equals(DisguiseType.Wither))
                {
                    return "Wither";
                }
                if (disguise.equals(DisguiseType.Wolf))
                {
                    return "Wolf";
                }
                if (disguise.equals(DisguiseType.Zombie))
                {
                    return "Zombie";
                }
            }
           return "Undefined";
    }

    public static void listDisguisedPlayers(CommandSender sender)
    {
        if (!disguiseCraftEnabled())
        {
            return;
        }

        try
        {
            DisguiseCraftAPI api = DisguiseCraft.getAPI();
            if (api != null)
            {
                final List<String> names = new ArrayList<String>();
                for (Player player : api.getOnlineDisguisedPlayers())
                {
                    names.add(TFM_PlayerRank.fromSender(player).getPrefix() + player.getName() + ChatColor.GOLD + "(" + getDisguise(player) + ")" + ChatColor.RESET);
                }
                final StringBuilder onlineUsers = new StringBuilder();
                onlineUsers.append("Disguised Players: ");
                onlineUsers.append(StringUtils.join(names, ChatColor.WHITE + ", "));
                sender.sendMessage(onlineUsers.toString());
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }

    public static boolean disguiseCraftEnabled()
    {
        boolean pluginEnabled = false;
        try
        {
            pluginEnabled = Bukkit.getPluginManager().isPluginEnabled("DisguiseCraft");
        }
        catch (Exception ex)
        {
        }
        return pluginEnabled;
    }
}
