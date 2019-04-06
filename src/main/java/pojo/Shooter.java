package pojo;

import java.util.HashMap;

import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.entities.Text;

import utils.Constants;
import view.TooltipModule;

public class Shooter extends Unit{
	
	public Text message;
	public int hp;
	public Circle circle;
	public Rectangle staticHealthBar;
	public Rectangle dynamicHealthBar;
	
	public Shooter(int id, int x, int y, int vx, int vy) {
		super(id,x,y,vx,vy,Constants.PLAYER_RADIUS,Constants.PLAYER_AMORT);
		hp=Constants.PLAYER_HP;
	}
	
	@Override
	public boolean isAlive() {
		return hp>0;
	}
	
	public void hideSprites() {
		circle.setVisible(false);
		message.setVisible(false);
		s.setVisible(false);
		dynamicHealthBar.setScaleX(0.);
	}
	
	@Override
	public void register(TooltipModule tooltipModule){//TODO different registering MORPH
		
	    StringBuilder sb = new StringBuilder();
	    sb.append("Shooter ").append(this.id).append("\n")
	      .append("x : ").append((int)this.x).append("\n")
	      .append("y : ").append((int)this.y).append("\n")
	      .append("vx : ").append((int)this.vx).append("\n")
	      .append("vy : ").append((int)this.vy).append("\n")
	      .append("Life : ").append(hp);
	    
        tooltipModule.registerEntity(s, new HashMap<>());
        tooltipModule.updateExtraTooltipText(s, sb.toString());
	}

	public void heal() {
		hp+=Constants.HEAL;
		s.setImage("healed.png");
	}
	
}
