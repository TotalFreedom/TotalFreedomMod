package me.totalfreedom.totalfreedommod.discord;

import com.google.common.base.Strings;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Discord extends FreedomService
{
    public static HashMap<String, Admin> LINK_CODES = new HashMap<>();
    public static HashMap<String, VPlayer> PLAYER_LINK_CODES = new HashMap<String, VPlayer>();
    public static List<String> VERIFY_CODES = new ArrayList();
    public static JDA bot = null;
    public Boolean enabled = false;

    public Discord(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    public void startBot()
    {
        enabled = !Strings.isNullOrEmpty(ConfigEntry.DISCORD_TOKEN.getString());
        if (!enabled)
        {
            return;
        }
        if (bot != null)
        {
            for (Object object : bot.getRegisteredListeners())
            {
                bot.removeEventListener(object);
            }
        }
        try
        {
            bot = new JDABuilder(AccountType.BOT).setToken(ConfigEntry.DISCORD_TOKEN.getString()).addEventListener(new MessageListener()).setAudioEnabled(false).setAutoReconnect(true).buildBlocking();
            FLog.info("Discord verification bot has successfully enabled!");
        }
        catch (LoginException e)
        {
            FLog.warning("An invalid token for the discord verification bot, the bot will not enable.");
        }
        catch (IllegalArgumentException | InterruptedException e)
        {
            FLog.warning("Discord verification bot failed to start.");
        }
    }

    @Override
    protected void onStart()
    {
        startBot();
    }

    public static String getCodeForAdmin(Admin admin)
    {
        for (String code : LINK_CODES.keySet())
        {
            if (LINK_CODES.get(code).equals(admin))
            {
                return code;
            }
        }
        return null;
    }

    public static String getCodeForPlayer(VPlayer playerData)
    {
        for (String code : PLAYER_LINK_CODES.keySet())
        {
            if (PLAYER_LINK_CODES.get(code).equals(playerData))
            {
                return code;
            }
        }
        return null;
    }

    @Override
    protected void onStop()
    {
        if (bot != null)
        {
            bot.shutdown();
        }
        FLog.info("Discord verification bot has successfully shutdown.");
    }
}
