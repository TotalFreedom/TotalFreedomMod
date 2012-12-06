package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = false)
public class Command_cake extends TFM_Command
{
    public static final int DROP_COUNT = 5;
    public static final double DROP_RADIUS_MAX = 3.0d;
    public static final double DROP_RADIUS_MIN = 1.0d;
    public static final double DROP_DURATION = 1.0d; // seconds
    public static final int BAKE_TIME = 30; // seconds
    public static Date bake_finished = new Date();

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if ((new Date()).before(bake_finished))
        {
            TFM_Util.playerMsg(sender, "Still baking cake, please wait...", ChatColor.BLUE);
            return true;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.SECOND, BAKE_TIME);
        bake_finished = c.getTime();

        StringBuilder output = new StringBuilder();
        final Random random = new Random();

        String words[] = TotalFreedomMod.CAKE_LYRICS.split(" ");
        for (String word : words)
        {
            String color_code = Integer.toHexString(1 + random.nextInt(14));
            output.append(ChatColor.COLOR_CHAR).append(color_code).append(word).append(" ");
        }

        TFM_Util.bcastMsg(output.toString());

        for (final Player p : server.getOnlinePlayers())
        {
            ItemStack heldItem = new ItemStack(Material.CAKE, 1);
            p.getInventory().setItem(p.getInventory().firstEmpty(), heldItem);

            for (double theta_p = 0.0; theta_p <= 1.0; theta_p += (1.0d / (double) DROP_COUNT))
            {
                final double theta = theta_p * Math.PI * 2.0d;
                final double radius = random.nextDouble() * (DROP_RADIUS_MAX - DROP_RADIUS_MIN) + DROP_RADIUS_MIN;

                Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        double x = radius * Math.cos(theta);
                        double z = radius * Math.sin(theta);
                        Location drop_pos = p.getLocation().clone().add(x * 3.0d, 3.0d, z * 3.0d);
                        p.getWorld().dropItemNaturally(drop_pos, new ItemStack(Material.CAKE));
                        p.playSound(drop_pos, Sound.BURP, 100.0f, 0.5f + random.nextFloat());
                    }
                }, Math.round(theta_p * 20.0d * DROP_DURATION));
            }
        }

        return true;
    }
}