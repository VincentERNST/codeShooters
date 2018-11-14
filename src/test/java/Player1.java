import java.util.Random;
import java.util.Scanner;

public class Player1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int myId = scanner.nextInt();
        int[][] grid = new int[3][3];
        Random random = new Random();

        while (true) {
            //players
            int m = scanner.nextInt();
            for(int i = 0;i<m;i++){
            	int x = scanner.nextInt();
            	int y = scanner.nextInt();
            	int vx = scanner.nextInt();
            	int vy = scanner.nextInt();
            	System.err.println(x+"  "+y+" "+vx+" "+vy);
            }           
            
            
            //bullets
            int n = scanner.nextInt();
            for(int i = 0;i<n;i++){
            	int bulletX = scanner.nextInt();
            	int bulletY = scanner.nextInt();
            	int bulletVX = scanner.nextInt();
            	int bulletVY = scanner.nextInt();
            	System.err.println(bulletX+"  "+bulletY);
            }
        	
            
            
            
             int  x = random.nextInt(1920);
             int  y = random.nextInt(1080);

            System.out.println(String.format("%d %d",x ,y ));
        }
    }
}
