package pojo;

import utils.Constants;

public class Bullet extends Unit{
	
	public int tic;
	
	public Bullet(int id, int x, int y) {
		super(id,x,y,0,0,Constants.BULLET_RADIUS,Constants.BULLET_AMORT);
		this.tic=Constants.BULLET_TIC;
	}

	public void explosion() {
		s.setImage("explosion2.png").setScale(2*Constants.EXPLOSION_RADIUS/100);
		vx=0.;
		vy=0.;
		tic=-1;
	}
	
	public void initSprite() {
        s.setVisible(true)
        .setX((int) x)
        .setY((int) y)
        .setImage("bille.png")
        .setScale(2*Constants.BULLET_RADIUS/100)
        .setAnchor(0.5);  
	}
}
