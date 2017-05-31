package me.totalfreedom.totalfreedommod;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class AutoEject extends FreedomService
{

    private final Map<String, Integer> ejects = new HashMap<>(); // ip -> amount

    private final Map<Player, String> punishment = new HashMap<>();

    public AutoEject(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    public void autoEject(Player player, String kickMessage)
    {
        EjectMethod method = EjectMethod.STRIKE_ONE;
        final String ip = Ips.getIp(player);

        if (!ejects.containsKey(ip))
        {
            ejects.put(ip, 0);
        }

        int kicks = ejects.get(ip);
        kicks += 1;

        ejects.put(ip, kicks);

        if (kicks <= 1)
        {
            method = EjectMethod.STRIKE_ONE;
        }
        else if (kicks == 2)
        {
            method = EjectMethod.STRIKE_TWO;
        }
        else if (kicks >= 3)
        {
            method = EjectMethod.STRIKE_THREE;
        }

        FLog.info("AutoEject -> name: " + player.getName() + " - player ip: " + ip + " - method: " + method.toString());

        player.setOp(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        switch (method)
        {
            case STRIKE_ONE:
            {
                final Calendar cal = new GregorianCalendar();
                cal.add(Calendar.MINUTE, 5);
                final Date expires = cal.getTime();

                FUtil.bcastMsg(ChatColor.RED + player.getName() + " has been banned for 5 minutes.");

                plugin.bm.addBan(Ban.forPlayer(player, Bukkit.getConsoleSender(), expires, kickMessage));
                player.kickPlayer(kickMessage);

                break;
            }
            case STRIKE_TWO:
            {
                final Calendar c = new GregorianCalendar();
                c.add(Calendar.MINUTE, 10);
                final Date expires = c.getTime();

                FUtil.bcastMsg(ChatColor.RED + player.getName() + " has been banned for 10 minutes.");

                plugin.bm.addBan(Ban.forPlayer(player, Bukkit.getConsoleSender(), expires, kickMessage));
                player.kickPlayer(kickMessage);
                break;
            }
            case STRIKE_THREE:
            {
                plugin.bm.addBan(Ban.forPlayerFuzzy(player, Bukkit.getConsoleSender(), null, kickMessage));

                FUtil.bcastMsg(ChatColor.RED + player.getName() + " has been banned.");

                player.kickPlayer(kickMessage);
                break;
            }
        }
    }

    public static enum EjectMethod
    {

        STRIKE_ONE, STRIKE_TWO, STRIKE_THREE;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if (plugin.al.isAdmin(player))
        {
            return;
        }

        FPlayer fPlayer = plugin.pl.getPlayer(player);
        if (fPlayer.isMuted())
        {
            fPlayer.setMuted(true);
        }

        if (fPlayer.allCommandsBlocked())
        {
            fPlayer.setCommandsBlocked(true);
        }

        if (fPlayer.isPVPBlock())
        {
            fPlayer.setPVPBlock(true);
        }

        if (fPlayer.isEditBlock())
        {
            fPlayer.setEditBlocked(true);
        }

        if (fPlayer.isOrbiting())
        {
            double strength = 10.0;
            fPlayer.startOrbiting(strength);
        }

        if (fPlayer.getFreezeData().isFrozen())
        {
            fPlayer.getFreezeData().setFrozen(true);
        }

        /*
         if (fPlayer.getCageData().isCaged()) {
         fPlayer.getCageData().setCaged(true);
         player.setGameMode(GameMode.SURVIVAL);

         }
         */
    }

}
