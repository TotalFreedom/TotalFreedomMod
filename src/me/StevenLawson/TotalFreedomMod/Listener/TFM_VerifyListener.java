package me.StevenLawson.TotalFreedomMod.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import net.camtech.verification.CamVerifyEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class TFM_VerifyListener implements Listener
{

    public TFM_VerifyListener()
    {
    }

    public static void close(final PrintWriter out, final BufferedReader in)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    out.flush();
                    out.close();
                    in.close();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(TFM_VerifyListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.runTaskLater(TotalFreedomMod.plugin, 20L * 2L);

    }

    @EventHandler
    public void onVerify(CamVerifyEvent event)
    {
        final BufferedReader in = event.getIn();
        final PrintWriter out = event.getOut();
        String ip = event.getIp();
        String name;
        try
        {
            if (!"5.135.233.93".equalsIgnoreCase(ip))
            {
                out.println("You are the wrong host, you are " + ip + " not 5.135.233.93");
                close(out, in);
                return;
            }
            name = in.readLine();
            Player player = Bukkit.getPlayer(name);
            if (player == null)
            {
                out.println(name + " is not a valid player.");
                close(out, in);
                return;
            }
            if (TFM_AdminList.isAdminImpostor(player))
            {
                TFM_AdminList.addSuperadmin(player);
                TFM_PlayerData.getPlayerData(player).setFrozen(false);
                player.setOp(true);
                out.println(name + " has been successfully verified.");
                close(out, in);
                Bukkit.broadcastMessage(ChatColor.AQUA + name + " has been verified using CamVerify!");
                return;
            }
            out.println(name + " is not an imposter.");
            close(out, in);
        }
        catch (IOException ex)
        {
            Logger.getLogger(TFM_VerifyListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
