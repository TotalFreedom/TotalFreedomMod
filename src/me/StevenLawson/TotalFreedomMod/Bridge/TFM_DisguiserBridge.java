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
                    names.add(TFM_PlayerRank.fromSender(player).getPrefix() + player.getName() + ChatColor.GOLD + "(" + api.getDisguise(player).type.name() + ")" + ChatColor.RESET);
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
