import com.codingame.gameengine.runner.GameRunner;

public class Main {
    public static void main(String[] args) {
        
        GameRunner gameRunner = new GameRunner();
        String avatarUrl1 = "https://static.codingame.com/servlet/fileservlet?id=10860609425191&format=navigation_avatar";
        gameRunner.addAgent(Player1.class,"Player1", avatarUrl1);
        String avatarUrl2 = "https://static.codingame.com/servlet/fileservlet?id=21040051085748&format=navigation_avatar";
        gameRunner.addAgent(Player2.class,"WhatTrickeryIsThis", avatarUrl2);
        
        // gameRunner.addAgent("python3 /home/user/player.py");
        
        gameRunner.start();
    }
}
