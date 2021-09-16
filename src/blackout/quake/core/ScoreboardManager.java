package blackout.quake.core;

import java.util.Collections;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import blackout.quake.main.Main;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardBaseCriteria;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
import net.minecraft.server.v1_8_R3.ScoreboardScore;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;

public class ScoreboardManager {

	public static void init(Player p) {
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

		Scoreboard board = new Scoreboard();
		board.registerObjective("Score", new ScoreboardBaseCriteria("dummy"));
		
		ScoreboardObjective objective = board.getObjective("Score");
		objective.setDisplayName("�6Quake");
		board.setDisplaySlot(1, objective);

		ScoreboardTeam team = new ScoreboardTeam(board, p.getName());
		
		connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
		connection.sendPacket(new PacketPlayOutScoreboardObjective(objective, 0));
		connection.sendPacket(new PacketPlayOutScoreboardDisplayObjective(1, objective));
		
		QuakePlayer qp = QuakePlayer.getFromPlayer(p);
		qp.setBoard(board);
		
		setLine(qp, "�a�m----------", 15); 
		setLine(qp, "Time: �a0:00", 14);
		setLine(qp, " ", 13); 
		setLine(qp, "  ", 7);
		setLine(qp, "�a�m---------- ", 6); 
		
	}
	
  	public static void setLine(QuakePlayer player, String name, int score) {
  		ScoreboardScore packetScore = new ScoreboardScore(player.getBoard(), player.getBoard().getObjective("Score"), name);
  		packetScore.setScore(score);
  		
  		((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardScore(packetScore));
  	}
  	
  	public static void updatePlayers() {
  		Collections.sort(Main.players, new PlayerComparator());
  		
  		for (int i = 0; i < 5; i++) {
  			if (i < Main.players.size()) {
	  			for (QuakePlayer qp : Main.players) {
	  				QuakePlayer q = Main.players.get(i);
	  				setLine(qp, q.getPlayer().getName()+": �a"+q.getScore(), 12 - i);
	  			}
  			} else {
  				for (QuakePlayer qp : Main.players) {
  					setLine(qp, "   ", 12 - i); 
  				}
  			}
  		}
  	}
} 