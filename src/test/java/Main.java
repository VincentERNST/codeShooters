import com.codingame.gameengine.runner.GameRunner;

public class Main {
	
	private String[] avatars = new String[] { "16085713250612", "16085756802960", "16085734516701", "16085746254929",
			"16085763837151", "16085720641630", "16085846089817", "16085834521247" };
	
    public static void main(String[] args) {
    	
    	
    	
    	

        
        GameRunner gameRunner = new GameRunner();
        
        String avatarUrl1 = "https://static.codingame.com/servlet/fileservlet?id=10860609425191&format=navigation_avatar";
        gameRunner.addAgent(Player2.class,"Automaton2000", avatarUrl1);
        
        String avatarUrl2 =  "https://static.codingame.com/servlet/fileservlet?id=21040051085748&format=navigation_avatar";
        gameRunner.addAgent(Player2.class,"WhatTrickeryIsThis", avatarUrl2);
//        gameRunner.addAgent(Player1.class,"WhatTrickeryIsThis", avatarUrl2);
        
        // gameRunner.addAgent("python3 /home/user/player.py");
        
        gameRunner.start();
    }
}
