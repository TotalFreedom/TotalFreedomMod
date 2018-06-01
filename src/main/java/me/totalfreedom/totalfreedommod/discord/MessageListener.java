package me.totalfreedom.totalfreedommod.discord;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter
{
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        if (!event.getAuthor().getId().equals(Discord.bot.getSelfUser().getId()))
        {

            // Handle link code
            if (event.getMessage().getContentRaw().matches("[0-9][0-9][0-9][0-9][0-9]"))
            {
                String code = event.getMessage().getContentRaw();
                if (Discord.LINK_CODES.get(code) != null)
                {
                    Admin admin = Discord.LINK_CODES.get(code);
                    admin.setDiscordID(event.getMessage().getAuthor().getId());
                    Discord.LINK_CODES.remove(code);
                    event.getChannel().sendMessage("Link successful. Now this Discord account is linked with the Minecraft account `" + admin.getName() + "`.\n "
                            + "Now when you are an impostor on the server, you may use `/verify` to verify.").complete();
                }
                if (Discord.PLAYER_LINK_CODES.get(code) != null)
                {
                    VPlayer player = Discord.PLAYER_LINK_CODES.get(code);
                    player.setDiscordId(event.getMessage().getAuthor().getId());

                    TotalFreedomMod.plugin().pv.saveVerificationData(player);
                    Discord.PLAYER_LINK_CODES.remove(code);
                    event.getChannel().sendMessage("Link successful. Now this Discord account is linked with the Minecraft account `" + player.getName() + "`.\n "
                            + "Now when you are an impostor on the server, you may use `/verify` to verify.").complete();
                }
            }
        }
    }
}
