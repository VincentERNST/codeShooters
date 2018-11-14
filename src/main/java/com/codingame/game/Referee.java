package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.google.inject.Inject;

import pojo.Bullet;
import pojo.Constants;
import pojo.Point;
import pojo.Unit;
import pojo.Utils;

public class Referee extends AbstractReferee {
    @Inject private GameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    private List<Bullet> bullets = new ArrayList<Bullet>();
    private List<Sprite> bulletsSprites = new ArrayList<Sprite>();
    private Sprite[] playersSprites = new Sprite[Constants.NUMBER_OF_PLAYERS];
    private Unit[] players = new Unit[Constants.NUMBER_OF_PLAYERS];
    
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    @Override
    public Properties init(Properties params) {
        // Display the background image. The asset image must be in the directory src/main/resources/view/assets
        graphicEntityModule.createSprite()
                .setImage("Background.jpg")
                .setAnchor(0);
        
        
        Random r = new Random();
        	
        for (Player player : gameManager.getPlayers()) {
        	//expose avatars 
            player.sendInputLine(String.format("%d", player.getIndex() + 1));
            graphicEntityModule.createText(player.getNicknameToken())
                    .setX(180 + (player.getIndex() % 2) * 1400)
                    .setY(50 + 100 * (player.getIndex() / 2))
                    .setZIndex(20)
                    .setFontSize(90)
                    .setFillColor(player.getColorToken())
                    .setAnchor(0);

            graphicEntityModule.createSprite()
                    .setX(100 + (player.getIndex() % 2) * 1400)
                    .setY(90 + 100 * (player.getIndex() / 2))
                    .setZIndex(20)
                    .setImage(player.getAvatarToken())
                    .setAnchor(0.5);

            //create units
            players[player.getIndex()] = new Unit(player.getIndex(), WIDTH/4 + 2*(player.getIndex())*WIDTH/4,3*HEIGHT/4 -2*(player.getIndex() ) * HEIGHT/4,0,0,Constants.PLAYER_AMORT);
            
            //create player sprite
            Sprite s = graphicEntityModule.createSprite()
			.setX(WIDTH/4 + 2*(player.getIndex())*WIDTH/4 )
			.setY(3*HEIGHT/4 -2*(player.getIndex() ) * HEIGHT/4)
                    .setZIndex(20)
                    .setAnchor(0.5);
            
            if(player.getIndex() ==1) {s.setImage("pitlord.jpg");}//player2 img
            else{ s.setImage("test.png");}
            playersSprites[player.getIndex()] = s;
            
        }
        

        gameManager.setFrameDuration(500);

        return params;
    }

    private int checkWinner() {
    	return 0;
    }

    @Override
    public void gameTurn(int turn) {
    	//turn from 0 to end
    	
        //send players inputs
    	Player player = gameManager.getPlayer(turn % gameManager.getPlayerCount());
        player.sendInputLine(String.format("%d", Constants.NUMBER_OF_PLAYERS));
        Unit unit= players[turn % gameManager.getPlayerCount()];
        player.sendInputLine(String.format("%d %d %d %d",(int)unit.x, (int)unit.y, (int)unit.vx, (int)unit.vy));
        Unit enemy= players[gameManager.getPlayerCount()-1-turn % gameManager.getPlayerCount()];
        player.sendInputLine(String.format("%d %d %d %d",(int)enemy.x, (int)enemy.y, (int)enemy.vx, (int)enemy.vy));
        
        //send bullets inputs
        player.sendInputLine(String.format("%d", bullets.size()));
        for(Bullet b : bullets){
        	player.sendInputLine(String.format("%d %d %d %d", (int)b.x, (int)b.y, (int)b.vx, (int)b.vy));
        }
        
        player.execute();
        
        // Read inputs
        //todo send player positions
        try {
            String[] output = player.getOutputs().get(0).split(" ");
            
            int targetShootX = Integer.parseInt(output[0]);
            int targetShootY = Integer.parseInt(output[1]);
            Bullet b = new Bullet(bullets.size(),(int)unit.x, (int)unit.y, 20, 20);
            Point target = new Point(targetShootX , targetShootY);
            Utils.aim(b, new Point(targetShootX , targetShootY),300.0);
            bullets.add(b);
            draw(b,target);
            
            gameManager.addToGameSummary(String.format("Player %s played shoot (%d %d) ", player.getNicknameToken(), targetShootX, targetShootY));

        } catch (NumberFormatException e) {
            player.deactivate("Wrong output!");
            player.setScore(-1);
            gameManager.endGame();
        } catch (TimeoutException e) {
            gameManager.addToGameSummary(GameManager.formatErrorMessage(player.getNicknameToken() + " timeout!"));
            player.deactivate(player.getNicknameToken() + " timeout!");
            player.setScore(-1);
            gameManager.endGame();
        }
        
        //TODO
        //animate Bullets
        moveBullets();
        //drawBullets();

        //TODO
        //animate players
        

        // check winner
        int winner = checkWinner();
        if (winner > 0 ) {
            gameManager.addToGameSummary(GameManager.formatSuccessMessage(player.getNicknameToken() + " won!"));
            gameManager.getPlayer(winner - 1).setScore(1);
            gameManager.endGame();
        }
        //check tie
        if( turn > 15) {
        	gameManager.addToGameSummary(GameManager.formatSuccessMessage(" Tie "));
        	//TODO tie breaker
        	gameManager.getPlayer(0).setScore(1);
        	gameManager.getPlayer(1).setScore(1);
        	gameManager.endGame();
        }
        
    }

	private void draw(Bullet b, Point target) {
		double angle = Utils.angle(b, target);
        Sprite s = graphicEntityModule.createSprite()
        .setX((int) b.x)
        .setY((int) b.y)
        .setImage("bullet.png")
        .setRotation(angle)
        .setAnchor(0.5);  
        bulletsSprites.add(s);
	}

	private void drawBullets() {
		
		for(int i=0; i< bullets.size(); i++){
        	Sprite s = bulletsSprites.get(i);
        	Bullet b = bullets.get(i);
            graphicEntityModule.commitEntityState(0, s);
            s.setX( (int) b.x)
            .setY( (int) b.y)
            .setImage("bullet.png")
            .setAnchor(0.5); 
            graphicEntityModule.commitEntityState(1, s);
        }		
		
	}
	
	private void moveBullets() {
		for(Bullet b : bullets){
			
			Sprite s = bulletsSprites.get(b.id);
			graphicEntityModule.commitEntityState(0, s);
			
				double ty0 = b.vy==0 ? 10.0 : b.y /-b.vy ;
				double ty1 = b.vy==0 ? 10.0 : (1080 - b.y) /b.vy ;
				
				double tx0 = b.vx==0 ? 10.0 : b.x /-b.vx ;
				double tx1 = b.vx==0 ? 10.0 : (1920 - b.x) /b.vx ;
				
				if(ty0 < 1.0 && ty0>0) {
					bounce(b,s,Constants.VERTICAL,ty0);
				}
				else if(ty1 < 1.0 && ty1>0) {
					bounce(b,s,Constants.VERTICAL,ty1);
				}
				else if(tx0 < 1.0 && tx0>0 ) {
					bounce(b,s,Constants.HORIZONTAL,tx0);
				}
				else if(tx1 < 1.0 && tx1>0) {
					bounce(b,s,Constants.HORIZONTAL,tx1);
				}
				else {
					b.move(1.0);
					b.end();
		            s.setX( (int) b.x)
		            .setY( (int) b.y)
		            .setImage("bullet.png")
		            .setAnchor(0.5); 
		            graphicEntityModule.commitEntityState(1, s); 
				}
		}
	}

	private void bounce(Bullet b,Sprite s, int direction, double t) {
		 
		b.move(t);
		b.end();
		if(direction == Constants.HORIZONTAL) {b.vx=-b.vx;}
		if(direction == Constants.VERTICAL) {b.vy=-b.vy;}
		
		s.setX( (int) b.x)
		.setY( (int) b.y)
		.setImage("bullet.png")
		.setAnchor(0.5); 
		graphicEntityModule.commitEntityState(t, s);
		
		b.move(1.0-t);
		s.setX( (int) b.x)
		.setY( (int) b.y)
		.setImage("bullet.png")
		.setAnchor(0.5); 
		graphicEntityModule.commitEntityState(1, s); 
	}
	
	 
    
}
