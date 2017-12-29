package me.totalfreedom.totalfreedommod.discord;

import com.google.common.base.Strings;
import me.totalfreedom.totalfreedommod.discord.MessageListener;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import javax.security.auth.login.LoginException;

public class Discord extends FreedomService
{
    public static HashMap<String, Admin> LINK_CODES = new HashMap<>();
    public static List<String> VERIFY_CODES = new ArrayList();
    public static JDA bot = null;
    public static Boolean enabled = false;

    public Discord(TotalFreedomMod plugin)
    {
        super(plugin);
    }

     public void startBot()
     {
        if (!Strings.isNullOrEmpty(ConfigEntry.DISCORD_TOKEN.getString()))
        {
            enabled = true;
        }
        else
        {
            enabled = false;
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
        catch (RateLimitedException e)
        {
            FLog.warning("The discord verification bot was ratelimited trying to login, please try again later.");
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
        for (String code: LINK_CODES.keySet())
        {
            if (LINK_CODES.get(code).equals(admin))
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
