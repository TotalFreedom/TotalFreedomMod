package me.totalfreedom.totalfreedommod.rank;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.ChatUtils;
import org.apache.commons.lang3.StringUtils;
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

    public Displayable getDisplay(CommandSender sender)
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

        // Master builders show up if they are not admins
        if (plugin.mbl.isMasterBuilder(player) && !plugin.al.isAdmin(player))
        {
            return Title.MASTER_BUILDER;
        }

        // Developers always show up
        if (FUtil.DEVELOPERS.contains(player.getName()))
        {
            return Title.DEVELOPER;
        }

        if (ConfigEntry.SERVER_EXECUTIVES.getList().contains(player.getName()) && plugin.al.isAdmin(player))
        {
            return Title.EXECUTIVE;
        }

        // If the player's an owner, display that
        if (ConfigEntry.SERVER_OWNERS.getList().contains(player.getName()))
        {
            return Title.OWNER;
        }

        return getRank(player);
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

        Rank rank = admin.getRank();

        // Get console
        if (rank.hasConsoleVariant())
        {
            rank = rank.getConsoleVariant();
        }
        return rank;
    }

    public Rank getRank(Player player)
    {
        if (plugin.al.isAdminImpostor(player) || plugin.pv.isPlayerImpostor(player) || plugin.mbl.isMasterBuilderImpostor(player))
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

    public void updateDisplay(Player player)
    {
        if (!player.isOnline())
        {
            return;
        }
        FPlayer fPlayer = plugin.pl.getPlayer(player);
        if (plugin.al.isAdmin(player))
        {
            Displayable display = getDisplay(player);
            fPlayer.setTag(display.getColoredTag());
            String displayName = display.getColor() + player.getName();
            player.setPlayerListName(StringUtils.substring(displayName, 0, 16));
        }
        else
        {
            fPlayer.setTag(null);
            player.setPlayerListName(null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        //plugin.pl.getData(player);
        final FPlayer fPlayer = plugin.pl.getPlayer(player);

        // Unban admins
        boolean isAdmin = plugin.al.isAdmin(player);
        if (isAdmin)
        {
            // Verify strict IP match
            if (!plugin.al.isIdentityMatched(player))
            {
                FUtil.bcastMsg("Warning: " + player.getName() + " is an admin, but is using an account not registered to one of their ip-list.", ChatColor.RED);
                fPlayer.setSuperadminIdVerified(false);
            }
            else
            {
                fPlayer.setSuperadminIdVerified(true);
                plugin.al.updateLastLogin(player);
            }
        }

        // Handle impostors
        Boolean isImposter = plugin.al.isAdminImpostor(player) || plugin.pv.isPlayerImpostor(player) || plugin.mbl.isMasterBuilderImpostor(player);
        if (isImposter)
        {
            FUtil.bcastMsg(ChatColor.AQUA + player.getName() + " is " + Rank.IMPOSTOR.getColoredLoginMessage());
            if (plugin.al.isAdminImpostor(player))
            {
                FUtil.bcastMsg("Warning: " + player.getName() + " has been flagged as an admin impostor and has been frozen!", ChatColor.RED);
            }
            else if (plugin.mbl.isMasterBuilderImpostor(player))
            {
                FUtil.bcastMsg("Warning: " + player.getName() + " has been flagged as a Master Builder impostor and has been frozen!", ChatColor.RED);
            }
            else if (plugin.pv.isPlayerImpostor(player))
            {
                FUtil.bcastMsg("Warning: " + player.getName() + " has been flagged as a player impostor and has been frozen!", ChatColor.RED);
            }
            String displayName = Rank.IMPOSTOR.getColor() + player.getName();
            player.setPlayerListName(StringUtils.substring(displayName, 0, 16));
            player.getInventory().clear();
            player.setOp(false);
            player.setGameMode(GameMode.SURVIVAL);
            plugin.pl.getPlayer(player).getFreezeData().setFrozen(true);
            player.sendMessage(ChatColor.RED + "You are marked as an impostor, please verify yourself!");
            return;
        }

        // Set display
        if (isAdmin || FUtil.DEVELOPERS.contains(player.getName()) || plugin.mbl.isMasterBuilder(player))
        {
            final Displayable display = getDisplay(player);
            String loginMsg = display.getColoredLoginMessage();

            if (isAdmin)
            {
                Admin admin = plugin.al.getAdmin(player);
                if (admin.hasLoginMessage())
                {
                    loginMsg = ChatUtils.colorize(admin.getLoginMessage());
                }
            }

            FUtil.bcastMsg(loginMsg.replace("%name%", player.getName()));
            plugin.pl.getPlayer(player).setTag(display.getColoredTag());

            if (isAdmin)
            {
                Admin admin = plugin.al.getAdmin(player);
                if (admin.getTag() != null)
                {
                    plugin.pl.getPlayer(player).setTag(FUtil.colorize(admin.getTag()));
                }
            }

            String displayName = display.getColor() + player.getName();
            try
            {
                player.setPlayerListName(StringUtils.substring(displayName, 0, 16));
            }
            catch (IllegalArgumentException ex)
            {
            }
        }
    }
}
