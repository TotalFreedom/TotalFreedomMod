package me.totalfreedom.totalfreedommod.discord;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.rank.Title;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.ChatColor;

public class DiscordToMinecraftListener extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String chat_channel_id = ConfigEntry.DISCORD_CHAT_CHANNEL_ID.getString();
        if (event.getMember() != null && !chat_channel_id.isEmpty() && event.getChannel().getId().equals(chat_channel_id))
        {
            if (!event.getAuthor().getId().equals(Discord.bot.getSelfUser().getId()))
            {
                Member member = event.getMember();
                String tag = getDisplay(member);
                String message = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "Discord" + ChatColor.DARK_GRAY + "]";
                if (tag != null)
                {
                    message += " " + tag;
                }
                message += " " + ChatColor.RED + ChatColor.stripColor(member.getEffectiveName()) + ChatColor.DARK_GRAY + ": " + ChatColor.RESET + ChatColor.stripColor(event.getMessage().getContentDisplay());
                FUtil.bcastMsg(message);
            }
        }
    }

    public String getDisplay(Member member)
    {
        Guild server = Discord.bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
        // Server Owner
        if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_SERVER_OWNER_ROLE_ID.getString())))
        {
            return Title.OWNER.getColoredTag();
        }
        // Developers
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_DEVELOPER_ROLE_ID.getString())))
        {
            return Title.DEVELOPER.getColoredTag();
        }
        // Executives
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_EXECUTIVE_ROLE_ID.getString())))
        {
            return Title.EXECUTIVE.getColoredTag();
        }
        // Assistant Executives
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_ASSISTANT_EXECUTIVE_ROLE_ID.getString())))
        {
            return Title.ASSISTANT_EXECUTIVE.getColoredTag();
        }
        // Senior Admins
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_SENIOR_ROLE_ID.getString())))
        {
            return Rank.SENIOR_ADMIN.getColoredTag();
        }
        // Telnet Admins
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_TELNET_ROLE_ID.getString())))
        {
            return Rank.TELNET_ADMIN.getColoredTag();
        }
        // Super Admins
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_SUPER_ROLE_ID.getString())))
        {
            return Rank.SUPER_ADMIN.getColoredTag();
        }
        // Master Builders
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_MASTER_BUILDER_ROLE_ID.getString())))
        {
            return Title.MASTER_BUILDER.getColoredTag();
        }
        // None
        else
        {
            return null;
        }
    }

}
