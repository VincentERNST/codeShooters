package utils;

import pojo.Bomb;
import pojo.Shooter;
import pojo.Wall;

public class UnitFactory {
	static int shooterNbr;
	static int bulletNbr;
    static Bomb[] bulletsPool = new Bomb[Constants.BOMB_POOL_SIZE];
    
    
	public static Shooter createShooter(int owner, int x, int y,int vx, int vy){
		return new Shooter(shooterNbr++,owner, x, y, vx ,vy);
	}
	
	public static Bomb createBomb(int x, int y,double vx,double vy){
		int idx = bulletNbr%Constants.BOMB_POOL_SIZE;
		Bomb b  = bulletsPool[idx];
		if(b==null) {
			b  = new Bomb(bulletNbr++, x, y);
			bulletsPool[idx] = b;
		}
		else {
			b.x=x;
			b.y=y;
			b.id=bulletNbr++;
			b.tic=Constants.BOMB_TIC;
		}
		b.vx=vx;
		b.vy=vy;
		return b;
	}
	
	public static Wall createWall(int dir){
		return new Wall(dir);
	}
	
}
