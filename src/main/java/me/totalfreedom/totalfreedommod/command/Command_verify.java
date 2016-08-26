package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.admin.Verify;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.player.VerifyStage;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

@CommandPermissions(level = Rank.IMPOSTOR, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Verify yourself, set verification details", usage = "/<command> [set <password | otp>]", aliases = "ver")
public class Command_verify extends FreedomCommand
{

    @Override
    protected boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FPlayer fPlayer = getFPlayer(sender_p);

        // Verifcation sequence
        if (plugin.al.isAdminImpostor(sender_p))
        {
            final Admin admin = plugin.al.getEntryByName(sender.getName());

            if (!admin.getVerify().hasPassword() || !admin.getVerify().hasTotpSecret())
            {
                plugin.vm.veriMsg(sender, "You can not verify because you have no verfication information set!");
                return true;
            }

            if (args.length != 0)
            {
                return false;
            }

            fPlayer.setVerifyStage(VerifyStage.VERIFY_PASSWORD);
            plugin.vm.veriMsg(sender, "Please enter your password:");
            return true;
        }

        if (!isAdmin(sender))
        {
            return noPerms();
        }

        final Admin admin = getAdmin(sender);
        final Verify verify = admin.getVerify();

        if (args.length == 0)
        {
            msg("TotalFreedomMod verification system", ChatColor.GOLD);
            StringBuilder line = new StringBuilder();
            while (line.length() < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH)
            {
                line.append("-");
            }
            msg(line.toString());

            msg("By setting a password and using a OTP code generator,", ChatColor.GOLD);
            msg("you can verify yourself if you become an impostor.", ChatColor.GOLD);
            msg("Note: For this to work, you need to have a OTP generator", ChatColor.GOLD);
            msg("such as Google Authenticator installed.", ChatColor.GOLD);
            msg("");
            msg("Password set: " + (verify.hasPassword() ? ChatColor.GREEN + "yes" : ChatColor.RED + "no"));
            msg("OTP set: " + (verify.hasTotpSecret() ? ChatColor.GREEN + "yes" : ChatColor.RED + "no"));
            msg("");
            msg("You can set your password and OTP code generator with /verify set <password | otp>");
        }

        if (args.length != 1 || !args[0].equals("set"))
        {
            return false;
        }

        if (args[1].equals("password"))
        {
            if (verify.hasPassword())
            {
                plugin.vm.veriMsg(sender, "Type 'exit' to cancel");
                plugin.vm.veriMsg(sender, "Please enter your CURRENT password:");
                fPlayer.setVerifyStage(VerifyStage.SET_PASSWORD_VERIFY_OLD);
                return true;
            }

            plugin.vm.veriMsg(sender, "Type 'exit' to cancel");
            plugin.vm.veriMsg(sender, "Please enter your NEW password:");
            fPlayer.setVerifyStage(VerifyStage.SET_PASSWORD);
            return true;
        }

        if (args[1].equals("otp"))
        {
            if (!verify.hasPassword())
            {
                plugin.vm.veriMsg(sender, "You can only set your OTP generator after you've set your password.");
                return true;
            }

            if (verify.hasTotpSecret())
            {
                plugin.vm.veriMsg(sender, "You already have generator secret set!");
                plugin.vm.veriMsg(sender, "Setting your generator secret will invalidate your old one!");
            }

            plugin.vm.veriMsg(sender, "Your generator secret is: " + ChatColor.UNDERLINE + verify.generateTotpSecret());
            plugin.vm.veriMsg(sender, "Please enter this code into your OTP app.");
            plugin.vm.veriMsg(sender, "Type 'exit' to cancel");
            plugin.vm.veriMsg(sender, "Please enter a the code received from your app:");
            fPlayer.setVerifyStage(VerifyStage.SET_TOTP_CONFIRM);
            return true;
        }

        return false;
    }

}
