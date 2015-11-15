package me.totalfreedom.totalfreedommod.rank;

import java.util.List;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.config.MainConfig;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.component.service.AbstractService;
import net.pravian.aero.util.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class RankManager extends AbstractService<TotalFreedomMod>
{

    public RankManager(TotalFreedomMod plugin)
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

    public Rank getDisplayRank(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return getRank(sender); // Consoles don't have display ranks
        }

        final Player player = (Player) sender;

        // Developers always show up
        if (FUtil.DEVELOPERS.contains(player.getName()))
        {
            return TitleRank.DEVELOPER;
        }

        final PlayerRank playerRank = getRank((Player) sender);
        final Admin admin = playerRank.isAdmin() ? plugin.al.getAdmin(sender) : null;

        // Titles except developer are only for admins
        if (admin == null)
        {
            return playerRank;
        }

        if (MainConfig.get(ConfigEntry.SERVER_OWNERS, List.class).contains(player.getName()))
        {
            return TitleRank.OWNER;
        }

        final String loginMessage = admin.getLoginMessage();
        return loginMessage == null ? playerRank : new CustomLoginRank(playerRank, ChatUtils.colorize(loginMessage));

    }

    public Rank getRank(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getRank((Player) sender);
        }

        Admin admin = plugin.al.getEntryByName(sender.getName());

        if (admin == null)
        { // Unknown console, RCon, CONSOLE?
            return ConsoleRank.SENIOR_CONSOLE;
        }

        return ConsoleRank.forRank(admin.getRank());
    }

    public PlayerRank getRank(Player player)
    {
        if (plugin.al.isAdminImpostor(player))
        {
            return PlayerRank.IMPOSTOR;
        }

        final Admin entry = plugin.al.getAdmin(player);
        if (entry == null)
        {
            return player.isOp() ? PlayerRank.OP : PlayerRank.NON_OP;
        }

        return entry.getRank();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final PlayerData data = plugin.pl.getData(player);
        final FPlayer fPlayer = plugin.pl.getPlayer(player);

        // Unban admins
        boolean isAdmin = plugin.al.isAdmin(player);
        if (isAdmin)
        {
            // Verify strict IP match
            if (!plugin.al.isIdentityMatched(player))
            {
                fPlayer.setSuperadminIdVerified(false);
                FUtil.bcastMsg("Warning: " + player.getName() + " is an admin, but is using an account not registered to one of their ip-list.", ChatColor.RED);
            }
            else
            {
                fPlayer.setSuperadminIdVerified(true);
                plugin.al.updateLastLogin(player);
            }
        }

        // Handle impostors
        if (plugin.al.isAdminImpostor(player))
        {
            FUtil.bcastMsg("Warning: " + player.getName() + " has been flagged as an impostor and has been frozen!", ChatColor.RED);
            FUtil.bcastMsg(ChatColor.AQUA + player.getName() + " is " + plugin.rm.getDisplayRank(player).getColoredLoginMessage());
            player.getInventory().clear();
            player.setOp(false);
            player.setGameMode(GameMode.SURVIVAL);
            plugin.pl.getPlayer(player).getFreezeData().setFrozen(true);
        }

        // Set display
        Rank display = getDisplayRank(player);
        if (isAdmin || FUtil.DEVELOPERS.contains(player.getName()))
        {
            FUtil.bcastMsg(ChatColor.AQUA + player.getName() + " is " + display.getColoredLoginMessage());
            plugin.pl.getPlayer(player).setTag(display.getColoredTag());
            try
            {
                String displayName = display.getColorString() + player.getName();
                player.setPlayerListName(displayName.substring(0, 16));
            }
            catch (IllegalArgumentException ex)
            {
            }
        }
    }
}
