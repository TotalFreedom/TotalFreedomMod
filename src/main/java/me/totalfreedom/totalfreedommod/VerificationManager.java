package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.admin.Verify;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.player.VerifyStage;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.component.service.AbstractService;
import net.pravian.aero.util.Ips;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class VerificationManager extends AbstractService<TotalFreedomMod> {

    public VerificationManager(TotalFreedomMod plugin)
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

    @EventHandler(priority = EventPriority.LOWEST) // LOWEST == execute first
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FPlayer fPlayer = plugin.pl.getPlayerSync(player);
        VerifyStage stage = fPlayer.getVerifyStage();


        if (stage == VerifyStage.NONE || stage == null) {
            return;
        }

        // Make sure passwords and verification codes don't show up in chat
        event.setCancelled(true);
        event.setMessage("");
        event.setFormat("");

        final Admin admin = plugin.al.getEntryByName(player.getName());
        final Verify verify = admin.getVerify();

        // Exit
        if (event.getMessage().equals("exit")) {
            if (stage == VerifyStage.SET_TOTP_CONFIRM) {
                verify.clearTotpSecret(); // Exiting whilst setting OTP code
            }

            fPlayer.setVerifyStage(VerifyStage.NONE);
            veriMsg(player, "Exit.");
        }


        // Verify password
        if (stage == VerifyStage.VERIFY_PASSWORD) {
            if (!verify.verifyPassword(event.getMessage())) {
                veriMsg(player, "Incorrect password. Type 'exit' at any time to stop.");
                return;
            }

            veriMsg(player, "Please enter the verification code from your app:");
            fPlayer.setVerifyStage(VerifyStage.VERIFY_TOTP);
            return;
        }

        // Verify OTP
        if (stage == VerifyStage.VERIFY_TOTP) {
            if (!verify.verifyTotp(event.getMessage())) {
                veriMsg(player, "Incorrect verification code. Type 'exit' at any time to stop.");
                return;
            }

            veriMsg(player, "Verified.");
            fPlayer.setVerifyStage(VerifyStage.NONE);

            FUtil.adminAction("TotalFreedomMod", "Verifying " + player.getName() + " as an admin", true);
            admin.addIp(Ips.getIp(player));
            plugin.al.save();
            plugin.al.updateTables();
            return;
        }

        // Verify old password
        if (stage == VerifyStage.SET_PASSWORD_VERIFY_OLD) {
            if (!verify.verifyPassword(event.getMessage())) {
                veriMsg(player, "Incorrect password. Type 'exit' at any time to stop.");
                return;
            }

            veriMsg(player, "Type 'exit' to cancel");
            veriMsg(player, "Please enter your NEW password:");
            fPlayer.setVerifyStage(VerifyStage.SET_PASSWORD);
            return;
        }

        // Set password
        if (stage == VerifyStage.SET_PASSWORD) {
            fPlayer.setNewVerifyPassword(event.getMessage());
            veriMsg(player, "Type 'exit' to cancel");
            veriMsg(player, "Please enter your NEW password:");
            return;
        }

        // Set password confirm
        if (stage == VerifyStage.SET_PASSWORD_CONFIRM) {
            if (!fPlayer.getNewVerifyPassword().equals(event.getMessage())) {
                veriMsg(player, "Type 'exit' to cancel");
                veriMsg(player, "Passwords do not match!");
                veriMsg(player, "Please enter your NEW password:");
                fPlayer.setNewVerifyPassword(null);
                fPlayer.setVerifyStage(VerifyStage.SET_PASSWORD);
                return;
            }

            verify.setPassword(fPlayer.getNewVerifyPassword());
            fPlayer.setNewVerifyPassword(null);
            fPlayer.setVerifyStage(VerifyStage.NONE);
            veriMsg(player, "New password set successfully!");
            return;
        }

        // Set TOTP confirm
        if (stage == VerifyStage.SET_TOTP_CONFIRM) {
            if (!verify.verifyTotp(event.getMessage())) {
                veriMsg(player, "Type 'exit' to cancel");
                veriMsg(player, "Incorrect verification code.");
                return;
            }

            veriMsg(player, "OTP generator set up successfully!");
            fPlayer.setVerifyStage(VerifyStage.NONE);
        }

        FLog.warning("Unknown verification stage: " + stage + " for player " + player.getName());
        fPlayer.setVerifyStage(VerifyStage.NONE);
    }

    public void veriMsg(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.AQUA + "[" + ChatColor.GREEN + "Verify" + ChatColor.AQUA + "] " + ChatColor.RED + message);
    }

}
