package com.blackoutburst.quake.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.blackoutburst.quake.commands.CommandBoop;
import com.blackoutburst.quake.commands.CommandBounceCount;
import com.blackoutburst.quake.commands.CommandClean;
import com.blackoutburst.quake.commands.CommandConfig;
import com.blackoutburst.quake.commands.CommandDashDelay;
import com.blackoutburst.quake.commands.CommandDashStrength;
import com.blackoutburst.quake.commands.CommandEnd;
import com.blackoutburst.quake.commands.CommandListMap;
import com.blackoutburst.quake.commands.CommandMaxScore;
import com.blackoutburst.quake.commands.CommandPlay;
import com.blackoutburst.quake.commands.CommandPlayerJump;
import com.blackoutburst.quake.commands.CommandPlayerSlow;
import com.blackoutburst.quake.commands.CommandPlayerSpeed;
import com.blackoutburst.quake.commands.CommandRayLength;
import com.blackoutburst.quake.commands.CommandResetConfig;
import com.blackoutburst.quake.commands.CommandScan;
import com.blackoutburst.quake.commands.CommandShatterCount;
import com.blackoutburst.quake.commands.CommandShatterLength;
import com.blackoutburst.quake.commands.CommandShowConfig;
import com.blackoutburst.quake.commands.CommandShowSpawn;
import com.blackoutburst.quake.commands.CommandSpawn;
import com.blackoutburst.quake.commands.CommandStart;
import com.blackoutburst.quake.commands.CommandToggleBlindness;
import com.blackoutburst.quake.commands.CommandToggleDash;
import com.blackoutburst.quake.commands.CommandToggleInvisibility;
import com.blackoutburst.quake.commands.CommandToggleJump;
import com.blackoutburst.quake.commands.CommandToggleNametag;
import com.blackoutburst.quake.commands.CommandToggleVerticalDash;
import com.blackoutburst.quake.commands.CommandToggleWalk;
import com.blackoutburst.quake.commands.CommandTriggerSpeed;
import com.blackoutburst.quake.core.Core;
import com.blackoutburst.quake.core.Dash;
import com.blackoutburst.quake.core.GameOption;
import com.blackoutburst.quake.core.GunProfile;
import com.blackoutburst.quake.core.QuakePlayer;
import com.blackoutburst.quake.core.RailGun;
import com.blackoutburst.quake.core.ScoreboardManager;
import com.blackoutburst.quake.core.SkullLoader;
import com.blackoutburst.quake.menu.BeamMenu;
import com.blackoutburst.quake.menu.ColorMenu;
import com.blackoutburst.quake.menu.ConfigMenu;
import com.blackoutburst.quake.menu.CustomMenu;
import com.blackoutburst.quake.menu.GunMenu;
import com.blackoutburst.quake.menu.MapMenu;
import com.blackoutburst.quake.menu.NameColorMenu;
import com.blackoutburst.quake.menu.ShapeMenu;
import com.blackoutburst.quake.menu.SoundsMenu;

import net.minecraft.server.v1_8_R3.EnumParticle;


public class Main extends JavaPlugin implements Listener {

	public static List<QuakePlayer> players = new ArrayList<>();
	public static boolean gameRunning = false;
	public static int gameTime = 0;
	public static List<Location> respawns = new ArrayList<>();
	
	public static World gameWorld;
	
	public static Location spawn;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		new Core().cooldownTimer();
		new Core().gameTimer();
		new File("./plugins/Quake/player data/").mkdirs();
		SkullLoader.load();
		
		gameWorld = Bukkit.getWorld("world");
		spawn = new Location(Bukkit.getWorld("world"), 8.5f, 5.0f, 8.5f, 0, 0);
	}
	
	@EventHandler
 	public void onPlayerJoin(PlayerJoinEvent event) {
		event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
		event.getPlayer().setHealth(20);
		event.getPlayer().setSaturation(20);
		event.getPlayer().setFoodLevel(20);
		event.getPlayer().setGameMode(GameMode.ADVENTURE);
		event.getPlayer().getInventory().clear();
		event.getPlayer().teleport(spawn);
		
		GunProfile gunProfile = new GunProfile("�bWooden Case", Material.WOOD_HOE, Type.BALL, Color.AQUA, false, Sound.BLAZE_DEATH, 2, ChatColor.WHITE, EnumParticle.FIREWORKS_SPARK);
		QuakePlayer qp = new QuakePlayer(event.getPlayer(), gunProfile);
		
		ScoreboardManager.init(qp);
		CustomMenu.giveItem(event.getPlayer());
		
		if (new File("./plugins/Quake/player data/"+event.getPlayer().getUniqueId().toString().replace("-", "")+".yml").exists()) {
			players.add(qp);
			qp.readPlayerData();
		} else {
			qp.savePlayerData("gun", 11);
			qp.savePlayerData("shape", 11);
			qp.savePlayerData("color", 31);
			qp.savePlayerData("sound", 11);
			qp.savePlayerData("nameColor", 23);
			qp.savePlayerData("trail", 11);
			players.add(qp);
		}
		
		ScoreboardManager.updatePlayers();
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		final QuakePlayer qp = QuakePlayer.getFromPlayer(event.getPlayer());
		final World world = player.getWorld();
		final Location belowPlayer = player.getLocation();
		belowPlayer.setY(belowPlayer.getY() - 1);
		
		if (qp.getJumpPadCooldown() <= 0 && world.getBlockAt(belowPlayer).getType().equals(Material.REDSTONE_BLOCK)) {
			qp.setJumpPadCooldown(1);
			Vector dash = player.getLocation().getDirection().clone();
			world.playSound(player.getLocation(), Sound.PISTON_EXTEND, 4, 0.5f);
			
			dash.setY(0.05f);
			dash.setX(0.0f);
			dash.setZ(0.0f);
			
			player.setVelocity(dash.multiply(5));
			
			new BukkitRunnable(){
				@Override
				public void run(){
					Vector dash = player.getLocation().getDirection().clone();
					dash.setY(0.1f);
					player.setVelocity(dash.multiply(5));
				}
			}.runTaskLater(Main.getPlugin(Main.class), 1L);
			
			new BukkitRunnable(){
				@Override
				public void run(){
					qp.setJumpPadCooldown(0);
				}
			}.runTaskLater(Main.getPlugin(Main.class), 10L);
		}
		
		if (qp.getJumpPadCooldown() <= 0 && world.getBlockAt(belowPlayer).getType().equals(Material.LAPIS_BLOCK)) {
			qp.setJumpPadCooldown(1);
			Vector dash = player.getLocation().getDirection().clone();
			world.playSound(player.getLocation(), Sound.PISTON_EXTEND, 4, 0.5f);
			
			dash.setY(0.05f);
			dash.setX(0.0f);
			dash.setZ(0.0f);
			
			player.setVelocity(dash.multiply(2.5f));
			
			new BukkitRunnable(){
				@Override
				public void run(){
					Vector dash = player.getLocation().getDirection().clone();
					dash.setY(1.0f);
					dash.setX(0.0f);
					dash.setZ(0.0f);
					player.setVelocity(dash.multiply(2.1f));
				}
			}.runTaskLater(Main.getPlugin(Main.class), 1L);
			
			new BukkitRunnable(){
				@Override
				public void run(){
					qp.setJumpPadCooldown(0);
				}
			}.runTaskLater(Main.getPlugin(Main.class), 10L);
		}
		
		if (event.getPlayer().getLocation().getY() < -10) {
			if (gameRunning) {
				Core.teleportToRespawn(event.getPlayer());
			} else {
				event.getPlayer().teleport(spawn);
			}
		}
	}
	
	@EventHandler
 	public void onPlayerQuit(PlayerQuitEvent event) {
		players.remove(QuakePlayer.getFromPlayer(event.getPlayer()));
		ScoreboardManager.updatePlayers();
		
		if (players.size() == 0) {
			gameRunning = false;
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		event.setCancelled(gameRunning);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setMessage(event.getMessage().replace("&", "�"));
		event.setFormat("%s: %s");
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		QuakePlayer qp = QuakePlayer.getFromPlayer(event.getPlayer());
		if (qp == null) return;
		
		if (event.getPlayer().getItemInHand().getType().equals(Material.NETHER_STAR)) {
			CustomMenu.open(event.getPlayer());
		}
		
		if (!gameRunning) return;
		
		Dash.dash(qp, event);
		
		if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || 
			event.getAction().equals(Action.RIGHT_CLICK_AIR)) &&
			Core.clickHoe(event.getPlayer().getItemInHand().getType()) &&
			qp.getCooldown() <= 0) {
			
			Location loc = event.getPlayer().getLocation().clone();
			loc.setY(loc.getY() + event.getPlayer().getEyeHeight());
			
			
			new BukkitRunnable(){
				@Override
				public void run(){
					new RailGun(loc, event.getPlayer().getLocation().getDirection().clone(), qp, GameOption.RAY_LENGTH, false).fire(qp);
				}
			}.runTaskAsynchronously(this);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		switch(event.getInventory().getName()) {
			case "Gun customisation Menu": 
				CustomMenu.click(event.getSlot(), (Player) event.getWhoClicked());
				event.setCancelled(true);
			break;
			case "Gun Menu": 
				GunMenu.getValue(event.getSlot(), (Player) event.getWhoClicked(), true);
				event.setCancelled(true);
			break;
			case "Shape Menu": 
				ShapeMenu.getValue(event.getSlot(), (Player) event.getWhoClicked(), true);
				event.setCancelled(true);
			break;
			case "Color Menu": 
				ColorMenu.getValue(event.getSlot(), (Player) event.getWhoClicked(), true);
				event.setCancelled(true);
			break;
			case "Kill Sounds": 
				if (event.isLeftClick())
					SoundsMenu.getValue(event.getSlot(), (Player) event.getWhoClicked(), true);
				if (event.isRightClick())
					SoundsMenu.preview(event.getSlot(), (Player) event.getWhoClicked());
				event.setCancelled(true);
			break;
			case "Name Color Menu": 
				NameColorMenu.getValue(event.getSlot(), (Player) event.getWhoClicked(), true);
				event.setCancelled(true);
			break;
			case "Game Configuration": 
				ConfigMenu.getValue(event.getSlot(), (Player) event.getWhoClicked(), event.getClickedInventory());
				event.setCancelled(true);
			break;
			case "Map Selector": 
				if (event.getCurrentItem() != null) {
					MapMenu.getValue(event.getCurrentItem().getItemMeta().getDisplayName());
					event.setCancelled(true);
				}
			break;
			case "Beam Menu": 
				BeamMenu.getValue(event.getSlot(), (Player) event.getWhoClicked(), true);
				event.setCancelled(true);
			break;
			default: break;
		}
	}
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event) {
		if (event.isSneaking())
			event.setCancelled(gameRunning);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}
	
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
    	event.setCancelled(true);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch(command.getName().toLowerCase()) {
			case "spawn":
			case "lobby":
			case "l":	
				new CommandSpawn().execute(sender); break;
			case "maxscore": new CommandMaxScore().execute(sender, args); break;
			case "bouncecount": new CommandBounceCount().execute(sender, args); break;
			case "raylength": new CommandRayLength().execute(sender, args); break;
			case "shattercount": new CommandShatterCount().execute(sender, args); break;
			case "shatterlength": new CommandShatterLength().execute(sender, args); break;
			case "listmap": new CommandListMap().execute(sender); break;
			case "start": new CommandStart().execute(sender, args); break;
			case "boop": new CommandBoop().execute(sender, args); break;
			case "end": new CommandEnd().execute(sender); break;
			case "scan": new CommandScan().execute(sender, args); break;
			case "clean": new CommandClean().execute(sender, args); break;
			case "showspawn": new CommandShowSpawn().execute(sender, args); break;
			case "triggerspeed": new CommandTriggerSpeed().execute(sender, args); break;
			case "dashdelay": new CommandDashDelay().execute(sender, args); break;
			case "config": new CommandConfig().execute(sender); break;
			case "showconfig": new CommandShowConfig().execute(sender); break;
			case "resetconfig": new CommandResetConfig().execute(sender); break;
			case "togglejump": new CommandToggleJump().execute(sender); break;
			case "togglewalk": new CommandToggleWalk().execute(sender); break;
			case "toggledash": new CommandToggleDash().execute(sender); break;
			case "toggleverticaldash": new CommandToggleVerticalDash().execute(sender); break;
			case "togglenametag": new CommandToggleNametag().execute(sender); break;
			case "toggleinvisibility": new CommandToggleInvisibility().execute(sender); break;
			case "toggleblindness": new CommandToggleBlindness().execute(sender); break;
			case "playerslow": new CommandPlayerSlow().execute(sender, args); break;
			case "playerspeed": new CommandPlayerSpeed().execute(sender, args); break;
			case "playerjump": new CommandPlayerJump().execute(sender, args); break;
			case "dashstrength": new CommandDashStrength().execute(sender, args); break;
			case "play": new CommandPlay().execute(sender); break;
			default: return true;
		}
		return true;
	}
}
