package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.Tooltip;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

import pojo.Bullet;
import pojo.Point;
import pojo.Shooter;
import pojo.Unit;
import pojo.UnitFactory;
import utils.Collision;
import utils.Constants;
import utils.Utils;
import view.TooltipModule;

public class Referee extends AbstractReferee {
    @Inject private GameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    private List<Bullet> bullets = new ArrayList<Bullet>();
    private Shooter[] players = new Shooter[Constants.NUMBER_OF_PLAYERS];
    private TooltipModule tooltipModule;
    private Random r = new Random();

    @Override
    public Properties init(Properties params) {
    	  tooltipModule = new TooltipModule(gameManager);
        // Display the background image. The asset image must be in the directory src/main/resources/view/assets
        graphicEntityModule.createSprite()
                .setImage("Background.jpg")
                .setAnchor(0);
        
        	
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
            players[player.getIndex()] = UnitFactory.createShooter(Constants.WIDTH/4 + 2*(player.getIndex())*Constants.WIDTH/4,3*Constants.HEIGHT/4 -2*(player.getIndex() ) * Constants.HEIGHT/4,0,0);
            
            //create player sprite
            Sprite s = graphicEntityModule.createSprite()
			.setX(Constants.WIDTH/4 + 2*(player.getIndex())*Constants.WIDTH/4 )
			.setY(3*Constants.HEIGHT/4 -2*(player.getIndex() ) * Constants.HEIGHT/4)
                    .setZIndex(20)
                    .setAnchor(0.5);
            
            if(player.getIndex() ==1) {s.setImage("pitlord.jpg");}//player2 img
            else{ s.setImage("test.png");}
            players[player.getIndex()].s=s;
            players[player.getIndex()].register(tooltipModule);
            
            
            //create message sprite for players
            String text = player.getIndex()==0 ? ":)" : ":(";
            Text msg = graphicEntityModule.createText(text)
                    .setX((int)players[player.getIndex()].x)
                    .setY((int)players[player.getIndex()].y - 100)
                    .setZIndex(20)
                    .setFontSize(60)
                    .setFillColor(0xFFAC59)
                    .setAnchor(0.5);
            
            players[player.getIndex()].message=msg;
            
        }
        
        gameManager.setFrameDuration(500);
        return params;
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
        gameManager.addTooltip(new Tooltip(player.getIndex() ,   player.getNicknameToken()+" has won one cell")); // TOOLTIP EVENT Progress bar
        
        // Read inputs
        //todo send player positions
        try {
            String[] output = player.getOutputs().get(0).split(";");
            
            //move
            String move = output[0];
            if(move.equals("WAIT")){
            	gameManager.addToGameSummary(String.format("Player %s is waiting ", player.getNicknameToken()));
            }
			else if (move.split(" ")[0].equals("MOVE")) {
				int targetMoveX = Integer.parseInt(move.split(" ")[1]);
				int targetMoveY = Integer.parseInt(move.split(" ")[2]);
				gameManager.addToGameSummary(String.format("Player %s played Move (%d %d) ", player.getNicknameToken(),targetMoveX,targetMoveY));
				Utils.aim(players[ player.getIndex() ]  , new Point(targetMoveX , targetMoveY),100.0);
				
//				players[player.getIndex()].message.setText(targetMoveX+ " "+targetMoveY);
				 
			}
			else{
				throw new Exception(" MOVE command is not properly set");
			}      
            
            //shoot
            String shoot = output[1];
			if (shoot.split(" ")[0].equals("SHOOT")) {
				int targetShootX = Integer.parseInt(shoot.split(" ")[1]);
				int targetShootY = Integer.parseInt(shoot.split(" ")[2]);
				gameManager.addToGameSummary(String.format("Player %s played shoot (%d %d) ", player.getNicknameToken(), targetShootX, targetShootY));
				
	            Bullet b = UnitFactory.createBullet((int)unit.x, (int)unit.y);
	            Point target = new Point(targetShootX , targetShootY);
	            Utils.aim(b, new Point(targetShootX , targetShootY),300.0);
	            bullets.add(b);
	            draw(b,target);
			}
			else{
				throw new Exception(" SHOOT command is not properly set");
			}     
			
			//comment 
			if(output.length>2) {
				players[player.getIndex()].message.setText(output[2]);
			}
			
			
        } catch (NumberFormatException e) {
            player.deactivate("Wrong output!");
            player.setScore(-1);
            gameManager.endGame();
        } catch (TimeoutException e) {
            gameManager.addToGameSummary(GameManager.formatErrorMessage(player.getNicknameToken() + " timeout!"));
            player.deactivate(player.getNicknameToken() + " timeout!");
            player.setScore(-1);
            gameManager.endGame();
        }catch (Exception e) {
            gameManager.addToGameSummary(GameManager.formatErrorMessage(player.getNicknameToken() + e.getMessage()));
            player.deactivate(player.getNicknameToken() + " timeout!");
            player.setScore(-1);
            gameManager.endGame();
        }
        
        moveBullets();
        movePlayers();

        // check winner
        int winner = checkWinner();
        if (winner > 0 ) {
            gameManager.addToGameSummary(GameManager.formatSuccessMessage(player.getNicknameToken() + " won!"));
            gameManager.getPlayer(winner - 1).setScore(1);
            gameManager.endGame();
        }
        //check tie
        if(turn > 15) {
        	gameManager.addToGameSummary(GameManager.formatSuccessMessage(" Tie "));
        	//TODO tie breaker
        	gameManager.getPlayer(0).setScore(1);
        	gameManager.getPlayer(1).setScore(1);
        	gameManager.endGame();
        }
        
    }

    
    
    private int checkWinner() {
    	return 0;
    }
    


	
	private void moveBullets() {
		for (Bullet b : bullets) {

			graphicEntityModule.commitEntityState(0, b.s);
			Collision collisionMurale = Utils.CollisionMurale(b, 0.0);
			if (collisionMurale != null) {

				b.move(collisionMurale.t);
				b.end();
				collisionMurale.apply();

				b.s.setX((int) b.x).setY((int) b.y);
				graphicEntityModule.commitEntityState(collisionMurale.t, b.s);

				b.move(1.0 - collisionMurale.t);
				b.s.setX((int) b.x).setY((int) b.y);
				graphicEntityModule.commitEntityState(1, b.s);

			} else {
				b.move(1.0);
				b.end();
				b.s.setX((int) b.x).setY((int) b.y).setAnchor(0.5);
				graphicEntityModule.commitEntityState(1, b.s);
			}

			b.register(tooltipModule);// update tooltip for next turn
		}
	}
	

	
	
	private void movePlayers() {
		
		for(Shooter p : players){
			
			commit(0,p);
			Collision collisionMurale = Utils.CollisionMurale(p, 0.0);
			
			if (collisionMurale != null) {

				p.move(collisionMurale.t);
				collisionMurale.apply();
				updateSprites(p,collisionMurale.t);

				p.move(1.0 - collisionMurale.t);
				updateSprites(p,1.0);

			} else {
				
				p.move(1.0);
				updateSprites(p,1.0);
				
			}
				
			p.register(tooltipModule);//update tooltip for next turn
		}
	}
	
	
	
/** 						* *	* 								**
 * 																*
 * 																*
 * 																*
 * 																*
 * 																*
 * 																*
 * 						   GRAPHICS                    			*
 * 																*
 * 																*
 * 																*
 * 																*
 * 																*
 * 																*
 * 					        * *	* 								**
 */
	



	
    private void updateSprites(Shooter p, double t) {
		p.s.setX((int) p.x).setY((int) p.y);
		commit(t,p);
		
		p.message.setX( (int) p.x).setY( (int) p.y-100);
        commit(t,p);
	}

	private void commit(double t, Unit u) {
    	graphicEntityModule.commitEntityState(t, u.s);
	}
	
	private void draw(Bullet b, Point target) {
		double angle = Utils.angle(b, target);
        Sprite s = graphicEntityModule.createSprite()
        .setX((int) b.x)
        .setY((int) b.y)
        .setImage("bullet.png")
        .setRotation(angle)
        .setAnchor(0.5);  
        b.s=s;
	}
}
