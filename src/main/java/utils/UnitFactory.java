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
	
	
	public static Shooter createShooter(int owner, int nbr){
		Shooter toReturn = null;
		
		if(nbr>1){//extra shooters are coming from corners
			if(owner==0){
				toReturn =  new Shooter(shooterNbr++,owner, (int)(-Constants.PLAYER_RADIUS), (int)(-Constants.PLAYER_RADIUS), 200 ,200);
				toReturn.invulTimer=Constants.INVUL_TIMER;
			}
			if(owner==1){
				toReturn =  new Shooter(shooterNbr++,owner, (int)(Constants.WIDTH+Constants.PLAYER_RADIUS), (int)(Constants.HEIGHT+Constants.PLAYER_RADIUS), -200 ,-200);
				toReturn.invulTimer=Constants.INVUL_TIMER;
			}
		}else{
			int x = Constants.WIDTH/4 + owner*Constants.WIDTH/2;
			int y = Constants.HEIGHT/4*(4*nbr*owner+3-2*nbr-2*owner);
			toReturn =  new Shooter(shooterNbr++,owner, x, y, 0 ,0);
		}
		
		return toReturn;
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
