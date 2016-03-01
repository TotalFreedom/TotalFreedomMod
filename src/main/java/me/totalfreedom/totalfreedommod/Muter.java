package me.totalfreedom.totalfreedommod;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FSync;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Muter extends FreedomService
{

    // TODO: Match actual commands
    public static final List<String> MUTE_COMMANDS = Arrays.asList(StringUtils.split("say,me,msg,m,tell,r,reply,mail,email", ","));

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

        String command = event.getMessage();
        if (plugin.al.isAdmin(player))
        {
            fPlayer.setMuted(false);
            return;
        }

        // TODO: Find match actual command, instead of label
        for (String commandName : MUTE_COMMANDS)
        {
            if (Pattern.compile("^/" + commandName.toLowerCase() + " ").matcher(command.toLowerCase()).find())
            {
                player.sendMessage(ChatColor.RED + "That command is blocked while you are muted.");
                event.setCancelled(true);
                return;
            }
        }

        if (ConfigEntry.ENABLE_PREPROCESS_LOG.getBoolean())
        {
            FLog.info(String.format("[PREPROCESS_COMMAND] %s(%s): %s", player.getName(), ChatColor.stripColor(player.getDisplayName()), command), true);
        }
    }

}
