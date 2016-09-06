package me.unraveledmc.unraveledmcmod.command;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FLog;
import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "View ticks-per-second", usage = "/<command>")
public class Command_health extends FreedomCommand
{

    private static final int BYTES_PER_MB = 1024 * 1024;
    private static final DoubleRange TPS_RANGE = new DoubleRange(20.0 - 0.1, 20.0 + 0.1);

    @Override
    public boolean run(final CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Runtime runtime = Runtime.getRuntime();
        long usedMem = runtime.totalMemory() - runtime.freeMemory();

        msg("Reserved Memory: " + (double) runtime.totalMemory() / (double) BYTES_PER_MB + "mb");
        msg("Used Memory: " + new DecimalFormat("#").format((double) usedMem / (double) BYTES_PER_MB)
                + "mb (" + new DecimalFormat("#").format(((double) usedMem / (double) runtime.totalMemory()) * 100.0) + "%)");
        msg("Max Memory: " + (double) runtime.maxMemory() / (double) BYTES_PER_MB + "mb");
        msg("Calculating ticks per second, please wait...");

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

                    // Plugin was disabled during async task
                    if (!plugin.isEnabled())
                    {
                        return;
                    }

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            msg("Ticks per second: " + (TPS_RANGE.containsDouble(ticksPerSecond) ? ChatColor.GREEN : ChatColor.RED) + ticksPerSecond);
                        }
                    }.runTask(plugin);
                }
                catch (Exception ex)
                {
                    FLog.severe(ex);
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }

    private class TFM_TickMeter
    {

        private final AtomicInteger ticks = new AtomicInteger();
        private final UnraveledMCMod plugin;
        private long startTime;
        private BukkitTask task;

        public TFM_TickMeter(UnraveledMCMod plugin)
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
