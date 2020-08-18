package me.totalfreedom.totalfreedommod.discord;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.rank.Title;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DiscordToMinecraftListener extends ListenerAdapter
{
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String chat_channel_id = ConfigEntry.DISCORD_CHAT_CHANNEL_ID.getString();
        if (event.getMember() != null && !chat_channel_id.isEmpty() && event.getChannel().getId().equals(chat_channel_id))
        {
            if (!event.getAuthor().getId().equals(Discord.bot.getSelfUser().getId()) && !event.getMessage().getContentDisplay().isEmpty())
            {
                Member member = event.getMember();
                String tag = getDisplay(member);
                String message = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "Discord" + ChatColor.DARK_GRAY + "]";
                if (tag != null)
                {
                    message += " " + tag;
                }
                message += " " + ChatColor.RED + ChatColor.stripColor(member.getEffectiveName()) + ChatColor.DARK_GRAY + ": " + ChatColor.RESET + ChatColor.stripColor(event.getMessage().getContentDisplay());
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (TotalFreedomMod.getPlugin().pl.getData(player).doesDisplayDiscord())
                    {
                        player.sendMessage(message);
                    }
                }
                FLog.info(message);
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
        // Admins
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_ADMIN_ROLE_ID.getString())))
        {
            return Rank.ADMIN.getColoredTag();
        }
        // Mods
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_MOD_ROLE_ID.getString())))
        {
            return Rank.MOD.getColoredTag();
        }
        // Trial Mods
        else if (member.getRoles().contains(server.getRoleById(ConfigEntry.DISCORD_TRIAL_MOD_ROLE_ID.getString())))
        {
            return Rank.TRIAL_MOD.getColoredTag();
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