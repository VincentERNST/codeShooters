package pojo;

import java.util.HashMap;
import utils.Constants;
import view.TooltipModule;

public class Bullet extends Unit{
	
	public int tic;
	
	public Bullet(int id, int x, int y) {
		super(id,x,y,0,0,Constants.BULLET_RADIUS,Constants.BULLET_AMORT);
		this.tic=Constants.BULLET_TIC;
	}
	
	@Override
	public boolean isAlive() {
		return tic>0;
	}
	
	public void explosion() {
		s.setImage("explosion2.png").setScale(2*Constants.EXPLOSION_RADIUS/100).setZIndex(2);
		vx=0.;
		vy=0.;
		this.tic=0;
	}
	
	public void fade() {
		s.setAlpha(0.1)
		.setImage("smoke.png")
		.setScale(2+2*Constants.EXPLOSION_RADIUS/100);
	}
	
	public void initSprite() {
        s.setVisible(true)
        .setX((int) x)
        .setY((int) y)
        .setZIndex(3)
        .setAlpha(1)
        .setImage("bille.png")
        .setScale(2*Constants.BULLET_RADIUS/100)
        .setAnchor(0.5);  
	}
	
	@Override
	public void register(TooltipModule tooltipModule){//TODO different registering MORPH
		
	    StringBuilder sb = new StringBuilder();
	    sb.append("Bullet ").append(this.id).append("\n")
	      .append("x : ").append((int)this.x).append("\n")
	      .append("y : ").append((int)this.y).append("\n")
	      .append("vx : ").append((int)this.vx).append("\n")
	      .append("vy : ").append((int)this.vy).append("\n")
	      .append("Tic : ").append(tic);
	    
        tooltipModule.registerEntity(s, new HashMap<>());
        tooltipModule.updateExtraTooltipText(s, sb.toString());
	}
	
}
