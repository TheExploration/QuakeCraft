package com.blackoutburst.quake.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.blackoutburst.quake.core.GameOption;

public class CommandBounceCount {

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("�cSpecifie a score");
			return;
		}
		
		int score = 25;

		try {
			score = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage("�cThe value must be a valid number");
		}
		GameOption.BOUNCE_COUNT = score;
		
		Player p = (Player) sender;
		
		Bukkit.broadcastMessage(p.getDisplayName()+" �bhas set the bounce count to �6"+score);
	}
}
