package pojo;

import com.codingame.gameengine.module.entities.Sprite;
import view.TooltipModule;

public class Unit extends Point{
	
	public int id;
	public double vx;
	public double vy;
	public double r;
	public double f;
	public Sprite s;

	public Unit(){super();}
	
	public Unit(int id, int x, int y, int vx, int vy, double r, double f) {
		super(x,y);
		this.id=id;
		this.vx = vx;
		this.vy = vy;
		this.r=r;
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
	
	public void register(TooltipModule tooltipModule){}

	@Override
	public String toString() {
		return "Unit [id=" + id + ", vx=" + vx + ", vy=" + vy + ", x=" + x + ", y=" + y + "]";
	}

	public boolean isAlive() {
		return false;
	}

	public void fallIntoVortex() {
		
	}
	
    
}
