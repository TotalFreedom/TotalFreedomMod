package me.StevenLawson.TotalFreedomMod.Commands;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "View ticks-per-second", usage = "/<command>")
public class Command_health extends TFM_Command
{
    private static final int BYTES_PER_MB = 1024 * 1024;
    private static final DoubleRange TPS_RANGE = new DoubleRange(20.0 - 0.1, 20.0 + 0.1);

    @Override
    public boolean run(final CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Runtime runtime = Runtime.getRuntime();
        long usedMem = runtime.totalMemory() - runtime.freeMemory();

        playerMsg("Reserved Memory: " + (double) runtime.totalMemory() / (double) BYTES_PER_MB + "mb");
        playerMsg("Used Memory: " + new DecimalFormat("#").format((double) usedMem / (double) BYTES_PER_MB) + "mb (" + new DecimalFormat("#").format(((double) usedMem / (double) runtime.totalMemory()) * 100.0) + "%)");
        playerMsg("Max Memory: " + (double) runtime.maxMemory() / (double) BYTES_PER_MB + "mb");
        playerMsg("Calculating ticks per second, please wait...");

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    TFM_TickMeter tickMeter = new TFM_TickMeter(plugin);
                    tickMeter.startTicking();
                    Thread.sleep(2500);
                    final double ticksPerSecond = tickMeter.stopTicking();

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            playerMsg("Ticks per second: " + (TPS_RANGE.containsDouble(ticksPerSecond) ? ChatColor.GREEN : ChatColor.RED) + ticksPerSecond);
                        }
                    }.runTask(plugin);
                }
                catch (Exception ex)
                {
                    TFM_Log.severe(ex);
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }

    private class TFM_TickMeter
    {
        private final AtomicInteger ticks = new AtomicInteger();
        private final TotalFreedomMod plugin;
        private long startTime;
        private BukkitTask task;

        public TFM_TickMeter(TotalFreedomMod plugin)
        {
            this.plugin = plugin;
        }

        public void startTicking()
        {
            startTime = System.currentTimeMillis();
            ticks.set(0);

            task = new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    ticks.incrementAndGet();
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }

        public double stopTicking()
        {
            task.cancel();
            long elapsed = System.currentTimeMillis() - startTime;
            int tickCount = ticks.get();

            return (double) tickCount / ((double) elapsed / 1000.0);
        }
    }
}
