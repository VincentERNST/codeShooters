package pojo;

import com.codingame.gameengine.module.entities.Text;

import utils.Constants;

public class Shooter extends Unit{
	
	public Text message;
	public int hp;
	
	public Shooter(int id, int x, int y, int vx, int vy) {
		super(id,x,y,vx,vy,Constants.PLAYER_RADIUS,Constants.PLAYER_AMORT);
		hp=1000;
	}
	
	
}
