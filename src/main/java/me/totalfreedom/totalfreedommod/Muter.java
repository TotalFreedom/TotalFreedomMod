package me.totalfreedom.totalfreedommod;

import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FSync;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Muter extends FreedomService
{

    public static final List<String> MUTE_COMMANDS = Arrays.asList(StringUtils.split("say,me,msg,tell,reply,mail", ","));

    public Muter(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event)
    {
        FPlayer fPlayer = plugin.pl.getPlayerSync(event.getPlayer());

        if (!fPlayer.isMuted())
        {
            return;
        }

        if (plugin.al.isAdminSync(event.getPlayer()))
        {
            fPlayer.setMuted(false);
            return;
        }

        FSync.playerMsg(event.getPlayer(), ChatColor.RED + "You are muted, STFU! - You will be unmuted in 5 minutes.");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {

        Player player = event.getPlayer();
        FPlayer fPlayer = plugin.pl.getPlayer(event.getPlayer());

        // Block commands if player is muted
        if (!fPlayer.isMuted())
        {
            return;
        }

        String message = event.getMessage();
        if (plugin.al.isAdmin(player))
        {
            fPlayer.setMuted(false);
            return;
        }

        String cmdName = message.split(" ")[0].toLowerCase();
        if (cmdName.startsWith("/"))
        {
            cmdName = cmdName.substring(1);
        }

        Command command = server.getPluginCommand(cmdName);
        if (command != null)
        {
            cmdName = command.getName().toLowerCase();
        }

        if (MUTE_COMMANDS.contains(cmdName))
        {
            player.sendMessage(ChatColor.RED + "That command is blocked while you are muted.");
            event.setCancelled(true);
            return;
        }

        // TODO: Should this go here?
        if (ConfigEntry.ENABLE_PREPROCESS_LOG.getBoolean())
        {
            FLog.info(String.format("[PREPROCESS_COMMAND] %s(%s): %s", player.getName(), ChatColor.stripColor(player.getDisplayName()), message), true);
        }
    }

}
