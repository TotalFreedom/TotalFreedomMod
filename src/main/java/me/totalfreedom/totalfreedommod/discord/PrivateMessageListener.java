package me.totalfreedom.totalfreedommod.discord;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilder;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PrivateMessageListener extends ListenerAdapter
{
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event)
    {
        if (!event.getAuthor().getId().equals(Discord.bot.getSelfUser().getId()))
        {
            // Handle link code
            if (event.getMessage().getContentRaw().matches("[0-9][0-9][0-9][0-9][0-9]"))
            {
                String code = event.getMessage().getContentRaw();
                String name;
                if (Discord.ADMIN_LINK_CODES.get(code) != null)
                {
                    Admin admin = Discord.ADMIN_LINK_CODES.get(code);
                    name = admin.getName();
                    admin.setDiscordID(event.getMessage().getAuthor().getId());
                    TotalFreedomMod.plugin().al.save();
                    TotalFreedomMod.plugin().al.updateTables();
                    Discord.ADMIN_LINK_CODES.remove(code);
                    Discord.syncRoles(admin);
                }
                else if (Discord.PLAYER_LINK_CODES.get(code) != null)
                {
                    VPlayer player = Discord.PLAYER_LINK_CODES.get(code);
                    name = player.getName();
                    player.setDiscordId(event.getMessage().getAuthor().getId());
                    player.setEnabled(true);

                    TotalFreedomMod.plugin().pv.saveVerificationData(player);
                    Discord.PLAYER_LINK_CODES.remove(code);
                }
                else if (Discord.MASTER_BUILDER_LINK_CODES.get(code) != null)
                {
                    MasterBuilder masterBuilder = Discord.MASTER_BUILDER_LINK_CODES.get(code);
                    name = masterBuilder.getName();
                    masterBuilder.setDiscordID(event.getMessage().getAuthor().getId());
                    TotalFreedomMod.plugin().mbl.save();
                    TotalFreedomMod.plugin().mbl.updateTables();
                    Discord.MASTER_BUILDER_LINK_CODES.remove(code);
                }
                else
                {
                    return;
                }
                event.getChannel().sendMessage("Link successful. Now this Discord account is linked with your Minecraft account **" + name + "**.\n"
                        + "Now when you are an impostor on the server, you may use `/verify` to verify.").complete();
            }
        }
    }
}
