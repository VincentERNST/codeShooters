package utils;

import pojo.Bomb;
import pojo.Shooter;
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
		
		if(u2 instanceof Wall){//unit hits a wall
			Wall wall = (Wall)u2;
			if (wall.direction == Constants.HORIZONTAL){
				u1.vx = -u1.vx;
			}
			if (wall.direction == Constants.VERTICAL){
				u1.vy = -u1.vy;
			}
			return;
		}
		
		if(u1 instanceof Bomb){//ball hits a unit
			((Bomb)u1).explosion();
			if(u2 instanceof Shooter) {
				((Shooter) u2).hp-=Constants.BOMB_DAMAGE;
				((Shooter) u2).s.setImage("damaged.png");
			}
			else if(u2 instanceof Bomb){
				((Bomb)u2).explosion();
			}
			return;
		}
		
		if(u2 instanceof Bomb){//a unit hits a ball
			((Bomb)u2).explosion();
			if(u1 instanceof Shooter) {
				((Shooter) u1).hp-=Constants.BOMB_DAMAGE;
				((Shooter) u1).s.setImage("Damaged.png");
			}
			else if(u1 instanceof Bomb){
				((Bomb)u1).explosion();
			}
			return;
		}
		
		bounce(u1,u2);//Shooter hits another Shooter
		  
	}
	
	
    public static void bounce(Unit u1, Unit u2){
        
        double nx = u1.x - u2.x;
        double ny = u1.y - u2.y;
        double nxnydeux = nx*nx + ny*ny;
        double dvx = u1.vx - u2.vx;
        double dvy = u1.vy - u2.vy;
        double product = (nx*dvx + ny*dvy) / (nxnydeux * 2);
        double fx = nx * product;
        double fy = ny * product;
        

        u1.vx -= fx;
        u1.vy -= fy;
        u2.vx += fx;
        u2.vy += fy;
        
  	  // Normalize vector at 100
  	  double impulse = Math.sqrt(fx*fx + fy*fy);
  	  if (impulse < 100.0) {
  	      double min = 100.0 / impulse;
  	      fx = fx * min;
  	      fy = fy * min;
  	  }
  	  
        u1.vx -= fx;
        u1.vy -= fy;
        u2.vx += fx;
        u2.vy += fy;    
     }
    
}
