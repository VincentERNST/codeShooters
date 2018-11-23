package utils;

import pojo.Point;
import pojo.Unit;
import pojo.UnitFactory;

public class Utils {

	public static double angle(Point e1, Point e2){//resultat en radian
        double dx = e2.x-e1.x;
        double dy = e2.y - e1.y;
        double d = distance(e1,e2);
        if(d==0){return 0;}
        double res = Math.acos(dx/d);
        return dy<0 ? 2*Math.PI-res: res;
    }   

    public static double distance(Point p1,Point p2){
        return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
    }  

	public static double distance2(Point p1, Point p2) {
		return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
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
	
    
}
