package pojo;

import java.util.List;
import utils.Constants;
import utils.Utils;

public class Vortex extends Unit{

	public Vortex(int id, int x, int y, double r) {
		super(id,x,y,0,0, r,0.);
	}
	
	public void attract(List<Unit> units) {
		for(Unit u: units){
			if(!u.isAlive()||u instanceof Vortex)
				continue;
			double angle = Utils.angle(this,u);
			double d2 = Utils.distance2(this, u);
			if(d2<r*r) {
				d2=r*r;
			}
			double attractionForce = Math.max(Constants.VORTEX_THRUST/(d2/20000) , 10.);
			u.vx-= attractionForce*Math.cos(angle);
			u.vy-= attractionForce*Math.sin(angle);
		}
	}

	@Override
	public boolean isAlive() {
		return true;
	}
	
}
