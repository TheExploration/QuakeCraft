package com.blackoutburst.quake.core;

import java.awt.Color;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.PlayerConnection;


public class RailGun {
	
	protected Location location;
	protected Vector direction;
	protected QuakePlayer owner;
	
	private int trailColor = 0;
	private int circle = 0;
	
	public RailGun(Location location, Vector direction, QuakePlayer owner) {
		this.location = location;
		this.direction = direction;
		this.owner = owner;
	}
	
	public boolean insideBlock() {
		final Block b = location.getWorld().getBlockAt(location);
		
		return (!b.getType().equals(Material.AIR) && 
				!b.getType().equals(Material.TORCH) &&
				!b.getType().equals(Material.STEP) &&
				!b.getType().equals(Material.RED_ROSE) &&
				!b.getType().equals(Material.YELLOW_FLOWER) &&
				!b.getType().equals(Material.LONG_GRASS) &&
				!b.getType().equals(Material.DOUBLE_PLANT) &&
				!b.getType().equals(Material.DEAD_BUSH) &&
				!b.getType().equals(Material.RED_MUSHROOM) &&
				!b.getType().equals(Material.BROWN_MUSHROOM) &&
				!b.getType().equals(Material.VINE) &&
				!b.getType().equals(Material.WOOD_STEP) &&
				!b.getType().equals(Material.STONE_PLATE) &&
				!b.getType().equals(Material.WOOD_PLATE) &&
				!b.getType().equals(Material.GOLD_PLATE) &&
				!b.getType().equals(Material.IRON_PLATE) &&
				!b.getType().equals(Material.TRAP_DOOR) &&
				!b.getType().equals(Material.IRON_TRAPDOOR) &&
				!b.getType().equals(Material.CARPET) &&
				!b.getType().equals(Material.BARRIER) &&
				!b.getType().equals(Material.STONE_BUTTON) &&
				!b.getType().equals(Material.WOOD_BUTTON) &&
				!b.getType().equals(Material.SNOW) &&
				!b.getType().equals(Material.FIRE) &&
				!b.getType().equals(Material.SIGN) &&
				!b.getType().equals(Material.WATER) &&
				!b.getType().equals(Material.WATER_LILY) &&
				!b.getType().equals(Material.STATIONARY_WATER) &&
				!b.getType().equals(Material.LAVA) &&
				!b.getType().equals(Material.STATIONARY_LAVA) &&
				!b.getType().equals(Material.RAILS) &&
				!b.getType().equals(Material.ACTIVATOR_RAIL) &&
				!b.getType().equals(Material.DETECTOR_RAIL) &&
				!b.getType().equals(Material.POWERED_RAIL) &&
				!b.getType().equals(Material.LEVER) &&
				!b.getType().equals(Material.REDSTONE_WIRE) &&
				!b.getType().equals(Material.REDSTONE_TORCH_ON) &&
				!b.getType().equals(Material.REDSTONE_TORCH_OFF) &&
				!b.getType().equals(Material.LADDER) &&
				!b.getType().equals(Material.TRIPWIRE_HOOK) &&
				!b.getType().equals(Material.WATER_LILY) &&
				!b.getType().equals(Material.REDSTONE_COMPARATOR) &&
				!b.getType().equals(Material.REDSTONE_COMPARATOR_ON) &&
				!b.getType().equals(Material.REDSTONE_COMPARATOR_OFF) &&
				!b.getType().equals(Material.FLOWER_POT) &&
				!b.getType().equals(Material.PORTAL) &&
				!b.getType().equals(Material.ENDER_PORTAL));
	}
	
	public void fire(QuakePlayer p) {
		final RailGun b = this;
		trailColor = 0;

		p.getPlayer().getWorld().playSound(p.getPlayer().getLocation(), Sound.BLAZE_HIT, 2, 2);
		p.cooldown = GameOption.FIRE_DELAY;
		
		for (int i = 500; i > 0; i--) {
			b.location.add(b.direction.normalize().multiply(0.5));
			b.trail();
			b.getNearbyPlayer();
			if (b.insideBlock()) break;
		}
	}
	
	public void getNearbyPlayer() {
		for (Entity e : location.getWorld().getEntities()) {
			final float x = (float) (location.getX() - e.getLocation().getX());
			final float y = (float) (location.getY() - e.getLocation().getY());
			final float z = (float) (location.getZ() - e.getLocation().getZ());
			final boolean dist = ((x * x) + (y * y) + (z * z)) <= 4.0;
			if (e instanceof Player) {
				if (((Player) e).getGameMode() == GameMode.SPECTATOR) continue;
				if (e.getUniqueId() != owner.getPlayer().getUniqueId() && dist) {
					Core.teleportToRespawn((Player) e);
					owner.getPlayer().getWorld().playSound(owner.getPlayer().getLocation(), owner.getGunProfile().getSound(), 3, owner.getGunProfile().getPitch());
					Bukkit.broadcastMessage(owner.getPlayer().getDisplayName()+" �egibbed�r "+((Player)e).getDisplayName());
					this.detonate(owner);
				}
			} else if (e instanceof LivingEntity && dist && ((LivingEntity) e).getHealth() > 0) {
				Bukkit.broadcastMessage(owner.getPlayer().getDisplayName()+" �egibbed a�r "+e.getName()+" ??");
				((LivingEntity) e).setHealth(0);
			}
		}
	}
	
	private Color getColor() {
		switch(trailColor) {
			default: return new Color(255, 0, 0);
			case 1: return new Color(255, 85, 0);
			case 2: return new Color(255, 132, 0);
			case 3: return new Color(255, 174, 0);
			case 4: return new Color(255, 255, 0);
			case 5: return new Color(174, 255, 0);
			case 6: return new Color(132, 255, 0);
			case 7: return new Color(85, 255, 0);
			case 8: return new Color(0, 255, 0);
			case 9: return new Color(0, 255, 85);
			case 10: return new Color(0, 255, 132);
			case 11: return new Color(0, 255, 174);
			case 12: return new Color(0, 255, 255);
			case 13: return new Color(0, 174, 255);
			case 14: return new Color(0, 132, 255);
			case 15: return new Color(0, 85, 255);
			case 16: return new Color(0, 0, 255);
			case 17: return new Color(85, 0, 255);
			case 18: return new Color(132, 0, 255);
			case 19: return new Color(174, 0, 255);
			case 20: return new Color(255, 0, 255);
			case 21: return new Color(255, 0, 174);
			case 22: return new Color(255, 0, 132);
			case 23: trailColor = 0; return new Color(255, 0, 85);
		}
	}
	
	private void createCircle(PlayerConnection connection, float radius) {
		int particles = 9;
		for (int i = 0; i < particles; i++) {
			double angle = 2 * Math.PI * i / particles;
			double x = Math.cos(angle) * radius;
			double y = Math.sin(angle) * radius;
			Vector v = rotateAroundAxisX(new Vector(x, y, 0), location.getPitch());
			v = rotateAroundAxisY(v, location.getYaw());
			Location temp = location.clone().add(v);
			
			connection.sendPacket(new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, (float) temp.getX(), (float) temp.getY(), (float) temp.getZ(), 0, 0, 0, 0, 1));
		}
	}
	
	private Vector rotateAroundAxisX(Vector v, double angle) {
		angle = Math.toRadians(angle);
		
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double y = v.getY() * cos - v.getZ() * sin;
		double z = v.getY() * sin + v.getZ() * cos;
		return v.setY(y).setZ(z);
	}
	 
	private Vector rotateAroundAxisY(Vector v, double angle) {
		angle = -angle;
		angle = Math.toRadians(angle);
		
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double x = v.getX() * cos + v.getZ() * sin;
		double z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}
	
	public void trail() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
			if (owner.gunProfile.trail == EnumParticle.REDSTONE) {
				final Color c = getColor();
				final float r = c.getRed() == 0 ? Float.MIN_VALUE : (float)(c.getRed()) / 255.0f;
				final float g = (float)(c.getGreen()) / 255.0f;
				final float b = (float)(c.getBlue()) / 255.0f;
				connection.sendPacket(new PacketPlayOutWorldParticles(owner.gunProfile.trail, true, (float) location.getX(), (float) location.getY(), (float) location.getZ(), r, g, b, 1, 0));
			}else if (owner.gunProfile.trail == EnumParticle.BARRIER) {
				connection.sendPacket(new PacketPlayOutWorldParticles(EnumParticle.FLAME, true, (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 1));
				circle++;
				if (circle > 2) {
					createCircle(connection, 0.3f);
					circle = 0;
				}
			} else {
				connection.sendPacket(new PacketPlayOutWorldParticles(owner.gunProfile.trail, true, (float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, 1));
			}
		}
		trailColor++;
	}
	
	public void detonate(QuakePlayer owner) {
		owner.score++;
		ScoreboardManager.updatePlayers();
		
		if (owner.score >= GameOption.MAX_SCORE) {
			Core.endGame();
		}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getWorld() != owner.getPlayer().getWorld()) continue;
			
			PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
			ItemStack stackFirework = new ItemStack(Material.FIREWORK);
			FireworkMeta fireworkMeta = (FireworkMeta) stackFirework.getItemMeta();
			FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(this.owner.getGunProfile().getColor()).with(this.owner.getGunProfile().getShape()).build();
			fireworkMeta.addEffect(effect);
			fireworkMeta.setPower(2);
			stackFirework.setItemMeta(fireworkMeta);
			EntityFireworks firework = new EntityFireworks(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(stackFirework));
			firework.expectedLifespan = 0;
			connection.sendPacket(new PacketPlayOutSpawnEntity(firework, 76));
			connection.sendPacket(new PacketPlayOutEntityMetadata(firework.getId(), firework.getDataWatcher(), true));
			connection.sendPacket(new PacketPlayOutEntityStatus(firework, (byte) 17));
			connection.sendPacket(new PacketPlayOutEntityDestroy(firework.getId()));
		}
	}
}