package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Get an answer to a question you may have!", usage = "/<command> [question]")
public class Command_faq extends TFM_Command
{

  @Override
	public boolean run(CommandSender sender, Player sender_p, Command cmd,String commandLabel, String[] args, boolean senderIsConsole)
	{
		playerMsg("Welcome to the FAQ Question list!", ChatColor.AQUA);
		playerMsg("Owner?", ChatColor.GOLD);
		playerMsg("WhatdoIdoifsomeonegriefsme?", ChatColor.GOLD);
		playerMsg("Imgettinggriefedandtheresnoadmin!", ChatColor.GOLD);
		playerMsg("Forums?", ChatColor.GOLD);
		playerMsg("Website?", ChatColor.GOLD);
		//playerMsg("can i touch robolawrence?", ChatColor.GOLD);
		playerMsg("To get the answer of a question, do /faq questionname", ChatColor.AQUA);
		playerMsg("Don't use spaces! Always use this for example: /faq WhatdoIdoifsomeonegriefsme?", ChatColor.AQUA);
		
		if (args[0].equalsIgnoreCase("Owner?"))
		{
			playerMsg("The owner is MarkByron.", ChatColor.GOLD);
		}
		else if (args[0].equalsIgnoreCase("WhatdoIdoifsomeonegriefsme?"))
		{
			playerMsg("Simple, you say 'Admin! I'm getting griefed!'", ChatColor.GOLD);
		}
		else if (args[0].equalsIgnoreCase("Imgettinggriefedandtheresnoadmin!"))
		{
			playerMsg("To be honest, there is nothing you can do. BUT! You can actually make a griefer report on the forums! /faq Forums? for how to register on the forums.", ChatColor.GOLD);
		}
		else if (args[0].equalsIgnoreCase("Forums?"))
		{
			playerMsg("To get on our forums, you gotta go to totalfreedom.boards.net, register (It's simple so I'm not gonna instruct you on registering) then you have access to our forums! We recommend you explore!", ChatColor.GOLD);
		}
		else if (args[0].equalsIgnoreCase("Website?"))
		{
			playerMsg("It is important that players know our website, totalfreedom.me (Yes, me domain, deal with it!)", ChatColor.GOLD);
		}
		/*
		else if (args[0].equalsIgnoreCase("can i touch robolawrence?"))
		{
		        playerMsg("Yes you can! He loves it!", ChatColor.GOLD);
		}
		*/
		
		//Note to darth: You can always add some more questions.
			
		return senderIsConsole;
	}
	
}
	
