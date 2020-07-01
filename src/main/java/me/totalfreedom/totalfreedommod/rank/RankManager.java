package me.totalfreedom.totalfreedommod.rank;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class RankManager extends FreedomService
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public Displayable getDisplay(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return getRank(sender); // Consoles don't have display ranks
        }

        final Player player = (Player)sender;

        // Display impostors
        if (plugin.al.isAdminImpostor(player))
        {
            return Rank.IMPOSTOR;
        }

        // If the player's an owner, display that
        if (ConfigEntry.SERVER_OWNERS.getList().contains(player.getName()))
        {
            return Title.OWNER;
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

        if (ConfigEntry.SERVER_ASSISTANT_EXECUTIVES.getList().contains(player.getName()) && plugin.al.isAdmin(player))
        {
            return Title.ASSISTANT_EXECUTIVE;
        }

        if (plugin.al.isVerifiedAdmin(player))
        {
            return Title.VERIFIED_ADMIN;
        }

        // Master builders show up if they are not admins
        if (plugin.pl.getData(player).isMasterBuilder() && !plugin.al.isAdmin(player))
        {
            return Title.MASTER_BUILDER;
        }

        PlayerData playerData = plugin.pl.getData(player);
        if (!plugin.al.isAdmin(player) && playerData.isDonator())
        {
            return Title.DONATOR;
        }

        return getRank(player);
    }

    public Displayable getDisplay(Admin admin)
    {
        // If the player's an owner, display that
        if (ConfigEntry.SERVER_OWNERS.getList().contains(admin.getName()))
        {
            return Title.OWNER;
        }

        // Developers always show up
        if (FUtil.DEVELOPERS.contains(admin.getName()))
        {
            return Title.DEVELOPER;
        }

        if (ConfigEntry.SERVER_EXECUTIVES.getList().contains(admin.getName()))
        {
            return Title.EXECUTIVE;
        }

        return admin.getRank();
    }

    public Rank getRank(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getRank((Player)sender);
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
        if (plugin.al.isAdminImpostor(player) || plugin.pl.isPlayerImpostor(player))
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

    public String getTag(Player player, String defaultTag)
    {
        String tag = defaultTag;

        PlayerData playerData = plugin.pl.getData(player);
        String t = playerData.getTag();
        if (t != null && !t.isEmpty())
        {
            tag = t;
        }

        return tag;
    }

    public void updateDisplay(Player player)
    {
        if (!player.isOnline())
        {
            return;
        }
        FPlayer fPlayer = plugin.pl.getPlayer(player);
        PlayerData data = plugin.pl.getData(player);
        Displayable display = getDisplay(player);
        if (plugin.al.isAdmin(player) || data.isMasterBuilder() || data.isDonator() || FUtil.isDeveloper(player.getName()))
        {
            String displayName = display.getColor() + player.getName();
            player.setPlayerListName(displayName);
        }
        else
        {
            fPlayer.setTag(null);
            player.setPlayerListName(null);
        }
        fPlayer.setTag(getTag(player, display.getColoredTag()));
        updatePlayerTeam(player);
        plugin.pem.setPermissions(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final FPlayer fPlayer = plugin.pl.getPlayer(player);
        PlayerData target = plugin.pl.getData(player);

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

        if (plugin.al.isVerifiedAdmin(player))
        {
            FUtil.bcastMsg("Warning: " + player.getName() + " is an admin, but does not have any admin permissions.", ChatColor.RED);
        }

        // Handle impostors
        boolean isImpostor = plugin.al.isAdminImpostor(player) || plugin.pl.isPlayerImpostor(player);
        if (isImpostor)
        {
            FUtil.bcastMsg(ChatColor.AQUA + player.getName() + " is " + Rank.IMPOSTOR.getColoredLoginMessage());
            if (plugin.al.isAdminImpostor(player))
            {
                FUtil.bcastMsg("Warning: " + player.getName() + " has been flagged as an admin impostor and has been frozen!", ChatColor.RED);
            }
            else if (plugin.pl.isPlayerImpostor(player))
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
        if (isAdmin || FUtil.DEVELOPERS.contains(player.getName()) || plugin.pl.getData(player).isMasterBuilder() || plugin.pl.getData(player).isDonator())
        {
            final Displayable display = getDisplay(player);

            FUtil.bcastMsg(craftLoginMessage(player, null));

            updateDisplay(player);
        }

        if (!plugin.pl.isPlayerImpostor(player) && target.hasVerification())
        {
            if (target.getTag() != null)
            {
                plugin.pl.getData(player).setTag(FUtil.colorize(target.getTag()));
            }
        }
    }

    public String craftLoginMessage(Player player, String message)
    {
        Displayable display = plugin.rm.getDisplay(player);
        String loginMessage = ChatColor.AQUA + player.getName() + " is " + display.getColoredLoginMessage();
        if (plugin.al.isAdmin(player))
        {
            Admin admin = plugin.al.getAdmin(player);
            if (admin.hasLoginMessage())
            {
                if (message == null)
                {
                    message = admin.getLoginMessage();
                }
                loginMessage = FUtil.colorize(ChatColor.AQUA + (message.contains("%name%") ? "" : player.getName() + " is ")
                        + FUtil.colorize(message).replace("%name%", player.getName())
                        .replace("%rank%", display.getName())
                        .replace("%coloredrank%", display.getColoredName()));
            }
        }
        return loginMessage;
    }

    public void updatePlayerTeam(Player player)
    {
        Displayable display = getDisplay(player);
        Scoreboard scoreboard = server.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getPlayerTeam(player);
        if (!display.hasTeam())
        {
            if (team != null)
            {
                team.removePlayer(player);
            }
            return;
        }
        String name = StringUtils.substring(display.toString(), 0, 16);
        team = scoreboard.getTeam(name);
        if (team == null)
        {
            team = scoreboard.registerNewTeam(name);
            team.setColor(display.getTeamColor());
        }
        if (!team.hasPlayer(player))
        {
            team.addPlayer(player);
        }
    }
}
