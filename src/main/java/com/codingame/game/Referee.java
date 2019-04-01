package com.codingame.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.Tooltip;
import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Rectangle;
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



//MOVE 774 61;SHOOT 1094 40;774 61 passe � travers :( pour un radius 10 ( d�part coll� au mur )


//nexts : 
//EXPLOSION SIZING
//HP rectangles
//Vortex :D
//Winning conditions
//Burning ship
//BackGround
//Refactor much
//unlimited ball


//Wood3 shoots
//Wood2 2 pods
//Wood1 Repair
//Bronze VORTEX

public class Referee extends AbstractReferee {
    @Inject private GameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    private List<Bullet> bullets = new ArrayList<Bullet>();
    private Shooter[] players = new Shooter[Constants.NUMBER_OF_PLAYERS];
    private TooltipModule tooltipModule;
  	static Rectangle staticHealthBar;
    static Rectangle dynamicHealthBar;
    
    @Override
    public Properties init(Properties params) {
    	  tooltipModule = new TooltipModule(gameManager);
        graphicEntityModule.createSprite()
                .setImage("Background.jpg")
                .setAnchor(0);
        
		//TODO Bandeau(Double Rectangles) HPs and Title
        for (Player player : gameManager.getPlayers()) {
        	//expose avatars 
            player.sendInputLine(String.format("%d", player.getIndex() + 1));
            graphicEntityModule.createText(player.getNicknameToken())
            		.setX(150 + (player.getIndex() % 2) * Constants.WIDTH - 550* (player.getIndex() % 2) )
                    .setY(50)
                    .setZIndex(0)
                    .setFontSize(30)
                    .setFillColor(player.getColorToken())
                    .setAnchor(0);
            
            
           
           
            
            //face
            graphicEntityModule.createSprite()
                    .setX(100 + (player.getIndex() % 2) * Constants.WIDTH - 200* (player.getIndex() % 2) )
                    .setY(100)
                    .setZIndex(20)
                    .setScale(2*Constants.PLAYER_RADIUS/100)
                    .setImage(player.getAvatarToken())
                    .setAnchor(0.5);
            
            //encircle face
            graphicEntityModule.createCircle()
	            .setX(100 + (player.getIndex() % 2) * Constants.WIDTH - 200* (player.getIndex() % 2) )
	            .setY(100)
	            .setRadius((int)(0.5*(Math.sqrt(2))*Constants.PLAYER_RADIUS -1))
	            .setLineWidth(2)
	            .setZIndex(501)
	            .setLineColor(player.getColorToken());
            
            
            //create units
            players[player.getIndex()] = UnitFactory.createShooter(Constants.WIDTH/4 + 2*(player.getIndex())*Constants.WIDTH/4,3*Constants.HEIGHT/4 -2*(player.getIndex() ) * Constants.HEIGHT/4,0,0);
            
            players[player.getIndex()].staticHealthBar = graphicEntityModule.createRectangle().setFillColor(0xE41515).setWidth(Constants.PLAYER_HP).setHeight(8)
            		.setY(110).setX(100 +(int)Constants.PLAYER_RADIUS + (player.getIndex() % 2) * Constants.WIDTH -Constants.PLAYER_HP*(player.getIndex() % 2)  - 200* (player.getIndex() % 2)-2*(player.getIndex() % 2)*((int)Constants.PLAYER_RADIUS) )
            		.setZIndex(10);
            players[player.getIndex()].dynamicHealthBar = graphicEntityModule.createRectangle().setFillColor(0x00FF00).setWidth(Constants.PLAYER_HP).setHeight(8)
            		.setY(110).setX(100 +(int)Constants.PLAYER_RADIUS + (player.getIndex() % 2) * Constants.WIDTH -Constants.PLAYER_HP*(player.getIndex() % 2)  - 200* (player.getIndex() % 2)-2*(player.getIndex() % 2)*((int)Constants.PLAYER_RADIUS) )
            		.setZIndex(11);
            
            //create player sprite
            Sprite s = graphicEntityModule.createSprite()
			.setX(Constants.WIDTH/4 + 2*(player.getIndex())*Constants.WIDTH/4 )
			.setY(3*Constants.HEIGHT/4 -2*(player.getIndex() ) * Constants.HEIGHT/4)
                    .setZIndex(20)
                    .setImage("alienspaceship.png")
                    .setScale(2*Constants.PLAYER_RADIUS/100)
                    .setAnchor(0.5);
            
            players[player.getIndex()].s=s;
            players[player.getIndex()].register(tooltipModule);
            
            
            //create message sprite for players
            String text = player.getIndex()==0 ? ":)" : ":(";
            Text msg = graphicEntityModule.createText(text)
                    .setX((int)players[player.getIndex()].x)
                    .setY((int)players[player.getIndex()].y - (int)Constants.PLAYER_RADIUS-10)
                    .setZIndex(500)
                    .setFontSize((int)(Constants.PLAYER_RADIUS/2))
                    .setFillColor(0xFFAC59)
                    .setAnchor(0.5);
            
            
           //create circle around player's sihp
           //plain version
//            graphicEntityModule.createCircle()
//	            .setX((int)players[player.getIndex()].x)
//	            .setY((int)players[player.getIndex()].y)
//	            .setRadius((int)(0.25*(1+Math.sqrt(2))*Constants.PLAYER_RADIUS))
//	            .setFillAlpha(0)
//	            .setLineWidth((int)(0.5*(-1+Math.sqrt(2))*Constants.PLAYER_RADIUS))
//	            .setZIndex(501)
//	            .setLineColor(player.getColorToken());
            
            Circle circle = graphicEntityModule.createCircle()
            .setX((int)players[player.getIndex()].x)
            .setY((int)players[player.getIndex()].y)
            .setRadius((int)(Constants.PLAYER_RADIUS -1))
            .setFillAlpha(0.15)
            .setFillColor(player.getColorToken())
            .setLineWidth(2)
            .setZIndex(501)
            .setLineColor(player.getColorToken()); 
            
            players[player.getIndex()].message=msg;
            players[player.getIndex()].circle=circle;
            
        }
        
        gameManager.setFrameDuration(500);
        return params;
    }
    

    
    
    @Override
    public void gameTurn(int turn) {//turn from 0 to end
    	System.err.println(" turn : "+turn);

        //send players inputs
    	for(Player player : gameManager.getPlayers()) {
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
    	}
	        
	    for(Player player : gameManager.getPlayers()) {      
	        player.execute();
	        Unit unit= players[player.getIndex()];
	        
	        gameManager.addTooltip(new Tooltip(player.getIndex() ,   player.getNicknameToken()+" has fired a bullet")); //TODO TOOLTIP EVENT Progress bar
	        // Read outputs
	        try {
	            String[] output = player.getOutputs().get(0).split(";");
	            
	            //move commands
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
					
		            Bullet b = UnitFactory.createBullet((int)unit.x, (int)unit.y,players[ player.getIndex() ].vx, players[ player.getIndex() ].vy);
		            //TODO Bullet inherit player speed
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
    	}
        
        //apply moves
	    ticBullets();
        playUnits();
        computeAOE();
        computeDeaths();
        
        //TODO HP BARS
        if(staticHealthBar==null) {
        	staticHealthBar = graphicEntityModule.createRectangle().setFillColor(0xE41515).setWidth(Constants.PLAYER_HP).setHeight(8).setY(50).setX(50).setZIndex(10);
        }
        if(dynamicHealthBar==null) {
        	dynamicHealthBar = graphicEntityModule.createRectangle().setFillColor(0x00FF00).setWidth(Constants.PLAYER_HP).setHeight(8).setY(500).setX(50).setZIndex(11);
        }
        
        graphicEntityModule.commitEntityState(0, staticHealthBar);
        graphicEntityModule.commitEntityState(0, dynamicHealthBar);
//        dynamicHealthBar.setScaleX(0.5,Curve.NONE);
        dynamicHealthBar.setX(51);
        graphicEntityModule.commitEntityState(0.5, dynamicHealthBar);
        dynamicHealthBar.setX(200);
        graphicEntityModule.commitEntityState(1, dynamicHealthBar);
        
        
        // check winner
        int winner = checkWinner();
        if (winner > 0 ) {
            gameManager.addToGameSummary(GameManager.formatSuccessMessage(gameManager.getPlayer(winner).getNicknameToken() + " won!"));
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
    
    private void computeDeaths() {
    	
		for (int i=bullets.size()-1; i>=0 ; i--){
			Bullet b = bullets.get(i);
			if(b.tic<0) {
				b.s.setVisible(false);
				bullets.remove(b);
			}
		}
		
		for(Shooter s : players) {
			if(!s.isAlive()) {
				s.hideSprites();
			}
		}
		
	}




	private void computeAOE() {
		for(Shooter p : players) {
			boolean getHit = false;
			for(Bullet b : bullets) {
    			if(b.tic==0 && Utils.distance(b, p)<Constants.EXPLOSION_RADIUS+Constants.PLAYER_RADIUS){
    				p.hp-=Constants.BULLET_AOE_DAMAGE;
    				p.s.setImage("Damaged.png");
    				getHit = true;
    			}
    		}
			if(!getHit) {
				p.s.setImage("alienspaceship.png");
			}
    	}
	}



	private int checkWinner() {
    	return 0;
    }
	
	private void ticBullets() {

		for (int i=bullets.size()-1; i>=0 ; i--){
			Bullet b = bullets.get(i);
			b.tic--;
			if(b.tic==0){
				b.explosion();
				graphicEntityModule.commitEntityState(0.5,b.s);
				continue;
			}
		}
		
	}

	private void playUnits() {
		List<Unit> units = new ArrayList<Unit>();
		units.addAll(Arrays.asList(players));
		units.addAll(bullets);
		commitAll(0,units);
		
		double t=0.0;
		System.err.println("   new collisions contest ");
		int round =1;
    	while(t<1.0 && round <100){
    		System.err.println("-------- round : "+t+"  -----------------");
    		round ++;
    		Collision c = Utils.getFirstCollisionFrom(units,t);
    		
    		if( c!=null && c.t>=t){
    			if(c.t==t) {
    				System.err.println("COLLSION IMMEDIATE !!");
    			}
    			System.err.println(c.u1);
    			System.err.println(c.u2);
    			moveAll(units, t,  c.t-t);
    			c.apply();
    			
    			
    			System.err.println(c.u1);
    			System.err.println(c.u2);
    			t= c.t;
    		}
    		if(c==null){
    			moveAll(units,t, 1-t);
    			t=1;
    		}
    		
    	}
    	endAll(units);
    	commitAll(1.0,units);
	}




    private void endAll(List<Unit> units) {
		for(Unit unit : units) {
			if(unit.isAlive()) {
				unit.end();
			}
			unit.register(tooltipModule);
		}
	}




	private void commitAll(double t, List<Unit> units) {
		for(Unit unit : units) {
			if(!unit.isAlive())
				continue;
			graphicEntityModule.commitEntityState(t, unit.s);
		}
	}




	private void moveAll(List<Unit> units,  double from, double duration) {
		for(Unit unit : units) {
			if(!unit.isAlive())
				continue;
			
			unit.move(duration);
			if( unit instanceof Shooter) {
				updateSprites((Shooter)unit,from+duration);
			}
			else {
		    	unit.s.setX((int) unit.x).setY((int) unit.y);
		    	commit(from+duration,unit);
			}
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
	


	private void commit(double t, Unit u) {
    	graphicEntityModule.commitEntityState(t, u.s);
	}
	
    private void updateSprites(Shooter p, double t) {
		p.s.setX((int) p.x).setY((int) p.y);
		p.s.setRotation(Utils.angle(p, new Point(p.x+p.vx, p.y+p.vy)));
		p.message.setX((int) p.x).setY((int) p.y- (int)Constants.PLAYER_RADIUS-10);
		p.circle.setX((int) p.x).setY((int) p.y);
		graphicEntityModule.commitEntityState(t, p.s);
//		graphicEntityModule.commitEntityState(t, p.message);//dont let comment go out of map
		graphicEntityModule.commitEntityState(t, p.circle);
		
		p.dynamicHealthBar.setScaleX(Math.min(1.0,Math.max(0,(double)Math.max(0,p.hp)/Constants.PLAYER_HP))+t*Constants.EPSILON);//t factor allows to bypass unexpected interpolation
		graphicEntityModule.commitEntityState(t, p.dynamicHealthBar);
	}
    
	
	private void draw(Bullet b, Point target) {
		if(b.s==null) {
	        b.s=graphicEntityModule.createSprite();
		}
		b.initSprite();
	}
	
}
