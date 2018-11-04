package pojo;

public class Wall extends Unit{
	
	public int direction;
	
	public Wall(int x, int y,  int dir) {
		super(x, y, 0, 0);
		this.direction = dir;
	}
	
	

}
