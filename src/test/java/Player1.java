import java.util.Random;
import java.util.Scanner;

public class Player1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int myId = scanner.nextInt();
        
        Random random = new Random();
        int turn = 0;
     whileloop :    while (true) {
        	turn ++;

        	
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
            }
            
            
             int  x = random.nextInt(1920);
             int  y = random.nextInt(1080);
             
             int  mx = random.nextInt(1920);
             int  my = random.nextInt(1080);
             
             
         	if(turn == 1){
                for(int i=0;i<m;i++){
                	System.out.println("MOVE 774 61;SHOOT 1094 40;774 61");
                }
        		continue whileloop;
        	}
        	if(turn%5 == 3){
                for(int i=0;i<m;i++){
                	System.out.println("MOVE 774 61;HEAL");
                }
        		continue whileloop;
        	}
        	
        	
            for(int i=0;i<m;i++){
            	System.out.println(String.format("MOVE %d %d;SHOOT %d %d;"+mx+" "+my,mx ,my,x,y ));
            }
        }
    }
}
