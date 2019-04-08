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

public class Referee extends AbstractReferee {
	
    @Inject private GameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;
    private List<Bullet> bullets = new ArrayList<Bullet>();
    private Shooter[] shooters = new Shooter[Constants.NUMBER_OF_PLAYERS * Constants.NUMBER_OF_SHIPS];
    private TooltipModule tooltipModule;
    private static Text hp11;
    private static Text hp12;
    private static Text hp21;
    private static Text hp22;
    private static Sprite backGround;
    private static Vortex vortex= new Vortex(-1, Constants.WIDTH/2, Constants.HEIGHT/2,200.0);
    
    @Override
    public Properties init(Properties params) {
    	
    	tooltipModule = new TooltipModule(gameManager);
        gameManager.setFrameDuration(500);
        
        initGraphics();
        return params;
    }


	@Override
    public void gameTurn(int turn) {//turn from 0 to end
    	
		//backGround graphical effects
		moveBackGround(turn);
    	resetMessages();
		
		//send alyers inputs
    	sendInputs(turn);
    	//apply outputs
    	readOutPuts();
	    
        //compute turn
    	List<Unit> units = new ArrayList<Unit>();
    	units.addAll(Arrays.asList(shooters));
    	units.addAll(bullets);
    	
	    ticBullets();
		vortex.attract(units);
		commitAll(0,units);
        computeTurn(units);
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
		
		for(Shooter shooter : shooters) {
			if(!shooter.isAlive() && shooter.s.isVisible()) {
		        gameManager.addTooltip(new Tooltip(shooter.id%2 ,   gameManager.getPlayers().get(shooter.id%2).getNicknameToken()+" Has Lost a Ship"));
		        shooter.die();
			}
		}
	}



	private void computeAOE() {
		//shooters get damaged
		for(Shooter p : shooters) {
			if(!p.isAlive())
				continue;
			boolean getHit = false;
			for(Bullet b : bullets) {
    			if(b.tic==0 && Utils.distance(b, p)<Constants.EXPLOSION_RADIUS+Constants.PLAYER_RADIUS){
    				p.hp-=Constants.BULLET_AOE_DAMAGE;
    				p.s.setImage("DamagedByAOE.png");
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

	private void computeTurn(List<Unit> units) {
		
		double t=0.0;
		
    	while(t<1.0){
    		
    		Collision c = Utils.getFirstCollisionFrom(units,t);
    		
    		if( c!=null && c.t>=t){
    			moveAll(units, t,  c.t-t);
    			c.apply();
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
		    	graphicEntityModule.commitEntityState(from+duration, unit.s);
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
 * 						   I/O                       			*
 * 																*
 * 																*
 * 																*
 * 																*
 * 																*
 * 																*
 * 					        * *	* 								**
 */
	
	private void sendInputs(int turn) {
    	for(Player player : gameManager.getPlayers()) {
    		//send ships inputs
    		player.sendInputLine(String.format("%d", Constants.NUMBER_OF_PLAYERS*Constants.NUMBER_OF_SHIPS));
	        for( Shooter shooter: shooters){
	        	player.sendInputLine(String.format("%d %d %d %d %d %d %d",shooter.id, shooter.owner, (int)shooter.x, (int)shooter.y, (int)shooter.vx, (int)shooter.vy, shooter.hp));
	        }
	        //send bullets inputs
	        player.sendInputLine(String.format("%d", bullets.size()));
	        for(Bullet b : bullets){
	        	player.sendInputLine(String.format("%d %d %d %d %d %d",b.id, (int)b.x, (int)b.y, (int)b.vx, (int)b.vy, b.tic));
	        }
    	}
	}

	private void readOutPuts() {
	    for(Player player : gameManager.getPlayers()) {      
	        player.execute();
	        
	        // Read outputs
	        try {
	            for(int i =0; i <player.getExpectedOutputLines();i++) {
	            	String out = player.getOutputs().get(i);  
	            	String[] output = out.split(";");
	            	Shooter unit= shooters[player.getIndex()+2*i];
	            	Shooter friend= shooters[player.getIndex()+2*(1-i)];
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
					}
					else{
						throw new Exception(" MOVE command is not properly set");
					}      
		            
		            //shoot
		            String shoot = output[1];
					if (shoot.split(" ")[0].equals("SHOOT")) {
						int targetShootX = Integer.parseInt(shoot.split(" ")[1]);
						int targetShootY = Integer.parseInt(shoot.split(" ")[2]);
						if( targetShootX == unit.x && targetShootY == unit.y) {
							gameManager.addToGameSummary(String.format("%s tried to shoot himself. Shoot is canceled.", player.getNicknameToken()));
						}
						else {
				            Bullet b = UnitFactory.createBullet((int)unit.x, (int)unit.y,unit.vx,unit.vy);
				            Point target = new Point(targetShootX , targetShootY);
				            Utils.aim(b, new Point(targetShootX , targetShootY),Constants.BULLET_THRUST);
				            bullets.add(b);
				            draw(b,target);
				            gameManager.addToGameSummary(String.format("Player %s played shoot (%d %d) ", player.getNicknameToken(), targetShootX, targetShootY));
						}
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
						String msg = output[2];
						if( msg.length()>15) {
							msg = msg.substring(0,13)+"...";
						}
						shooters[player.getIndex()+2*i].message.setText(msg);
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
        
	}


	private void checkWinner(int turn) {
		boolean win0=false,win1 =false;
		int winner = -1;
		if(!shooters[0].isAlive() && !shooters[2].isAlive()){
			win1=true;winner = 1;
		}
		if(!shooters[1].isAlive() && !shooters[3].isAlive()){
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
    	player0Hps+= shooters[0].isAlive()? shooters[0].hp : 0;
    	player0Hps+= shooters[2].isAlive()? shooters[0].hp : 0;
    	player1Hps+= shooters[1].isAlive()? shooters[1].hp : 0;
    	player1Hps+= shooters[3].isAlive()? shooters[3].hp : 0;
    	
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
	


	private void commitAll(double t, List<Unit> units) {
		for(Unit unit : units) {
			if(!unit.isAlive())
				continue;
			graphicEntityModule.commitEntityState(t, unit.s);
		}
	}

	private void updateHpBars() {
		//player1
		hp11.setText(shooters[0].hp+"");
		hp12.setText(shooters[2].hp+"");
		//player2
		hp21.setText(shooters[1].hp+"");
		hp22.setText(shooters[3].hp+"");
	}

    private void updateSprites(Shooter p, double t) {
		p.s.setX((int) p.x).setY((int) p.y);
		p.s.setRotation(Utils.angle(p, new Point(p.x+p.vx, p.y+p.vy)));
		p.message.setX((int) p.x).setY((int) p.y- (int)Constants.PLAYER_RADIUS-10);
		p.circle.setX((int) p.x).setY((int) p.y);
		graphicEntityModule.commitEntityState(t, p.s);
//		graphicEntityModule.commitEntityState(t, p.message);//dont let comment go out of map
		graphicEntityModule.commitEntityState(t, p.circle);
		
		p.dynamicHealthBar.setScaleX(Math.min(1.0,Math.max(0,(double)Math.max(0,p.hp)/Constants.BASE_PLAYER_HP)) +t*Constants.EPSILON);//t factor allows to bypass unexpected interpolation
		graphicEntityModule.commitEntityState(t, p.dynamicHealthBar);
	}
    
	
	private void draw(Bullet b, Point target) {
		if(b.s==null) {
	        b.s=graphicEntityModule.createSprite();
		}
		b.initSprite();
	}

    
    
    private void initGraphics() {
  	  //backGround
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
    	  //send player id
          player.sendInputLine(String.format("%d", player.getIndex()));
          
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
        	    shooters[player.getIndex()+2*i] = UnitFactory.createShooter(player.getIndex(), Constants.WIDTH/4 + 2*(player.getIndex())*Constants.WIDTH/4,  Constants.HEIGHT/4*(4*i*player.getIndex()+3-2*i-2*player.getIndex()),  0,0);
 
	            //create hp bars
        	    shooters[player.getIndex()+2*i].staticHealthBar = graphicEntityModule.createRectangle().setFillColor(0xE41515).setWidth(Constants.BASE_PLAYER_HP).setHeight(8)
	            		.setY(110+i*50).setX(100 +(int)Constants.PLAYER_RADIUS + (player.getIndex() % 2) * Constants.WIDTH -Constants.BASE_PLAYER_HP*(player.getIndex() % 2)  - 200* (player.getIndex() % 2)-2*(player.getIndex() % 2)*((int)Constants.PLAYER_RADIUS) )
	            		.setZIndex(100);
        	    shooters[player.getIndex()+2*i].dynamicHealthBar = graphicEntityModule.createRectangle().setFillColor(0x00FF00).setWidth(Constants.BASE_PLAYER_HP).setHeight(8)
	            		.setY(110+i*50).setX(100 +(int)Constants.PLAYER_RADIUS + (player.getIndex() % 2) * Constants.WIDTH -Constants.BASE_PLAYER_HP*(player.getIndex() % 2)  - 200* (player.getIndex() % 2)-2*(player.getIndex() % 2)*((int)Constants.PLAYER_RADIUS) )
	            		.setZIndex(100);
	            
	            //create ship sprite
	            Sprite s = graphicEntityModule.createSprite()
				.setX(Constants.WIDTH/4 + 2*(player.getIndex())*Constants.WIDTH/4 )
				.setY(Constants.HEIGHT/4*(4*i*player.getIndex()+3-2*i-2*player.getIndex()))
	                    .setZIndex(20)
	                    .setImage("alienspaceship.png")
	                    .setScale(2*Constants.PLAYER_RADIUS/100)
	                    .setAnchor(0.5);
	            
	            shooters[player.getIndex()+2*i].s=s;
	            shooters[player.getIndex()+2*i].register(tooltipModule);
	            
	            
	            //create message sprite for players
	            String text = player.getIndex()==0 ? ":)" : ":(";
	            Text msg = graphicEntityModule.createText(text)
	                    .setX((int)shooters[player.getIndex()+2*i].x)
	                    .setY((int)shooters[player.getIndex()+2*i].y - (int)Constants.PLAYER_RADIUS-10)
	                    .setZIndex(500)
	                    .setFontSize((int)(Constants.PLAYER_RADIUS/2))
	                    .setFillColor(0xFFAC59)
	                    .setAnchor(0.5);
	            shooters[player.getIndex()+2*i].message=msg;
	            
	            
	           //create circle around player's face
	            Circle circle = graphicEntityModule.createCircle()
	            .setX((int)shooters[player.getIndex()+2*i].x)
	            .setY((int)shooters[player.getIndex()+2*i].y)
	            .setRadius((int)(Constants.PLAYER_RADIUS -1))
	            .setFillAlpha(0.15)
	            .setFillColor(player.getColorToken())
	            .setLineWidth(2)
	            .setZIndex(20)
	            .setLineColor(player.getColorToken()); 
	            shooters[player.getIndex()+2*i].circle=circle;
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
              .setAnchor(0);
      hp22 = graphicEntityModule.createText("100")
              	.setX(Constants.WIDTH - 220 - 2*(int)Constants.PLAYER_RADIUS)
              	.setY(150)
              	.setFontSize(30)
              	.setFillColor(gameManager.getPlayers().get(1).getColorToken())
              	.setAnchor(0);
              		
	}

	private void moveBackGround(int turn) {
    	vortex.s.setRotation(((turn+1)%4)*0.5*Math.PI);
    	updateHpBars();
    	backGround.setAlpha(0.5+0.2*(turn%2));
	}
	
	private void resetMessages() {
		for(Shooter shooter : shooters) {
			if(shooter.isAlive() && shooter.s.isVisible()) {
				shooter.message.setText("");
			}
		}
	}

}
