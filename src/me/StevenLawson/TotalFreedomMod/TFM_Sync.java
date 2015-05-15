package me.StevenLawson.TotalFreedomMod;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TFM_Sync
{

    public static void playerMsg(final Player player, final String message)
    {
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                TFM_Util.playerMsg(player, message);
            }

        }.runTask(TotalFreedomMod.plugin);
    }

    public static void playerKick(final Player player, final String reason)
    {
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                player.kickPlayer(reason);
            }

        }.runTask(TotalFreedomMod.plugin);
    }

    public static void adminChatMessage(final CommandSender sender, final String message, final boolean isRed)
    {
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                TFM_Util.adminChatMessage(sender, message, isRed);
            }

        }.runTask(TotalFreedomMod.plugin);
    }

    public static void autoEject(final Player player, final String kickMessage)
    {
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                TFM_Util.autoEject(player, kickMessage);
            }

        }.runTask(TotalFreedomMod.plugin);
    }

    public static void bcastMsg(final String message, final ChatColor color)
    {
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                TFM_Util.bcastMsg(message, color);
            }

        }.runTask(TotalFreedomMod.plugin);
    }
}
