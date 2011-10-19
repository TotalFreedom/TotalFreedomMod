package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class Command_mp44 extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || TFM_Util.isUserSuperadmin(sender, plugin))
        {
            if (senderIsConsole)
            {
                sender.sendMessage(TotalFreedomMod.NOT_FROM_CONSOLE);
                return true;
            }

            if (args.length == 0)
            {
                return false;
            }

            TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(sender_p, plugin);

            if (args[0].equalsIgnoreCase("draw"))
            {
                playerdata.stopArrowShooter();
                int schedule_id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new ArrowShooter(sender_p), 1L, 1L);
                playerdata.startArrowShooter(schedule_id);
            }
            else
            {
                playerdata.stopArrowShooter();
            }
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
    
    class ArrowShooter implements Runnable
    {
        private Player _player;
        
        public ArrowShooter(Player player)
        {
            _player = player;
        }

        @Override
        public void run()
        {
            Arrow shot_arrow = _player.shootArrow();
            shot_arrow.setVelocity(shot_arrow.getVelocity().multiply(2.0));
        }
    }
}
