package pojo;

import java.util.ArrayList;
import utils.Constants;
import utils.Utils;

public class Vortex extends Unit{

	public Vortex(int id, int x, int y, double r) {
		super(id,x,y,0,0, r,0.);
	}
	
	public void attract(ArrayList<Unit> units) {
	 //TOSO vortexPower / d  equivalent 100.0 (player thrust) et 10
		for(Unit u: units){
			if(!u.isAlive())
				continue;
			double angle = Utils.angle(this,u);
			double attractionForce = Constants.VORTEX_THRUST;
			u.vx-= attractionForce*Math.cos(angle);
			u.vy-= attractionForce*Math.sin(angle);
		}
		
//		MIN(Constants.VORTEX_THRUST/ (Utils.dis / 1000)2, 10 )
		
	}
}
