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
import pojo.Constants;
import pojo.Point;
import pojo.Unit;
import pojo.Utils;
import view.TooltipModule;

public class Referee extends AbstractReferee {
    @Inject private GameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    private List<Bullet> bullets = new ArrayList<Bullet>();
    private Unit[] players = new Unit[Constants.NUMBER_OF_PLAYERS];
    private TooltipModule tooltipModule;
    
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    @Override
    public Properties init(Properties params) {
    	  tooltipModule = new TooltipModule(gameManager);
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
            players[player.getIndex()] = new Unit(player.getIndex(), WIDTH/4 + 2*(player.getIndex())*WIDTH/4,3*HEIGHT/4 -2*(player.getIndex() ) * HEIGHT/4,0,0,Constants.PLAYER_RADIUS,Constants.PLAYER_AMORT,Constants.UNIT_TYPE_PLAYER);
            
            //create player sprite
            Sprite s = graphicEntityModule.createSprite()
			.setX(WIDTH/4 + 2*(player.getIndex())*WIDTH/4 )
			.setY(3*HEIGHT/4 -2*(player.getIndex() ) * HEIGHT/4)
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
//                    .setFontSize(30)
                    .setFontSize(60)
                    .setFillColor(0xFFAC59)
                    .setAnchor(0.5);
            
            players[player.getIndex()].message=msg;
            
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
				
	            Bullet b = new Bullet(bullets.size(),(int)unit.x, (int)unit.y, 20, 20);
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

	private void drawBullets() {
		
		for(int i=0; i< bullets.size(); i++){
        	Bullet b = bullets.get(i);
            graphicEntityModule.commitEntityState(0, b.s);
            b.s.setX( (int) b.x)
            .setY( (int) b.y)
            .setImage("bullet.png")
            .setAnchor(0.5); 
            graphicEntityModule.commitEntityState(1, b.s);
        }		
		
	}
	
	private void moveBullets() {
		for(Bullet b : bullets){
			
			graphicEntityModule.commitEntityState(0, b.s);
			
				double ty0 = b.vy==0 ? 1.1 : b.y+b.r /-b.vy ;
				double ty1 = b.vy==0 ? 1.1 : (1080 - b.y-b.r) /b.vy ;
				
				double tx0 = b.vx==0 ? 1.1 : b.x+b.r /-b.vx ;
				double tx1 = b.vx==0 ? 1.1 : (1920 - b.x-b.r) /b.vx ;
				
				if(ty0 < 1.0 && ty0>0) {
					bounce(b, Constants.VERTICAL,ty0);
				}
				else if(ty1 < 1.0 && ty1>0) {
					bounce(b, Constants.VERTICAL,ty1);
				}
				else if(tx0 < 1.0 && tx0>0 ) {
					bounce(b, Constants.HORIZONTAL,tx0);
				}
				else if(tx1 < 1.0 && tx1>0) {
					bounce(b, Constants.HORIZONTAL,tx1);
				}
				else {
					b.move(1.0);
					b.end();
					b.s.setX( (int) b.x)
		            .setY( (int) b.y)
		            .setAnchor(0.5); 
		            graphicEntityModule.commitEntityState(1, b.s); 
				}
				
				b.register(tooltipModule);//update tooltip for next turn
		}
	}
	
	

	private void movePlayers() {
		
		for(Unit p : players){
			
			graphicEntityModule.commitEntityState(0, p.s);
				double ty0 = p.vy==0 ? 10.0 : p.y /-p.vy ;
				double ty1 = p.vy==0 ? 10.0 : (HEIGHT - p.y) /p.vy ;
				
				double tx0 = p.vx==0 ? 10.0 : p.x /-p.vx ;
				double tx1 = p.vx==0 ? 10.0 : (WIDTH - p.x) /p.vx ;
				
				if(ty0 < 1.0 && ty0>0) {
					bounce(p, Constants.VERTICAL,ty0);
				}
				else if(ty1 < 1.0 && ty1>0) {
					bounce(p, Constants.VERTICAL,ty1);
				}
				else if(tx0 < 1.0 && tx0>0 ) {
					bounce(p, Constants.HORIZONTAL,tx0);
				}
				else if(tx1 < 1.0 && tx1>0) {
					bounce(p, Constants.HORIZONTAL,tx1);
				}
				else {
					p.move(1.0);
					p.end();
					p.s.setX( (int) p.x)
		            .setY( (int) p.y)
		            .setAnchor(0.5); 
		            graphicEntityModule.commitEntityState(1, p.s); 
		            
		            
		            p.message.setX( (int) p.x)
		            .setY( (int) p.y-100)
		            .setAnchor(0.5); 
		            graphicEntityModule.commitEntityState(1,p.message); 
				}
				
			p.register(tooltipModule);//update tooltip for next turn
		}
		
	}
	
	
	
	private void bounce(Unit b , int direction, double t) {
		 
		b.move(t);
		b.end();
		if(direction == Constants.HORIZONTAL) {b.vx=-b.vx;}
		if(direction == Constants.VERTICAL) {b.vy=-b.vy;}
		
		b.s.setX( (int) b.x)
		.setY( (int) b.y);
		graphicEntityModule.commitEntityState(t, b.s);
		
		b.move(1.0-t);
		b.s.setX( (int) b.x)
		.setY( (int) b.y);
		graphicEntityModule.commitEntityState(1, b.s);
	}
	
//    public static Collision CollisionMurale(Unit u, double from){
//		double tx = 2.0;
//		double ty = tx;
//		
//		double r= u.r;
//		
//		if(u.unitType==1 && u.y<5450  && 2050 <u.y){
//			r=0.0;
//		}
//		
//		if (u.x + u.vx < r) {
//			tx = (r - u.x) / u.vx;
//		} else if (u.x + u.vx > WIDTH - r) {
//			tx = (WIDTH - r - u.x) / u.vx;
//		}
//
//		if (u.y + u.vy < r) {
//			ty = (r - u.y) / u.vy;
//		} else if (u.y + u.vy > HEIGHT - r) {
//			ty = (HEIGHT - r - u.y) / u.vy;
//		}
//
//		int dir = -1;
//		double t = -1.0;
//
//		if (tx < ty) {
//			dir = HORIZONTAL;
//			t = tx;
//		} else {
//			dir = VERTICAL;
//			t = ty;
//		}
//		t+=from;
//		if (t <= from || t > 1.0) {
//			return null;
//		}
//		if (dir == HORIZONTAL) {
//			return new Collision(u, MurH, t);
//		} else {
//			return new Collision(u, MurV, t);
//		}
//	} 
    
}
