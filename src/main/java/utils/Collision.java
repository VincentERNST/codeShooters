package utils;

import pojo.Unit;
import pojo.Wall;

public class Collision{
	
	public Unit u1;
	public Unit u2;
	public double t;
	
	public Collision(Unit u1, Unit u2, double t) {
		super();
		this.u1 = u1;
		this.u2 = u2;
		this.t = t;
	}
	
	public void apply() {
		
		if( u2 instanceof Wall){
			Wall wall = (Wall)u2;
			
//			u1.move(t);
//			u1.end();
			
			if (wall.direction == Constants.HORIZONTAL) {
				u1.vx = -u1.vx;
			}
			if (wall.direction == Constants.VERTICAL) {
				u1.vy = -u1.vy;
			}
	
//			u1.s.setX((int) u1.x).setY((int) u1.y);
//			graphicEntityModule.commitEntityState(t, u1.s);
//	
//			u1.move(1.0 - t);
//			u1.s.setX((int) u1.x).setY((int) u1.y);
//			graphicEntityModule.commitEntityState(1, u1.s);
		}
		
	}

}
