package me.unraveledmc.unraveledmcmod.discord;

import me.unraveledmc.unraveledmcmod.Discord;
import me.unraveledmc.unraveledmcmod.admin.Admin;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter
{
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        if (!event.getAuthor().getId().equals(Discord.bot.getSelfInfo().getId()))
        {
            
            // Handle link code
            if (event.getMessage().getRawContent().matches("[0-9][0-9][0-9][0-9][0-9]"))
            {
                String code = event.getMessage().getRawContent();
                if (Discord.LINK_CODES.get(code) != null)
                {
                    Admin admin = Discord.LINK_CODES.get(code);
                    admin.setDiscordID(event.getMessage().getAuthor().getId());
                    Discord.LINK_CODES.remove(code);
                    Discord.sendMessage(event.getChannel(), "Successfully linked this discord account to the minecraft account `" + admin.getName() + "`");
                }
            }
        }
    }
}
