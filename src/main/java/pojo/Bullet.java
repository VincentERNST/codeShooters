package pojo;

public class Bullet extends Unit{
	
	public Bullet(int id, int x, int y, int vx, int vy) {
		super(id,x,y,vx,vy,Constants.BULLET_RADIUS,Constants.BULLET_AMORT,Constants.UNIT_TYPE_BULLET);
	}
}
