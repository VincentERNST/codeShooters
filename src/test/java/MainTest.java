import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.codingame.game.Referee;

import pojo.Shooter;
import pojo.Unit;
import utils.Collision;
import utils.Utils;

public class MainTest {

	@Test
	public void testCollisionVortex() {
		
		Unit u = new Shooter(3,0, 820, 407,196, 138);
		double t = 0.0;
		Collision vortexCollision = Utils.getCollision(u, Referee.vortex, t);
	}

	@Test
	public void testScoring() {
		
		Shooter u1 = new Shooter(3,0, 820, 407,196, 138);
		Shooter u2 = new Shooter(3,0, 820, 407,196, 138);
		u1.hp = 67;
		List<Shooter> shooters = new ArrayList<Shooter>();
		
		shooters.add(u1);
		shooters.add(u2);
		
		int score0 = shooters.stream()
				.filter(s->s.owner==0 && s.isAlive())
				.map(s->s.hp)
				.reduce(0, (a,b)-> a+b);
		
	}

	

}
