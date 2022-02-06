package com.blackoutburst.quake.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.blackoutburst.quake.core.GameOption;
import com.blackoutburst.quake.core.QuakePlayer;
import com.blackoutburst.quake.main.Main;

import java.util.ArrayList;

public class CommandPlayerSlow {

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§cSpecifie a value");
			return;
		}
		
		int value = 0;

		try {
			value = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage("§cThe value must be a valid number");
		}
		GameOption.SLOWNESS = value;
		
		Player p = (Player) sender;
		
		for (QuakePlayer qp : Main.players)
			qp.getPlayer().sendMessage(p.getDisplayName()+" §bhas set the player slowness to §6"+value);
	}
}
