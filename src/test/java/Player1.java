import java.util.Random;
import java.util.Scanner;

public class Player1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int myId = scanner.nextInt();
        int[][] grid = new int[3][3];
        Random random = new Random();

        while (true) {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    grid[r][c] = scanner.nextInt();
                }
            }
            int n = scanner.nextInt();
            for(int i = 0;i<n;i++){
            	int bulletX = scanner.nextInt();
            	int bulletY = scanner.nextInt();
            	int bulletVX = scanner.nextInt();
            	int bulletVY = scanner.nextInt();
            	System.err.println(bulletX+"  "+bulletY);
            }
            int r, c,x, y;
            do {
                r = random.nextInt(3);
                c = random.nextInt(3);
                x = random.nextInt(1920);
                y = random.nextInt(1080);
            } while (grid[r][c] != 0);

            System.out.println(String.format("%d %d %d %d", r, c,x ,y ));
        }
    }
}
