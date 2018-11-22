package pojo;

import utils.Constants;

public class Bullet extends Unit{
	
	public Bullet(int id, int x, int y) {
		super(id,x,y,0,0,Constants.BULLET_RADIUS,Constants.BULLET_AMORT);
	}
}
