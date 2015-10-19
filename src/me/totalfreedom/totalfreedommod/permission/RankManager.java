package me.totalfreedom.totalfreedommod.permission;

import java.util.List;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.config.MainConfig;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.component.service.AbstractService;
import net.pravian.aero.util.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
}
