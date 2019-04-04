package utils;

import pojo.Point;

public final class Constants {
	
	public static int NUMBER_OF_PLAYERS = 2;
	public static int NUMBER_OF_SHIPS = 2;
	public static int WIDTH = 1920;
	public static int HEIGHT = 1080;
	public static int HORIZONTAL = 0;
	public static int VERTICAL = 1;
	public static int BULLET_POOL_SIZE = 16;
	public static int BULLET_TIC = 6;
	public static double EPSILON = 0.00001;
	public static double BULLET_AMORT = 0.8;
	public static double PLAYER_AMORT = 0.9;
	public static int PLAYER_HP = 100;
	public static final double PLAYER_THRUST = 100.;
	public static final double VORTEX_THRUST = 1000.;
	public static double BULLET_RADIUS = 10.0;
	public static double PLAYER_RADIUS = 50.0;//60
	public static double EXPLOSION_RADIUS = 200.0;
	public static int BULLET_DAMAGE = 10;
	public static int BULLET_AOE_DAMAGE = 2;
	public static int PLAYER_BASE_HP = 100;
	public static Point center = new Point(WIDTH/2,HEIGHT/2);
	
}
