package pojo;

public class UnitFactory {

	static int shooterNbr;
	static int bulletNbr;
	
	public static Shooter createShooter(int x, int y,int vx, int vy){
		return new Shooter(shooterNbr++, x, y, vx ,vy);
	}
	
	public static Bullet createBullet(int x, int y){
		return new Bullet(bulletNbr++, x, y);
	}
	
	public static Wall createWall(int dir){
		return new Wall(dir);
	}
	
}
