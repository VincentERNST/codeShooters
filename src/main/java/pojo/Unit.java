package pojo;

import java.util.HashMap;

import com.codingame.gameengine.module.entities.Sprite;

import view.TooltipModule;

public class Unit extends Point{
	
	public int id;
	public double vx;
	public double vy;
	public double r=50.0;
	public double f;
	public Sprite s;
	
	public Unit(int id, int x, int y, int vx, int vy, double f) {
		super(x,y);
		this.id=id;
		this.vx = vx;
		this.vy = vy;
		this.f=f;
	}	
	
	public void move(double t){
		x+=t*vx;
		y+=t*vy;
	}
	
	public void thrust(double angle, int thrust){
		vx+=thrust*Math.cos(angle);
		vy+=thrust*Math.sin(angle);
	}
	
	public void end(){
		x = (int)Math.round(x);
		y = (int)Math.round(y);
		vx = (int)Math.round(f*vx);
		vy = (int)Math.round(f*vy);
	}
	
	public void register( TooltipModule tooltipModule){
		if(s==null){return;}
		
	    StringBuilder sb = new StringBuilder();
	    sb.append("Unit ").append(this.id).append("\n")
	      .append("x : ").append((int)this.x).append("\n")
	      .append("y : ").append((int)this.y);
	    
        tooltipModule.registerEntity(s, new HashMap<>());
        tooltipModule.updateExtraTooltipText(s, sb.toString());
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
