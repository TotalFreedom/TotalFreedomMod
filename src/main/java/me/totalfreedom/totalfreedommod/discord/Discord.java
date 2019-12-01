package me.totalfreedom.totalfreedommod.discord;

import com.earth2me.essentials.User;
import com.google.common.base.Strings;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.security.auth.login.LoginException;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.pravian.aero.util.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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

            bot = new JDABuilder(AccountType.BOT)
                    .setToken(ConfigEntry.DISCORD_TOKEN.getString())
                    .addEventListeners(new PrivateMessageListener())
                    .addEventListeners(new DiscordToMinecraftListener())
                    .setAutoReconnect(true)
                    .addEventListeners(new ListenerAdapter()
                    {
                        @Override
                        public void onReady(ReadyEvent event)
                        {
                            new StartEvent(event.getJDA()).start();
                        }
                    }).build();
            FLog.info("Discord verification bot has successfully enabled!");
        }
        catch (LoginException e)
        {
            FLog.warning("An invalid token for the discord verification bot, the bot will not enable.");
        }
        catch (IllegalArgumentException e)
        {
            FLog.warning("Discord verification bot failed to start.");
        }
        catch (NoClassDefFoundError e)
        {
            FLog.warning("The JDA plugin is not installed, therefore the bot cannot start.");
            FLog.warning("To resolve this error, please download the latest JDA from: https://github.com/TFPatches/Minecraft-JDA/releases");
        }

    }

    // Do no ask why this is here. I spent two hours trying to make a simple thing work
    public class StartEvent {
        private final JDA api;

        public StartEvent(JDA api) {
            this.api = api;
        }

        public void start()
        {
            messageChatChannel("**Server has started**");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        messageChatChannel("**" + event.getPlayer().getName() + " joined the server" + "**");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        messageChatChannel("**" + event.getPlayer().getName() + " left the server" + "**");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        messageChatChannel("**" + event.getDeathMessage() + "**");
    }

    @Override
    protected void onStart()
    {
        startBot();
    }

    public void messageChatChannel(String message)
    {
        String chat_channel_id = ConfigEntry.DISCORD_CHAT_CHANNEL_ID.getString();
        if (message.contains("@everyone") || message.contains("@here"))
        {
            message = StringUtils.remove(message, "@");
        }
        if (enabled && !chat_channel_id.isEmpty())
        {
            bot.getTextChannelById(chat_channel_id).sendMessage(message).queue();
        }
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
            messageChatChannel("**Server has stopped**");
            bot.shutdown();
        }
        FLog.info("Discord verification bot has successfully shutdown.");
    }

    public boolean sendReport(Player reporter, Player reported, String reason)
    {
        if (ConfigEntry.DISCORD_REPORT_CHANNEL_ID.getString().isEmpty())
        {
            return false;
        }
        if (ConfigEntry.DISCORD_SERVER_ID.getString().isEmpty())
        {
            FLog.severe("No discord server ID was specified in the config, but there is a report channel id.");
            return false;
        }
        Guild server = bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
        if (server == null)
        {
            FLog.severe("The discord server ID specified is invalid, or the bot is not on the server.");
            return false;
        }
        TextChannel channel = server.getTextChannelById(ConfigEntry.DISCORD_REPORT_CHANNEL_ID.getString());
        if (channel == null)
        {
            FLog.severe("The report channel ID specified in the config is invalid");
            return false;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Report for " + reported.getName());
        embedBuilder.setDescription(reason);
        embedBuilder.setFooter("Reported by " + reporter.getName(), "https://minotar.net/helm/" + reporter.getName() + ".png");
        embedBuilder.setTimestamp(Instant.from(ZonedDateTime.now()));
        String location = "World: " + reported.getLocation().getWorld().getName() + ", X: " + reported.getLocation().getBlockX() + ", Y: " + reported.getLocation().getBlockY() + ", Z: " +  reported.getLocation().getBlockZ();
        embedBuilder.addField("Location", location, true);
        embedBuilder.addField("Game Mode", WordUtils.capitalizeFully(reported.getGameMode().name()), true);
        User user = plugin.esb.getEssentialsUser(reported.getName());
        embedBuilder.addField("God Mode", WordUtils.capitalizeFully(String.valueOf(user.isGodModeEnabled())), true);
        if (user.getNickname() != null)
        {
            embedBuilder.addField("Nickname", user.getNickname(), true);
        }
        MessageEmbed embed = embedBuilder.build();
        channel.sendMessage(embed).complete();
        return true;
    }

    public static boolean syncRoles(Admin admin)
    {
        if (admin.getDiscordID() == null)
        {
            return false;
        }

        Guild server = bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
        if (server == null)
        {
            FLog.severe("The discord server ID specified is invalid, or the bot is not on the server.");
            return false;
        }

        Member member = server.getMemberById(admin.getDiscordID());
        if (member == null)
        {
            return false;
        }

        Role superAdminRole = server.getRoleById(ConfigEntry.DISCORD_SUPER_ROLE_ID.getString());
        if (superAdminRole == null)
        {
            FLog.severe("The specified Super Admin role does not exist!");
            return false;
        }
        Role telnetAdminRole = server.getRoleById(ConfigEntry.DISCORD_TELNET_ROLE_ID.getString());
        if (telnetAdminRole == null)
        {
            FLog.severe("The specified Telnet Admin role does not exist!");
            return false;
        }
        Role seniorAdminRole = server.getRoleById(ConfigEntry.DISCORD_SENIOR_ROLE_ID.getString());
        if (seniorAdminRole == null)
        {
            FLog.severe("The specified Senior Admin role does not exist!");
            return false;
        }

        if (!admin.isActive())
        {
            if (member.getRoles().contains(superAdminRole))
            {
                server.removeRoleFromMember(member, superAdminRole).complete();
            }
            if (member.getRoles().contains(telnetAdminRole))
            {
                server.removeRoleFromMember(member, telnetAdminRole).complete();
            }
            if (member.getRoles().contains(seniorAdminRole))
            {
                server.removeRoleFromMember(member, seniorAdminRole).complete();
            }
            return true;
        }

        if (admin.getRank().equals(Rank.SUPER_ADMIN))
        {
            if (!member.getRoles().contains(superAdminRole))
            {
                server.addRoleToMember(member, superAdminRole).complete();
            }
            if (member.getRoles().contains(telnetAdminRole))
            {
                server.removeRoleFromMember(member, telnetAdminRole).complete();
            }
            if (member.getRoles().contains(seniorAdminRole))
            {
                server.removeRoleFromMember(member, seniorAdminRole).complete();
            }
            return true;
        }
        else if (admin.getRank().equals(Rank.TELNET_ADMIN))
        {
            if (!member.getRoles().contains(telnetAdminRole))
            {
                server.addRoleToMember(member, telnetAdminRole).complete();
            }
            if (member.getRoles().contains(superAdminRole))
            {
                server.removeRoleFromMember(member, superAdminRole).complete();
            }
            if (member.getRoles().contains(seniorAdminRole))
            {
                server.removeRoleFromMember(member, seniorAdminRole).complete();
            }
            return true;
        }
        else if (admin.getRank().equals(Rank.SENIOR_ADMIN))
        {
            if (!member.getRoles().contains(seniorAdminRole))
            {
                server.addRoleToMember(member, seniorAdminRole).complete();
            }
            if (member.getRoles().contains(superAdminRole))
            {
                server.removeRoleFromMember(member, superAdminRole).complete();
            }
            if (member.getRoles().contains(telnetAdminRole))
            {
                server.removeRoleFromMember(member, telnetAdminRole).complete();
            }
            return true;
        }

        return false;
    }
}
