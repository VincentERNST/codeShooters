package pojo;

public class Unit {
	
	public double x;
	public double y;
	public double vx;
	public double vy;
	public double r=50.0;
	
	public Unit(int x, int y, int vx, int vy) {
		super();
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
	}
	
	public void move(double t){
		x+=t*vx;
		y+=t*vy;
	}
	
	public void end(){
		x = (int)Math.round(x);
		y = (int)Math.round(y);
	}
	
	
//    public static Collision CollisionMurale(double from){
//		double tx = 2.0;
//		double ty = tx;
//		
//		double r = this.r;
//		
//		if(u.unitType==1 && u.y<5450  && 2050 <u.y){
//			r=0.0;
//		}
//		
//		if (u.x + u.vx < r) {
//			tx = (r - u.x) / u.vx;
//		} else if (u.x + u.vx > Constants.WIDTH - r) {
//			tx = (Constants.WIDTH - r - u.x) / u.vx;
//		}
//
//		if (u.y + u.vy < r) {
//			ty = (r - u.y) / u.vy;
//		} else if (u.y + u.vy > HEIGHT - r) {
//			ty = (HEIGHT - r - u.y) / u.vy;
//		}
//
//		int dir = -1;
//		double t = -1.0;
//
//		if (tx < ty) {
//			dir = Constants.HORIZONTAL;
//			t = tx;
//		} else {
//			dir = Constants.VERTICAL;
//			t = ty;
//		}
//		t+=from;
//		if (t <= from || t > 1.0) {
//			return null;
//		}
//		if (dir == Constants.HORIZONTAL) {
//			return new Collision(u, MurH, t);
//		} else {
//			return new Collision(u, MurV, t);
//		}
//	}
    
    
    
}
