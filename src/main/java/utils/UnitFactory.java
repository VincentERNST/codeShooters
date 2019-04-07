package utils;

import pojo.Bullet;
import pojo.Shooter;
import pojo.Wall;

public class UnitFactory {
	static int shooterNbr;
	static int bulletNbr;
    static Bullet[] bulletsPool = new Bullet[Constants.BULLET_POOL_SIZE];
    
    
	public static Shooter createShooter(int owner, int x, int y,int vx, int vy){
		return new Shooter(shooterNbr++,owner, x, y, vx ,vy);
	}
	
	public static Bullet createBullet(int x, int y,double vx,double vy){
		int idx = bulletNbr%Constants.BULLET_POOL_SIZE;
		Bullet b  = bulletsPool[idx];
		if(b==null) {
			b  = new Bullet(bulletNbr++, x, y);
			bulletsPool[idx] = b;
		}
		else {
			b.x=x;
			b.y=y;
			b.id=bulletNbr++;
			b.tic=Constants.BULLET_TIC;
		}
		b.vx=vx;
		b.vy=vy;
		return b;
	}
	
	public static Wall createWall(int dir){
		return new Wall(dir);
	}
	
}
