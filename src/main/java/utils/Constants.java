package utils;

import pojo.Point;

public final class Constants {
	
	public static final int MAX_GAME_TURN = 100;
	public static int NUMBER_OF_PLAYERS = 2;
	public static int NUMBER_OF_SHIPS = 2;
	public static int INVUL_TIMER = 5;
	public static int WIDTH = 1920;
	public static int HEIGHT = 1080;
	public static int HORIZONTAL = 0;
	public static int VERTICAL = 1;
//	public static double EPSILON = 0.00001;
	public static double EPSILON = 0.;
	public static int BOMB_POOL_SIZE = 36;
	
	public static int BASE_PLAYER_HP = 100;
	public static double PLAYER_AMORT = 0.7;
	public static final double PLAYER_THRUST = 60.;
	public static double PLAYER_RADIUS = 50.0;//60
	
	
	public static int BOMB_TIC = 6;
	public static double BOMB_RADIUS = 10.0;
	public static double BOMB_AMORT = 0.8;
	public static int BOMB_DAMAGE = 10;
	public static double EXPLOSION_RADIUS = 150.0;
	public static double BOMB_THRUST = 150.0;
	public static int BOMB_AOE_DAMAGE = 2;
	
	public static final double VORTEX_THRUST = 300.;
	public static final double VORTEX_RADIUS = 100.;
	
	public static int FIRST_TURN = 200;
	
	
	public static int HEAL = 5;
	public static Point center = new Point(WIDTH/2,HEIGHT/2);
	
}
