package pojo;

import utils.Constants;

public class UnitFactory {

	static int shooterNbr;
	static int bulletNbr;
    static Bullet[] bulletsPool = new Bullet[Constants.BULLET_POOL_SIZE];
    
    
	public static Shooter createShooter(int x, int y,int vx, int vy){
		return new Shooter(shooterNbr++, x, y, vx ,vy);
	}
	
	public static Bullet createBullet(int x, int y){
		int idx = bulletNbr%Constants.BULLET_POOL_SIZE;
		Bullet b  = bulletsPool[idx];
		if(b==null) {
			b  = new Bullet(bulletNbr++, x, y);
			bulletsPool[idx] = b;
			System.err.println("new stuff"+bulletNbr +" / "+Constants.BULLET_POOL_SIZE );
		}
		else {
			b.x=x;
			b.y=y;
			b.id=bulletNbr++;
			b.s.setVisible(true);//TODO reset sprite needed
			System.err.println("hello reusing bullet from pull nbr : "+idx);
		}
		return b;
	}
	
	public static Wall createWall(int dir){
		return new Wall(dir);
	}
	
}
