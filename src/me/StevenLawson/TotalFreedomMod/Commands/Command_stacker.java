package me.StevenLawson.TotalFreedomMod.Commands;

public class Command_stacker extends TFM_Command
{
//    @Override
//    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
//    {
//        if (senderIsConsole)
//        {
//            sender.sendMessage(TotalFreedomMod.NOT_FROM_CONSOLE);
//        }
//        else if (TFM_Util.isUserSuperadmin(sender, plugin))
//        {
//            if (args.length >= 1)
//            {
//                if (TFM_Util.isStopCommand(args[0]))
//                {
//                    for (World world : Bukkit.getWorlds())
//                    {
//                        for (LivingEntity entity : world.getLivingEntities())
//                        {
//                            entity.leaveVehicle();                            
//                        }
//                    }
//                    return true;
//                }
//            }
//
//            LivingEntity parent = sender_p;
//            for (LivingEntity entity : sender_p.getWorld().getLivingEntities())
//            {
//                if (parent != null)
//                {
//                    parent.setPassenger(entity);
//                }
//
//                parent = entity;
//            }
//        }
//        else
//        {
//            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
//        }
//
//        return true;
//    }
}
