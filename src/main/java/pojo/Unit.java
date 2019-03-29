package pojo;

import java.util.HashMap;

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
	
	public void register(TooltipModule tooltipModule){//TODO different registering MORPH
		if(s==null){return;}
		
	    StringBuilder sb = new StringBuilder();
	    sb.append("Unit ").append(this.id).append("\n")
	      .append("x : ").append((int)this.x).append("\n")
	      .append("y : ").append((int)this.y).append("\n")
	      .append("vx : ").append((int)this.vx).append("\n")
	      .append("vy : ").append((int)this.vy);
	    
	    if(this instanceof Shooter) {
	    	sb.append("\n").append("life : ").append(((Shooter)this).hp);
	    }
	    if(this instanceof Bullet) {
	    	sb.append("\n").append("TIC : ").append(((Bullet)this).tic);
	    }
	    
        tooltipModule.registerEntity(s, new HashMap<>());
        tooltipModule.updateExtraTooltipText(s, sb.toString());
	}

	@Override
	public String toString() {
		return "Unit [id=" + id + ", vx=" + vx + ", vy=" + vy + ", x=" + x + ", y=" + y + "]";
	}

	public boolean isAlive() {
		return false;
	}
	
    
}
