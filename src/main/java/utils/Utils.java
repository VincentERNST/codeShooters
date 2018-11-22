package utils;

import pojo.Point;
import pojo.Unit;

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
    
}
