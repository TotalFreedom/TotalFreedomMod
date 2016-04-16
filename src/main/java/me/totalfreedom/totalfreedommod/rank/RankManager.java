package me.totalfreedom.totalfreedommod.rank;

import java.util.List;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.config.MainConfig;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
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

    public RankBase getDisplay(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return getRank(sender); // Consoles don't have display ranks
        }

        final Player player = (Player) sender;

        // Display impostors
        if (plugin.al.isAdminImpostor(player))
        {
            return Rank.IMPOSTOR;
        }

        // Developers always show up
        if (FUtil.DEVELOPERS.contains(player.getName()))
        {
            return Title.DEVELOPER;
        }

        final Rank rank = getRank(player);

        // Non-admins don't have titles, display actual rank
        if (!rank.isAdmin())
        {
            return rank;
        }

        // If the player's an owner, display that
        if (MainConfig.get(ConfigEntry.SERVER_OWNERS, List.class).contains(player.getName()))
        {
            return Title.OWNER;
        }

        return rank;
    }

    public Rank getRank(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getRank((Player) sender);
        }

        // CONSOLE?
        if (sender.getName().equals("CONSOLE"))
        {
            return ConfigEntry.ADMINLIST_CONSOLE_IS_SENIOR.getBoolean() ? Rank.SENIOR_CONSOLE : Rank.TELNET_CONSOLE;
        }

        // Console admin, get by name
        Admin admin = plugin.al.getEntryByName(sender.getName());

        // Unknown console: RCON?
        if (admin == null)
        {
            return Rank.SENIOR_CONSOLE;
        }

        return admin.getRank();
    }

    public Rank getRank(Player player)
    {
        if (plugin.al.isAdminImpostor(player))
        {
            return Rank.IMPOSTOR;
        }

        final Admin entry = plugin.al.getAdmin(player);
        if (entry != null)
        {
            return entry.getRank();
        }

        return player.isOp() ? Rank.OP : Rank.NON_OP;
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
            FUtil.bcastMsg(ChatColor.AQUA + player.getName() + " is " + Rank.IMPOSTOR.getColoredLoginMessage());
            FUtil.bcastMsg("Warning: " + player.getName() + " has been flagged as an impostor and has been frozen!", ChatColor.RED);
            player.getInventory().clear();
            player.setOp(false);
            player.setGameMode(GameMode.SURVIVAL);
            plugin.pl.getPlayer(player).getFreezeData().setFrozen(true);
            player.sendMessage(ChatColor.RED + "You are marked as an impostor, please verify yourself!");
            return;
        }

        // Set display
        if (isAdmin || FUtil.DEVELOPERS.contains(player.getName()))
        {
            final RankBase display = getDisplay(player);
            String loginMsg = display.getColoredLoginMessage();

            if (isAdmin)
            {
                Admin admin = plugin.al.getAdmin(player);
                if (admin.hasLoginMessage())
                {
                    loginMsg = FUtil.colorize(admin.getLoginMessage()); // Adds ChatColor Code support.
                }
            }

            FUtil.bcastMsg(ChatColor.AQUA + player.getName() + " is " + loginMsg);
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
