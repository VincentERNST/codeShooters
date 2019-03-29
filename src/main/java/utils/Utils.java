package utils;

import java.util.List;

import pojo.Point;
import pojo.Unit;
import pojo.UnitFactory;

public class Utils {

	public static double distance(Point p1,Point p2){
		return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
	}  
	
	public static double distance2(Point p1, Point p2) {
		return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
	}
	
	public static double angle(Point e1, Point e2){//resultat en radian
        double dx = e2.x-e1.x;
        double dy = e2.y - e1.y;
        double d = distance(e1,e2);
        if(d==0){return 0;}
        double res = Math.acos(dx/d);
        return dy<0 ? 2*Math.PI-res: res;
    }   

	
    public static void  aim(Unit u, Point p,double thrust) {
    	double distance = distance(u,p);
        double coef = (thrust) / distance;
        u.vx += (p.x - u.x) * coef;
        u.vy += (p.y - u.y) * coef;
    }  
    
	public static Collision CollisionMurale(Unit u,double from){
    	
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
		if (t <= from || t > 1.0) {
			return null;
		}
		
		return new Collision(u, UnitFactory.createWall(dir), t);
		
	}
	
	public static Collision getCollision(Unit Unit, Unit Unit1, double from) {

		double x2 = Unit.x - Unit1.x;
		double y2 = Unit.y - Unit1.y;
		double r2 = Unit.r+Unit1.r;
		double vx2 = Unit.vx - Unit1.vx;
		double vy2 = Unit.vy - Unit1.vy;
		double a = vx2 * vx2 + vy2 * vy2;
		if (a <= Constants.EPSILON) return null;


		double b = 2.0 * (x2 * vx2 + y2 * vy2);
		double c = x2 * x2 + y2 * y2 - r2 * r2;
		double delta = b * b - 4.0 * a * c;
		if (delta < 0.0) return null;
		
		double t =  (-b - Math.sqrt(delta)) / (2.0 * a);
		if(t<=0.0) return null;//pas de collision immediate
		t+=from;
		if(t>1.0)  return null;
		
		return new Collision(Unit, Unit1,t);
	}
	
	
	public static Collision getFirstCollisionFrom(List<Unit> units, double t) {
		Collision res = null;
		for(Unit u : units) {
			
			if(!u.isAlive())
				continue;
			
			Collision collisionMur = CollisionMurale(u,t);
			if( collisionMur!=null && (res==null || collisionMur.t < res.t)) {
				res = collisionMur;
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
