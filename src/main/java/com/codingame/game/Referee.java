package com.codingame.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.Tooltip;
import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

import pojo.Bullet;
import pojo.Point;
import pojo.Shooter;
import pojo.Unit;
import pojo.Vortex;
import utils.Collision;
import utils.Constants;
import utils.UnitFactory;
import utils.Utils;
import view.TooltipModule;



//MOVE 774 61;SHOOT 1094 40;774 61 passe à travers :( pour un radius 10 ( départ collé au mur )


//nexts : 
//Refactor much
//TODOs

//Rules order : 
//Wood3 shoots
//Wood2 2 people
//Wood1 heal / missile
//Bronze VORTEx

public class Referee extends AbstractReferee {
    @Inject private GameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    private List<Bullet> bullets = new ArrayList<Bullet>();
    private Shooter[] players = new Shooter[Constants.NUMBER_OF_PLAYERS * Constants.NUMBER_OF_SHIPS];
    private TooltipModule tooltipModule;
    private static Text hp11;
    private static Text hp12;
    private static Text hp21;
    private static Text hp22;
    private static Sprite backGround;
    private static Vortex vortex= new Vortex(-1, Constants.WIDTH/2, Constants.HEIGHT/2,200.0);
    
    @Override
    public Properties init(Properties params) {
    	  //backGround
    	  tooltipModule = new TooltipModule(gameManager);
    	  backGround = graphicEntityModule.createSprite()
                .setImage("Omega_Centauri.jpg")
                .setAnchor(0);
        
    	  
    	  //create Vortex sprite
    	  vortex.s = graphicEntityModule.createSprite()
    			    .setImage("vortex.png")
    				.setX(Constants.WIDTH/2)
    				.setY(Constants.HEIGHT/2)
    				.setScale(2)
    				.setAnchor(0.5)
    				.setZIndex(1);
    	  
    	  for (Player player : gameManager.getPlayers()) {
        	
    		//WhatTrickeryIsThis ?
            player.sendInputLine(String.format("%d", player.getIndex() + 1));
            
            //Name
            graphicEntityModule.createText(player.getNicknameToken())
            		.setX(150 + (player.getIndex() % 2) * Constants.WIDTH - 550* (player.getIndex() % 2) )
                    .setY(50)
                    .setZIndex(100)
                    .setFontSize(30)
                    .setFillColor(player.getColorToken())
                    .setAnchor(0);
            
            //face
            graphicEntityModule.createSprite()
                    .setX(100 + (player.getIndex() % 2) * Constants.WIDTH - 200* (player.getIndex() % 2) )
                    .setY(100)
                    .setZIndex(100)
                    .setScale(2*Constants.PLAYER_RADIUS/100)
                    .setImage(player.getAvatarToken())
                    .setAnchor(0.5);
            
            //Encircle face
            graphicEntityModule.createCircle()
	            .setX(100 + (player.getIndex() % 2) * Constants.WIDTH - 200* (player.getIndex() % 2) )
	            .setY(100)
	            .setRadius((int)(0.5*(Math.sqrt(2))*Constants.PLAYER_RADIUS -1))
	            .setLineWidth(2)
	            .setZIndex(100)
	            .setLineColor(player.getColorToken());
            
            
            for(int i = 0; i<Constants.NUMBER_OF_SHIPS ; i++) {
	            
	            //create units
            	System.err.println("building  : "+(player.getIndex()+2*i));
	            players[player.getIndex()+2*i] = UnitFactory.createShooter(Constants.WIDTH/4 + 2*(player.getIndex())*Constants.WIDTH/4,  Constants.HEIGHT/4*(4*i*player.getIndex()+3-2*i-2*player.getIndex()),  0,0);

	            //create hp bars
	            players[player.getIndex()+2*i].staticHealthBar = graphicEntityModule.createRectangle().setFillColor(0xE41515).setWidth(Constants.PLAYER_HP).setHeight(8)
	            		.setY(110+i*50).setX(100 +(int)Constants.PLAYER_RADIUS + (player.getIndex() % 2) * Constants.WIDTH -Constants.PLAYER_HP*(player.getIndex() % 2)  - 200* (player.getIndex() % 2)-2*(player.getIndex() % 2)*((int)Constants.PLAYER_RADIUS) )
	            		.setZIndex(100);
	            players[player.getIndex()+2*i].dynamicHealthBar = graphicEntityModule.createRectangle().setFillColor(0x00FF00).setWidth(Constants.PLAYER_HP).setHeight(8)
	            		.setY(110+i*50).setX(100 +(int)Constants.PLAYER_RADIUS + (player.getIndex() % 2) * Constants.WIDTH -Constants.PLAYER_HP*(player.getIndex() % 2)  - 200* (player.getIndex() % 2)-2*(player.getIndex() % 2)*((int)Constants.PLAYER_RADIUS) )
	            		.setZIndex(100);
	            
	            //create ship sprite
	            Sprite s = graphicEntityModule.createSprite()
				.setX(Constants.WIDTH/4 + 2*(player.getIndex())*Constants.WIDTH/4 )
				.setY(Constants.HEIGHT/4*(4*i*player.getIndex()+3-2*i-2*player.getIndex()))
	                    .setZIndex(20)
	                    .setImage("alienspaceship.png")
	                    .setScale(2*Constants.PLAYER_RADIUS/100)
	                    .setAnchor(0.5);
	            
	            players[player.getIndex()+2*i].s=s;
	            players[player.getIndex()+2*i].register(tooltipModule);
	            
	            
	            //create message sprite for players
	            String text = player.getIndex()==0 ? ":)" : ":(";
	            Text msg = graphicEntityModule.createText(text)
	                    .setX((int)players[player.getIndex()+2*i].x)
	                    .setY((int)players[player.getIndex()+2*i].y - (int)Constants.PLAYER_RADIUS-10)
	                    .setZIndex(500)
	                    .setFontSize((int)(Constants.PLAYER_RADIUS/2))
	                    .setFillColor(0xFFAC59)
	                    .setAnchor(0.5);
	            players[player.getIndex()+2*i].message=msg;
	            
	            
	           //create circle around player's face
	            Circle circle = graphicEntityModule.createCircle()
	            .setX((int)players[player.getIndex()+2*i].x)
	            .setY((int)players[player.getIndex()+2*i].y)
	            .setRadius((int)(Constants.PLAYER_RADIUS -1))
	            .setFillAlpha(0.15)
	            .setFillColor(player.getColorToken())
	            .setLineWidth(2)
	            .setZIndex(20)
	            .setLineColor(player.getColorToken()); 
	            players[player.getIndex()+2*i].circle=circle;
            } 
        }
        //set up hp counts
        hp11 = graphicEntityModule.createText("100")
        		.setX(165 +2*(int)Constants.PLAYER_RADIUS )
        		.setY(100)
                .setFontSize(30)
                .setFillColor(gameManager.getPlayers().get(0).getColorToken())
                .setAnchor(0);
        hp12 = graphicEntityModule.createText("100")
        		.setX(165 +2*(int)Constants.PLAYER_RADIUS )
        		.setY(150)
        		.setFontSize(30)
        		.setFillColor(gameManager.getPlayers().get(0).getColorToken())
        		.setAnchor(0);
        
        hp21 = graphicEntityModule.createText("100")
        		.setX(Constants.WIDTH - 220 - 2*(int)Constants.PLAYER_RADIUS)
        		.setY(100)
                .setFontSize(30)
                .setFillColor(gameManager.getPlayers().get(1).getColorToken())
                .setAnchor(0);;
        hp22 = graphicEntityModule.createText("100")
                	.setX(Constants.WIDTH - 220 - 2*(int)Constants.PLAYER_RADIUS)
                	.setY(150)
                	.setFontSize(30)
                	.setFillColor(gameManager.getPlayers().get(1).getColorToken())
                	.setAnchor(0);;
                		
        gameManager.setFrameDuration(500);
        return params;
    }
    

    
    
    @Override
    public void gameTurn(int turn) {//turn from 0 to end
    	
    	System.err.println(" turn : "+turn);
    	vortex.s.setRotation(((turn+1)%4)*0.5*Math.PI);
    	updateHps();
    	backGround.setAlpha(0.5+0.2*(turn%2));
    	
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
	        
	        // Read outputs
	        try {
	            for(int i =0; i <player.getExpectedOutputLines();i++) {
	            	String out = player.getOutputs().get(i);  
	            	String[] output = out.split(";");
	            	Shooter unit= players[player.getIndex()+2*i];
	            	Shooter friend= players[player.getIndex()+2*(1-i)];
	            	if(!unit.isAlive())continue;
		            //move commands
		            String move = output[0];
		            if(move.equals("WAIT")){
		            	gameManager.addToGameSummary(String.format("Player %s is waiting ", player.getNicknameToken()));
		            }
					else if (move.split(" ")[0].equals("MOVE")) {
						int targetMoveX = Integer.parseInt(move.split(" ")[1]);
						int targetMoveY = Integer.parseInt(move.split(" ")[2]);
						gameManager.addToGameSummary(String.format("Player %s played Move (%d %d) ", player.getNicknameToken(),targetMoveX,targetMoveY));
						Utils.aim(unit  , new Point(targetMoveX , targetMoveY),Constants.PLAYER_THRUST);
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
						
			            Bullet b = UnitFactory.createBullet((int)unit.x, (int)unit.y,unit.vx,unit.vy);
			            Point target = new Point(targetShootX , targetShootY);
			            Utils.aim(b, new Point(targetShootX , targetShootY),Constants.BULLET_THRUST);
			            bullets.add(b);
			            draw(b,target);
					}
					else if (shoot.split(" ")[0].equals("HEAL")) {
						if(!friend.isAlive()){
							gameManager.addToGameSummary(String.format("Player %s played HEAL without effect since ally is dead", player.getNicknameToken()));
						}
						else{
							friend.heal();
							gameManager.addToGameSummary(String.format("Player %s played HEAL", player.getNicknameToken()));
						}
					}
					else{
						throw new Exception(" Secound command is not properly set");
					}     
					
					//comment 
					if(output.length>2) {
						players[player.getIndex()+2*i].message.setText(output[2]);
					}
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
        
        
        
        // check winner
        checkWinner(turn);
        
        //check tie
        if(turn > Constants.GAME_TURN) {
        	tieBreaker();
        }
        
    }





	private void computeDeaths() {
    	
		for (int i=bullets.size()-1; i>=0 ; i--){
			Bullet b = bullets.get(i);
			if(b.tic==-1) {
				b.fade();
			}
			if(b.tic==-2) {
				b.s.setVisible(false);
				bullets.remove(b);
			}
		}
		
		for(Shooter s : players) {
			if(!s.isAlive() && s.s.isVisible()) {
		        gameManager.addTooltip(new Tooltip(s.id%2 ,   gameManager.getPlayers().get(s.id%2).getNicknameToken()+" Has Lost a Ship")); //TODO TOOLTIP EVENT Progress bar
				s.hideSprites();
			}
		}
	}




	private void updateHps() {
		//player1
		hp11.setText(players[0].hp+"");
		hp12.setText(players[2].hp+"");
		//player2
		hp21.setText(players[1].hp+"");
		hp22.setText(players[3].hp+"");
		
	}


	private void computeAOE() {
		//shooters get damaged
		for(Shooter p : players) {
			if(!p.isAlive())
				continue;
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
		
		
		//chain reactions
		for(Bullet b1 : bullets) {
			if(b1.tic!=0)
				continue;
			for(Bullet b2 : bullets) {
				if(!b2.isAlive())
					continue;
				if(b1.tic==0 && Utils.distance(b1, b2)<Constants.EXPLOSION_RADIUS+Constants.BULLET_RADIUS){
					b2.tic=1;
				}
			}
		}
		
		
	}



	private void checkWinner(int turn) {
		boolean win0=false,win1 =false;
		int winner = -1;
		if(!players[0].isAlive() && !players[2].isAlive()){
			win1=true;winner = 1;
		}
		if(!players[1].isAlive() && !players[3].isAlive()){
			win0=true;winner = 0;
		}
		if( win0 && win1 ){
			gameManager.addToGameSummary(GameManager.formatSuccessMessage(" Tie "));
			gameManager.endGame();
			return;
		}
		if(win1 || win0){
			gameManager.addToGameSummary(GameManager.formatSuccessMessage(gameManager.getPlayer(winner).getNicknameToken() + " won!"));
			gameManager.getPlayer(winner).setScore(1);
			gameManager.endGame();
		}
    }

    
    private void tieBreaker() {
    	gameManager.addToGameSummary(GameManager.formatSuccessMessage(" Tie "));
    	//tie breaker
    	int player0Hps = 0,player1Hps = 0;
    	player0Hps+= players[0].isAlive()? players[0].hp : 0;
    	player0Hps+= players[2].isAlive()? players[0].hp : 0;
    	player1Hps+= players[1].isAlive()? players[1].hp : 0;
    	player1Hps+= players[3].isAlive()? players[3].hp : 0;
    	
    	if(player0Hps>player1Hps){
			gameManager.addToGameSummary(GameManager.formatSuccessMessage(gameManager.getPlayer(0).getNicknameToken() + " won on HP tie breaker!"));
    	}
    	if(player0Hps<player1Hps){
    		gameManager.addToGameSummary(GameManager.formatSuccessMessage(gameManager.getPlayer(1).getNicknameToken() + " won on HP tie breaker!"));
    	}
    	if(player0Hps==player1Hps){
    		gameManager.addToGameSummary(GameManager.formatSuccessMessage(" Tie "));
    	}
    	
    	gameManager.endGame();
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
		vortex.attract(units);
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
    				System.err.println("COLLISION IMMEDIATE !!");
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
		
		p.dynamicHealthBar.setScaleX(Math.min(1.0,Math.max(0,(double)Math.max(0,p.hp)/Constants.PLAYER_HP)) +t*Constants.EPSILON);//t factor allows to bypass unexpected interpolation
		graphicEntityModule.commitEntityState(t, p.dynamicHealthBar);
	}
    
	
	private void draw(Bullet b, Point target) {
		if(b.s==null) {
	        b.s=graphicEntityModule.createSprite();
		}
		b.initSprite();
	}
	
}
