package pojo;

import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Text;

import utils.Constants;

public class Shooter extends Unit{
	
	public Text message;
	public int hp;
	public Circle circle;
	Rectangle staticHealthBar;
	Rectangle dynamicHealthBar;
	
	public Shooter(int id, int x, int y, int vx, int vy) {
		super(id,x,y,vx,vy,Constants.PLAYER_RADIUS,Constants.PLAYER_AMORT);
		hp=Constants.PLAYER_BASE_HP;
	}
	
	@Override
	public boolean isAlive() {
		return hp>0;
	}
	
	public void hideSprites() {
		circle.setVisible(false);
		message.setVisible(false);
		s.setVisible(false);
	}
	
}
