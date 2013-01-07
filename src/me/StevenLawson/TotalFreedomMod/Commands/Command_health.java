package me.StevenLawson.TotalFreedomMod.Commands;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_TickMeter;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = false)
public class Command_health extends TFM_Command
{
    @Override
    public boolean run(final CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        Runnable task;
        task = new Runnable() // async
        {
            @Override
            public void run()
            {
                try
                {
                    TFM_TickMeter meter = new TFM_TickMeter(plugin);
                    meter.startTicking();
                    Thread.sleep(1000); // per second
                    meter.stopTicking();

                    Runtime runtime = Runtime.getRuntime();
                    int mb = 1048576; // 1024 * 1024

                    float usedMem = runtime.totalMemory() - runtime.freeMemory();

                    playerMsg("Reserved Memory: " + runtime.totalMemory() / mb + "mb");
                    playerMsg("Used Memory: " + new DecimalFormat("#").format(usedMem / mb)  + "mb (" + new DecimalFormat("#").format(usedMem/runtime.totalMemory()*100) + "%)");
                    playerMsg("Max Memory: " + runtime.maxMemory() / mb + "mb");
                    playerMsg("Ticks per second: " + (meter.getTicks() == 20 ? ChatColor.GREEN : ChatColor.RED) + meter.getTicks());
                }
                catch (Exception iex)
                {
                   TFM_Log.warning("Exception in TFM_TickMeter: Thread was interupted in sleeping process.");
                   TFM_Log.warning(ExceptionUtils.getStackTrace(iex));
                }
            }
        };
        new Thread(task, "TickMeter").start();
        return true;
    }
}
