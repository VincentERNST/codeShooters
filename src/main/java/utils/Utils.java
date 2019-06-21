package utils;

import java.util.List;

import com.codingame.game.Referee;

import pojo.Point;
import pojo.Unit;

public class Utils {

	public static double distance(Point p1,Point p2){
		return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
	}  
	
	public static double distance2(Point p1, Point p2) {
		return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
	}
	
	public static double angle(Point e1, Point e2){//radian
        double dx = e2.x-e1.x;
        double dy = e2.y - e1.y;
        double d = distance(e1,e2);
        if(d==0){return 0;}
        double res = Math.acos(dx/d);
        return dy<0 ? 2*Math.PI-res: res;
    }   

	
    public static void  aim(Unit u, Point p,double thrust) {
    	if(u.x==p.x && u.y==p.y){
    		return;
    	}
    	double distance = distance(u,p);
        double coef = (thrust) / distance;
        u.vx += (p.x - u.x) * coef;
        u.vy += (p.y - u.y) * coef;
    }  
    
	public static Collision WallCollision(Unit u,double from){
    	
		double tx = 2.0;
		double ty = tx;
		double r= u.r;
		
		if (u.x + u.vx < r) {
			tx = (r - u.x) / u.vx;
		} else if (u.x + u.vx > Constants.WIDTH - r) {
			tx = (Constants.WIDTH - r - u.x) / u.vx;
		}

		if (u.y + u.vy < r) {
			ty = (r - u.y) / u.vy;
		} else if (u.y + u.vy > Constants.HEIGHT - r) {
			ty = (Constants.HEIGHT - r - u.y) / u.vy;
		}

		int dir = -1;
		double t = -1.0;

		if (tx < ty) {
			dir = Constants.HORIZONTAL;
			t = tx;
		} else {
			dir = Constants.VERTICAL;
			t = ty;
		}
		t+=from;
		if (t < from || t > 1.0) {
			return null;
		}
		
		return new Collision(u, UnitFactory.createWall(dir), t);
		
	}
	
	public static Collision getCollision(Unit unit, Unit other, double from) {

		double r2 = unit.r+other.r;
		if(distance2(unit , other)<r2*r2){return null;}
		double x2 = unit.x - other.x;
		double y2 = unit.y - other.y;
		double vx2 = unit.vx - other.vx;
		double vy2 = unit.vy - other.vy;
		double a = vx2 * vx2 + vy2 * vy2;
		if (a <= Constants.EPSILON) return null;

		double b = 2.0 * (x2 * vx2 + y2 * vy2);
		double c = x2 * x2 + y2 * y2 - r2 * r2;
		double delta = b * b - 4.0 * a * c;
		if (delta < 0.0) return null;
		
		double t =  (-b - Math.sqrt(delta)) / (2.0 * a);
		if(t<0.0) return null;
		t+=from;
		if(t>1.0)  return null;
		
		return new Collision(unit, other,t);
	}
	
	
	public static Collision getFirstCollisionFrom(List<Unit> units, double t) {
		Collision res = null;
		for(Unit u : units) {
			
			if(!u.isAlive())
				continue;
			
			Collision collisionMur = WallCollision(u,t);
			if( collisionMur!=null && (res==null || collisionMur.t < res.t)) {
				res = collisionMur;
			}
			
			Collision vortexCollision = getCollision(u, Referee.vortex, t);
			if( vortexCollision!=null && (res==null || vortexCollision.t < res.t)) {
				res = vortexCollision;
			}
			
			for(Unit other : units) {
				
				if(!other.isAlive() || other.equals(u))
					continue;
				
				Collision collision = getCollision(u, other, t);
				if( collision!=null && (res==null || collision.t < res.t)) {
					res = collision;
				}
			}
		}
		return res;
	}
	
	
}
