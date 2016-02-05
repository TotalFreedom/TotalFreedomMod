package me.StevenLawson.TotalFreedomMod.Commands;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "A command for senior admins only", usage = "/<command> [add/saadd <player> | del <player> | suspend <player> | teston | testoff]")
public class Command_sys extends TFM_Command {

    public Command_sys() {
        // <editor-fold defaultstate="collapsed" desc="Compiled Code">
        /* 0: aload_0
         * 1: invokespecial me/StevenLawson/TotalFreedomMod/Commands/TFM_Command."<init>":()V
         * 4: return
         *  */
        // </editor-fold>
    }

    public boolean run(org.bukkit.command.CommandSender sender, org.bukkit.entity.Player sender_p, org.bukkit.command.Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        // <editor-fold defaultstate="collapsed" desc="Compiled Code">
        /* 0: aload_1
         * 1: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_AdminList.isSuperAdmin:(Lorg/bukkit/command/CommandSender;)Z
         * 4: ifne          64
         * 7: aload_1
         * 8: getstatic     me/StevenLawson/TotalFreedomMod/Commands/TFM_Command.MSG_NO_PERMS:Ljava/lang/String;
         * 11: invokeinterface org/bukkit/command/CommandSender.sendMessage:(Ljava/lang/String;)V
         * 16: new           java/lang/StringBuilder
         * 19: dup
         * 20: invokespecial java/lang/StringBuilder."<init>":()V
         * 23: getstatic     org/bukkit/ChatColor.RED:Lorg/bukkit/ChatColor;
         * 26: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
         * 29: ldc           WARNING:
         * 31: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 34: aload_1
         * 35: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 40: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 43: ldc            has attempted to use a system admin only command. System administration team has been alerted.
         * 45: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 48: invokevirtual java/lang/StringBuilder.toString:()Ljava/lang/String;
         * 51: invokestatic  org/bukkit/Bukkit.broadcastMessage:(Ljava/lang/String;)I
         * 54: pop
         * 55: aload_2
         * 56: dconst_0
         * 57: invokeinterface org/bukkit/entity/Player.setHealth:(D)V
         * 62: iconst_1
         * 63: ireturn
         * 64: aload_1
         * 65: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 70: ldc           rovertdude
         * 72: invokevirtual java/lang/String.equals:(Ljava/lang/Object;)Z
         * 75: ifne          235
         * 78: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_SYSADMINS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry;
         * 81: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 84: aload_1
         * 85: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 90: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 95: ifne          235
         * 98: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_STAFFMANS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry;
         * 101: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 104: aload_1
         * 105: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 110: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 115: ifne          235
         * 118: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_SENIORS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_SuperAdmin;
         * 121: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 124: aload_1
         * 125: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 130: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 135: ifne          235
         * 138: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_COOWNERS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry;
         * 141: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 144: aload_1
         * 145: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 150: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 155: ifne          235
         * 158: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_OWNERS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry;
         * 161: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 164: aload_1
         * 165: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 170: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 175: ifne          235
         * 178: aload_1
         * 179: getstatic     me/StevenLawson/TotalFreedomMod/Commands/TFM_Command.MSG_NO_PERMS:Ljava/lang/String;
         * 182: invokeinterface org/bukkit/command/CommandSender.sendMessage:(Ljava/lang/String;)V
         * 187: new           java/lang/StringBuilder
         * 190: dup
         * 191: invokespecial java/lang/StringBuilder."<init>":()V
         * 194: getstatic     org/bukkit/ChatColor.RED:Lorg/bukkit/ChatColor;
         * 197: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
         * 200: ldc           WARNING:
         * 202: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 205: aload_1
         * 206: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 211: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 214: ldc            has attempted to use a system admin only command. System administration team has been alerted.
         * 216: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 219: invokevirtual java/lang/StringBuilder.toString:()Ljava/lang/String;
         * 222: invokestatic  org/bukkit/Bukkit.broadcastMessage:(Ljava/lang/String;)I
         * 225: pop
         * 226: aload_2
         * 227: dconst_0
         * 228: invokeinterface org/bukkit/entity/Player.setHealth:(D)V
         * 233: iconst_1
         * 234: ireturn
         * 235: aload         5
         * 237: arraylength
         * 238: ifne          243
         * 241: iconst_0
         * 242: ireturn
         * 243: aload         5
         * 245: iconst_0
         * 246: aaload
         * 247: invokevirtual java/lang/String.toLowerCase:()Ljava/lang/String;
         * 250: ldc           add
         * 252: invokevirtual java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
         * 255: ifeq          377
         * 258: aload_0
         * 259: aload         5
         * 261: iconst_1
         * 262: aaload
         * 263: invokevirtual me/StevenLawson/TotalFreedomMod/Commands/Command_sys.getPlayer:(Ljava/lang/String;)Lorg/bukkit/entity/Player;
         * 266: astore        7
         * 268: aload         7
         * 270: ifnonnull     284
         * 273: aload_1
         * 274: getstatic     me/StevenLawson/TotalFreedomMod/Commands/TFM_Command.PLAYER_NOT_FOUND:Ljava/lang/String;
         * 277: invokeinterface org/bukkit/command/CommandSender.sendMessage:(Ljava/lang/String;)V
         * 282: iconst_1
         * 283: ireturn
         * 284: aload_1
         * 285: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 290: new           java/lang/StringBuilder
         * 293: dup
         * 294: invokespecial java/lang/StringBuilder."<init>":()V
         * 297: ldc           Adding
         * 299: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 302: aload         5
         * 304: iconst_1
         * 305: aaload
         * 306: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 309: ldc            to the superadmin list
         * 311: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 314: invokevirtual java/lang/StringBuilder.toString:()Ljava/lang/String;
         * 317: iconst_1
         * 318: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_Util.adminAction:(Ljava/lang/String;Ljava/lang/String;Z)V
         * 321: aload         7
         * 323: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_AdminList.addSuperadmin:(Lorg/bukkit/OfflinePlayer;)V
         * 326: aload         7
         * 328: invokeinterface org/bukkit/entity/Player.isOnline:()Z
         * 333: ifeq          375
         * 336: aload         7
         * 338: invokeinterface org/bukkit/entity/Player.getPlayer:()Lorg/bukkit/entity/Player;
         * 343: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_PlayerData.getPlayerData:(Lorg/bukkit/entity/Player;)Lme/StevenLawson/TotalFreedomMod/TFM_PlayerData;
         * 346: astore        8
         * 348: aload         8
         * 350: invokevirtual me/StevenLawson/TotalFreedomMod/TFM_PlayerData.isFrozen:()Z
         * 353: ifeq          375
         * 356: aload         8
         * 358: iconst_0
         * 359: invokevirtual me/StevenLawson/TotalFreedomMod/TFM_PlayerData.setFrozen:(Z)V
         * 362: aload_0
         * 363: aload         7
         * 365: invokeinterface org/bukkit/entity/Player.getPlayer:()Lorg/bukkit/entity/Player;
         * 370: ldc           You have been unfrozen.
         * 372: invokevirtual me/StevenLawson/TotalFreedomMod/Commands/Command_sys.playerMsg:(Lorg/bukkit/command/CommandSender;Ljava/lang/String;)V
         * 375: iconst_1
         * 376: ireturn
         * 377: aload         5
         * 379: iconst_0
         * 380: aaload
         * 381: invokevirtual java/lang/String.toLowerCase:()Ljava/lang/String;
         * 384: ldc           saadd
         * 386: invokevirtual java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
         * 389: ifeq          511
         * 392: aload_0
         * 393: aload         5
         * 395: iconst_1
         * 396: aaload
         * 397: invokevirtual me/StevenLawson/TotalFreedomMod/Commands/Command_sys.getPlayer:(Ljava/lang/String;)Lorg/bukkit/entity/Player;
         * 400: astore        7
         * 402: aload         7
         * 404: ifnonnull     418
         * 407: aload_1
         * 408: getstatic     me/StevenLawson/TotalFreedomMod/Commands/TFM_Command.PLAYER_NOT_FOUND:Ljava/lang/String;
         * 411: invokeinterface org/bukkit/command/CommandSender.sendMessage:(Ljava/lang/String;)V
         * 416: iconst_1
         * 417: ireturn
         * 418: aload_1
         * 419: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 424: new           java/lang/StringBuilder
         * 427: dup
         * 428: invokespecial java/lang/StringBuilder."<init>":()V
         * 431: ldc           Adding
         * 433: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 436: aload         5
         * 438: iconst_1
         * 439: aaload
         * 440: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 443: ldc            to the superadmin list
         * 445: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 448: invokevirtual java/lang/StringBuilder.toString:()Ljava/lang/String;
         * 451: iconst_1
         * 452: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_Util.adminAction:(Ljava/lang/String;Ljava/lang/String;Z)V
         * 455: aload         7
         * 457: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_AdminList.addSuperadmin:(Lorg/bukkit/OfflinePlayer;)V
         * 460: aload         7
         * 462: invokeinterface org/bukkit/entity/Player.isOnline:()Z
         * 467: ifeq          509
         * 470: aload         7
         * 472: invokeinterface org/bukkit/entity/Player.getPlayer:()Lorg/bukkit/entity/Player;
         * 477: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_PlayerData.getPlayerData:(Lorg/bukkit/entity/Player;)Lme/StevenLawson/TotalFreedomMod/TFM_PlayerData;
         * 480: astore        8
         * 482: aload         8
         * 484: invokevirtual me/StevenLawson/TotalFreedomMod/TFM_PlayerData.isFrozen:()Z
         * 487: ifeq          509
         * 490: aload         8
         * 492: iconst_0
         * 493: invokevirtual me/StevenLawson/TotalFreedomMod/TFM_PlayerData.setFrozen:(Z)V
         * 496: aload_0
         * 497: aload         7
         * 499: invokeinterface org/bukkit/entity/Player.getPlayer:()Lorg/bukkit/entity/Player;
         * 504: ldc           You have been unfrozen.
         * 506: invokevirtual me/StevenLawson/TotalFreedomMod/Commands/Command_sys.playerMsg:(Lorg/bukkit/command/CommandSender;Ljava/lang/String;)V
         * 509: iconst_1
         * 510: ireturn
         * 511: aload         5
         * 513: iconst_0
         * 514: aaload
         * 515: invokevirtual java/lang/String.toLowerCase:()Ljava/lang/String;
         * 518: ldc           del
         * 520: invokevirtual java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
         * 523: ifeq          740
         * 526: aload_0
         * 527: aload         5
         * 529: iconst_1
         * 530: aaload
         * 531: invokevirtual me/StevenLawson/TotalFreedomMod/Commands/Command_sys.getPlayer:(Ljava/lang/String;)Lorg/bukkit/entity/Player;
         * 534: astore        7
         * 536: aload         7
         * 538: ifnonnull     552
         * 541: aload_1
         * 542: getstatic     me/StevenLawson/TotalFreedomMod/Commands/TFM_Command.PLAYER_NOT_FOUND:Ljava/lang/String;
         * 545: invokeinterface org/bukkit/command/CommandSender.sendMessage:(Ljava/lang/String;)V
         * 550: iconst_1
         * 551: ireturn
         * 552: aload_1
         * 553: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 558: new           java/lang/StringBuilder
         * 561: dup
         * 562: invokespecial java/lang/StringBuilder."<init>":()V
         * 565: ldc           Removing
         * 567: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 570: aload         5
         * 572: iconst_1
         * 573: aaload
         * 574: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 577: ldc            from the superadmin list
         * 579: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 582: invokevirtual java/lang/StringBuilder.toString:()Ljava/lang/String;
         * 585: iconst_1
         * 586: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_Util.adminAction:(Ljava/lang/String;Ljava/lang/String;Z)V
         * 589: aload         7
         * 591: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_AdminList.removeSuperadmin:(Lorg/bukkit/OfflinePlayer;)V
         * 594: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_OWNERS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry;
         * 597: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 600: aload_1
         * 601: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 606: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 611: ifne          738
         * 614: getstatic     me/StevenLawson/TotalFreedomMod/TFM_Util.DEVELOPERS:Ljava/util/List;
         * 617: aload_1
         * 618: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 623: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 628: ifne          738
         * 631: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_HEADADMINS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry;
         * 634: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 637: aload_1
         * 638: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 643: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 648: ifne          738
         * 651: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_STAFFMANS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry;
         * 654: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 657: aload_1
         * 658: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 663: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 668: ifne          738
         * 671: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_COOWNERS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry;
         * 674: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 677: aload_1
         * 678: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 683: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 688: ifne          738
         * 691: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_SYSADMINS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry;
         * 694: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 697: aload_1
         * 698: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 703: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 708: ifne          738
         * 711: aload_1
         * 712: new           java/lang/StringBuilder
         * 715: dup
         * 716: invokespecial java/lang/StringBuilder."<init>":()V
         * 719: getstatic     org/bukkit/ChatColor.RED:Lorg/bukkit/ChatColor;
         * 722: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
         * 725: ldc           You can suspend the player instead by doing /sys suspend <player>
         * 727: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 730: invokevirtual java/lang/StringBuilder.toString:()Ljava/lang/String;
         * 733: invokeinterface org/bukkit/command/CommandSender.sendMessage:(Ljava/lang/String;)V
         * 738: iconst_1
         * 739: ireturn
         * 740: aload         5
         * 742: iconst_0
         * 743: aaload
         * 744: invokevirtual java/lang/String.toLowerCase:()Ljava/lang/String;
         * 747: ldc           suspend
         * 749: invokevirtual java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
         * 752: ifeq          960
         * 755: getstatic     me/StevenLawson/TotalFreedomMod/TFM_Util.DEVELOPERS:Ljava/util/List;
         * 758: aload_1
         * 759: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 764: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 769: ifne          803
         * 772: getstatic     me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.SERVER_OWNERS:Lme/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry;
         * 775: invokevirtual me/StevenLawson/TotalFreedomMod/Config/TFM_ConfigEntry.getList:()Ljava/util/List;
         * 778: aload_1
         * 779: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 784: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
         * 789: ifne          803
         * 792: aload_1
         * 793: getstatic     me/StevenLawson/TotalFreedomMod/Commands/TFM_Command.MSG_NO_PERMS:Ljava/lang/String;
         * 796: invokeinterface org/bukkit/command/CommandSender.sendMessage:(Ljava/lang/String;)V
         * 801: iconst_1
         * 802: ireturn
         * 803: aload_0
         * 804: aload         5
         * 806: iconst_1
         * 807: aaload
         * 808: invokevirtual me/StevenLawson/TotalFreedomMod/Commands/Command_sys.getPlayer:(Ljava/lang/String;)Lorg/bukkit/entity/Player;
         * 811: astore        7
         * 813: aload         7
         * 815: ifnonnull     829
         * 818: aload_1
         * 819: getstatic     me/StevenLawson/TotalFreedomMod/Commands/TFM_Command.PLAYER_NOT_FOUND:Ljava/lang/String;
         * 822: invokeinterface org/bukkit/command/CommandSender.sendMessage:(Ljava/lang/String;)V
         * 827: iconst_1
         * 828: ireturn
         * 829: aload_1
         * 830: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 835: new           java/lang/StringBuilder
         * 838: dup
         * 839: invokespecial java/lang/StringBuilder."<init>":()V
         * 842: ldc           Suspending
         * 844: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 847: aload         5
         * 849: iconst_1
         * 850: aaload
         * 851: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 854: invokevirtual java/lang/StringBuilder.toString:()Ljava/lang/String;
         * 857: iconst_1
         * 858: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_Util.adminAction:(Ljava/lang/String;Ljava/lang/String;Z)V
         * 861: aload         7
         * 863: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_AdminList.removeSuperadmin:(Lorg/bukkit/OfflinePlayer;)V
         * 866: aload         7
         * 868: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_PlayerList.getEntry:(Lorg/bukkit/entity/Player;)Lme/StevenLawson/TotalFreedomMod/TFM_Player;
         * 871: invokevirtual me/StevenLawson/TotalFreedomMod/TFM_Player.getIps:()Ljava/util/List;
         * 874: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
         * 879: astore        8
         * 881: aload         8
         * 883: invokeinterface java/util/Iterator.hasNext:()Z
         * 888: ifeq          925
         * 891: aload         8
         * 893: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
         * 898: checkcast     java/lang/String
         * 901: astore        9
         * 903: new           me/StevenLawson/TotalFreedomMod/TFM_Ban
         * 906: dup
         * 907: aload         9
         * 909: aload         7
         * 911: invokeinterface org/bukkit/entity/Player.getName:()Ljava/lang/String;
         * 916: invokespecial me/StevenLawson/TotalFreedomMod/TFM_Ban."<init>":(Ljava/lang/String;Ljava/lang/String;)V
         * 919: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_BanManager.addIpBan:(Lme/StevenLawson/TotalFreedomMod/TFM_Ban;)V
         * 922: goto          881
         * 925: aload         7
         * 927: invokestatic  me/StevenLawson/TotalFreedomMod/TFM_BanManager.addUuidBan:(Lorg/bukkit/entity/Player;)V
         * 930: aload         7
         * 932: invokeinterface org/bukkit/entity/Player.closeInventory:()V
         * 937: aload         7
         * 939: invokeinterface org/bukkit/entity/Player.getInventory:()Lorg/bukkit/inventory/PlayerInventory;
         * 944: invokeinterface org/bukkit/inventory/PlayerInventory.clear:()V
         * 949: aload         7
         * 951: ldc           You have been suspended. Check the forums for more information.
         * 953: invokeinterface org/bukkit/entity/Player.kickPlayer:(Ljava/lang/String;)V
         * 958: iconst_1
         * 959: ireturn
         * 960: aload         5
         * 962: iconst_0
         * 963: aaload
         * 964: invokevirtual java/lang/String.toLowerCase:()Ljava/lang/String;
         * 967: ldc           teston
         * 969: invokevirtual java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
         * 972: ifeq          1016
         * 975: new           java/lang/StringBuilder
         * 978: dup
         * 979: invokespecial java/lang/StringBuilder."<init>":()V
         * 982: getstatic     org/bukkit/ChatColor.RED:Lorg/bukkit/ChatColor;
         * 985: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
         * 988: ldc           WARNING:
         * 990: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 993: aload_1
         * 994: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 999: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 1002: ldc            has started testing on this server.
         * 1004: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 1007: invokevirtual java/lang/StringBuilder.toString:()Ljava/lang/String;
         * 1010: invokestatic  org/bukkit/Bukkit.broadcastMessage:(Ljava/lang/String;)I
         * 1013: pop
         * 1014: iconst_1
         * 1015: ireturn
         * 1016: aload         5
         * 1018: iconst_0
         * 1019: aaload
         * 1020: invokevirtual java/lang/String.toLowerCase:()Ljava/lang/String;
         * 1023: ldc           testoff
         * 1025: invokevirtual java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
         * 1028: ifeq          1067
         * 1031: new           java/lang/StringBuilder
         * 1034: dup
         * 1035: invokespecial java/lang/StringBuilder."<init>":()V
         * 1038: getstatic     org/bukkit/ChatColor.RED:Lorg/bukkit/ChatColor;
         * 1041: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
         * 1044: aload_1
         * 1045: invokeinterface org/bukkit/command/CommandSender.getName:()Ljava/lang/String;
         * 1050: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 1053: ldc            has successfully tested on this server.
         * 1055: invokevirtual java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
         * 1058: invokevirtual java/lang/StringBuilder.toString:()Ljava/lang/String;
         * 1061: invokestatic  org/bukkit/Bukkit.broadcastMessage:(Ljava/lang/String;)I
         * 1064: pop
         * 1065: iconst_1
         * 1066: ireturn
         * 1067: iconst_0
         * 1068: ireturn
         *  */
        // </editor-fold>
    }
}
