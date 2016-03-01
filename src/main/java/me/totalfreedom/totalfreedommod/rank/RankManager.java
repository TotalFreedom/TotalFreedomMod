package me.totalfreedom.totalfreedommod.rank;

import java.util.List;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.config.MainConfig;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class RankManager extends FreedomService
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

        // Display impostors
        if (plugin.al.isAdminImpostor(player))
        {
            return PlayerRank.IMPOSTOR;
        }

        // Developers always show up
        if (FUtil.DEVELOPERS.contains(player.getName()))
        {
            return TitleRank.DEVELOPER;
        }

        final PlayerRank rank = getRank(player);
        final Admin admin = rank.isAdmin() ? plugin.al.getAdmin(sender) : null;

        // Non-admins don't have titles, display actual rank
        if (admin == null)
        {
            return rank;
        }

        // If the player's an owner, display that
        if (MainConfig.get(ConfigEntry.SERVER_OWNERS, List.class).contains(player.getName()))
        {
            return TitleRank.OWNER;
        }

        final String loginMessage = admin.getLoginMessage();

        // If we don't have a custom login message, use the actual rank
        if (loginMessage == null)
        {
            return rank;
        }

        return new CustomLoginRank(rank, ChatUtils.colorize(loginMessage));
    }

    public PlayerRank getRank(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getRank((Player) sender);
        }

        // Console admin, get by name
        Admin admin = plugin.al.getEntryByName(sender.getName());

        // Unknown console: RCON, CONSOLE?
        if (admin == null)
        {
            return PlayerRank.SENIOR_CONSOLE;
        }

        return admin.getRank();
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
        //plugin.pl.getData(player);
        final FPlayer fPlayer = plugin.pl.getPlayer(player);

        // Unban admins
        boolean isAdmin = plugin.al.isAdmin(player);
        fPlayer.setSuperadminIdVerified(false);
        if (isAdmin)
        {
            // Verify strict IP match
            if (!plugin.al.isIdentityMatched(player))
            {
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
                String displayName = display.getColor() + player.getName();
                player.setPlayerListName(displayName.substring(0, Math.min(displayName.length(), 16)));
            }
            catch (IllegalArgumentException ex)
            {
            }
        }
    }
}
