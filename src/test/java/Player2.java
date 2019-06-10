import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

import pojo.Point;

public class Player2 {
	
	static Random random = new Random();
	static ArrayList<Ship> ships = new ArrayList<Ship>( );
	static ArrayList<Ship> enemies = new ArrayList<Ship>( );
	static ArrayList<Ball> balls = new ArrayList<Ball>( );
	
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int myId = scanner.nextInt();
        System.err.println( "my ID is : " +myId);
        int turn =0;
        while (true) {
        	turn++;
            //players
            int m = scanner.nextInt();
            for(int i = 0;i<m;i++){
            	int id = scanner.nextInt();
            	int owner= scanner.nextInt();           	
            	int x = scanner.nextInt();
            	int y = scanner.nextInt();
            	int vx = scanner.nextInt();
            	int vy = scanner.nextInt();
            	int hp = scanner.nextInt();
            	Ship s = new Ship(x,y,vx,vy,hp,owner);
            	System.err.println(s);
            	if(s.owner==myId){
            		ships.add(s);
            	}
            	else{
            		enemies.add(s);
            	}
            }           
            
            
            //bullets
            int n = scanner.nextInt();
            for(int i = 0;i<n;i++){
            	int id = scanner.nextInt();
            	int bulletX = scanner.nextInt();
            	int bulletY = scanner.nextInt();
            	int bulletVX = scanner.nextInt();
            	int bulletVY = scanner.nextInt();
            	int tic = scanner.nextInt();
            	Ball b = new Ball(bulletX,bulletY,bulletVX,bulletVY,tic);
            	balls.add(b);
            }
        	
            
            System.err.println("i have "+ships.size());
            
            
            
            for(Ship s : ships){

            	if(s.hp<=0){
            		System.out.println("WAIT");
            		continue;
            	}
            	
            	Ball b = closestComingBall(s);
            	int aimX = (int) s.x;
            	int aimY=(int) s.y;
            	if(b!=null){
            		double a = angle(b,new Ball(b.x+b.vx,b.y+b.vy,0,0,0));
            		aimX = (int)(s.x + 1000*Math.cos(a+(0.6*Math.PI)) ); 
            		aimY = (int)(s.y + 1000*Math.sin(a+(0.6*Math.PI)) ); 
            	}
            	
            	Ship e = closestEnemyAlive(s);
            	if(e==null){
            		System.err.println("WAIT");
            		System.out.println("WAIT");
            		continue;
            	}
            	else{
	            	if(turn%5==4){
	            		System.out.println(String.format("MOVE %d %d;HEAL;HEAL",aimX , aimY));
	            		continue;
	            	}
	            	
	            	if(turn%5==3){
	            		System.out.println(String.format("MOVE %d %d;SHOOT %d %d; Shoot "+e.x+" "+e.y,aimX , aimY,(int)s.x ,(int)s.y));
	            		continue;
	            	}           	
	            	System.out.println(String.format("MOVE %d %d;SHOOT %d %d; Shoot "+e.x+" "+e.y,aimX , aimY,(int)e.x ,(int)e.y));
            	}
            }
             
             ships.clear();
             enemies.clear();
             balls.clear();
             
        }
    }
    
	public static double angle(Ball e1, Ball e2){//radian
        double dx = e2.x-e1.x;
        double dy = e2.y - e1.y;
        double d = Math.sqrt((e1.x-e2.x)*(e1.x-e2.x) + (e1.y-e2.y)*(e1.y-e2.y));
        if(d==0){return 0;}
        double res = Math.acos(dx/d);
        return dy<0 ? 2*Math.PI-res: res;
    }   
	
	private static Ship closestEnemyAlive(Ship s) {
		Ship res = null;
		double min = 999999.;
		for(Ship e : enemies){
			double d = dist(s,e);
			if(d<min && e.hp>0){
				min=d;
				res = e;
			}
		}
		return res;
	}

	private static double dist(Ship s, Ship b) {
		return Math.sqrt((s.x-b.x)*(s.x-b.x) + (s.y-b.y)*(s.y-b.y));
	}

	private static Ball closestComingBall(Ship s) {
		Ball res = null;
		double min = 999999.;
		for(Ball b : balls){
			double d = dist(s,b);
			if(d<min && isComing(s,b)){
				min=d;
				res = b;
			}
		}
		return res;
	}

	private static boolean isComing(Ship s, Ball b) {
		Ball b2 = new Ball(b.x+b.vx,b.y+b.vy,0,0,0);
		return dist(s,b) > dist(s,b2);
	}

	private static double dist(Ship s, Ball b) {
		return Math.sqrt((s.x-b.x)*(s.x-b.x) + (s.y-b.y)*(s.y-b.y));
	}
}

class Ship{
	double x;
	double y;
	double vx;
	double vy;
	int hp;
	int owner;
	public Ship(double x, double y, double vx, double vy, int hp, int owner) {
		super();
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.hp = hp;
		this.owner = owner;
	}
	@Override
	public String toString() {
		return "Ship [x=" + x + ", y=" + y + ", vx=" + vx + ", vy=" + vy + ", hp=" + hp + ", owner=" + owner + "]";
	}
	
}

class Ball{
	double x;
	double y;
	double vx;
	double vy;
	int tic;
	public Ball(double x, double y, double vx, double vy, int tic) {
		super();
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.tic = tic;
	}
	@Override
	public String toString() {
		return "Ball [x=" + x + ", y=" + y + ", vx=" + vx + ", vy=" + vy + ", tic=" + tic + "]";
	}
	
}
