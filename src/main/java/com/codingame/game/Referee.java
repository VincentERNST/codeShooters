package com.codingame.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.swing.tree.ExpandVetoException;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.Tooltip;
import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;
import pojo.Bomb;
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
    private List<Bomb> bombs = new ArrayList<Bomb>();
//    private Shooter[] shooters = new Shooter[Constants.NUMBER_OF_PLAYERS * Constants.NUMBER_OF_SHIPS];
    private ArrayList<Shooter> shooters = new ArrayList<Shooter>();
    
    private TooltipModule tooltipModule;
    private static Sprite backGround;
    public static Vortex vortex= new Vortex(-11, Constants.WIDTH/2, Constants.HEIGHT/2,Constants.VORTEX_RADIUS);
    
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
		
    	if(turn%20==0 && turn >0){//adds
    		int nbr = 1+(int)turn/20;//nbr is the count of ships for a team
    	  	Shooter addShooterTeam0 = UnitFactory.createShooter(0,nbr);
    	  	shooters.add(addShooterTeam0);
    	  	drawShooter(addShooterTeam0,nbr);
    	  	Shooter addShooterTeam1 = UnitFactory.createShooter(1,nbr);
    	    shooters.add(addShooterTeam1);
    	    drawShooter(addShooterTeam1,nbr);
    	}
    	
    	
		//send alyers inputs
    	sendInputs(turn);
    	//apply outputs
    	readOutPuts();
	    
        //compute turn
    	List<Unit> units = new ArrayList<Unit>();
    	units.addAll(shooters);
    	units.addAll(bombs);
    	
	    ticBullets();
		vortex.attract(units);
		commitAll(0,units);
        computeTurn(units);
        computeAOE();
        computeDeaths();
        updateHpCount();
        ticInvuls();
        
        // check winner
        checkWinner(turn);
        
        //check tie
        if(turn >= Constants.MAX_GAME_TURN) {
        	tieBreaker();
        }
        
    }

	private void computeDeaths() {
    	
		for (int i=bombs.size()-1; i>=0 ; i--){
			Bomb b = bombs.get(i);
			if(b.tic==-1) {
				b.fade();
			}
			if(b.tic==-2) {
				b.s.setVisible(false);
				bombs.remove(b);
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
			if(!p.isAlive() || p.invulTimer>0)
				continue;
			boolean getHit = false;
			for(Bomb b : bombs) {
    			if(b.tic==0 && Utils.distance(b, p)<Constants.EXPLOSION_RADIUS+Constants.PLAYER_RADIUS){
    				p.hp-=Constants.BOMB_AOE_DAMAGE;
    				p.s.setImage("DamagedByAOE.png");
    				getHit = true;
    			}
    		}
			if(!getHit) {
				p.s.setImage("alienspaceship.png");
			}
    	}
		
		//chain reactions
		for(Bomb b1 : bombs) {
			if(b1.tic!=0)
				continue;
			for(Bomb b2 : bombs) {
				if(!b2.isAlive())
					continue;
				if(b1.tic==0 && Utils.distance(b1, b2)<Constants.EXPLOSION_RADIUS+Constants.BOMB_RADIUS){
					b2.tic=1;
				}
			}
		}
		
		
	}


    
	private void ticBullets() {

		for (int i=bombs.size()-1; i>=0 ; i--){
			Bomb b = bombs.get(i);
			b.tic--;
			if(b.tic==0){
				b.explosion();
				graphicEntityModule.commitEntityState(0.5,b.s);
				continue;
			}
		}
		
	}
	
	
	private void ticInvuls() {
		for (Shooter s : shooters){
			s.invulTimer--;
			if(s.invulTimer==0){
				s.circle.setLineColor(gameManager.getPlayer(s.owner).getColorToken()); 
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
    		player.setExpectedOutput(shooters.size()/2);
    		//send ships inputs
    		player.sendInputLine(String.format("%d", shooters.size()));
	        for( Shooter shooter: shooters){
	        	player.sendInputLine(String.format("%d %d %d %d %d %d %d %d",shooter.id, shooter.owner, (int)shooter.x, (int)shooter.y, (int)shooter.vx, (int)shooter.vy, shooter.hp, shooter.invulTimer));
	        }
	        //send bombs inputs
	        player.sendInputLine(String.format("%d", bombs.size()));
	        for(Bomb b : bombs){
	        	player.sendInputLine(String.format("%d %d %d %d %d %d",b.id, (int)b.x, (int)b.y, (int)b.vx, (int)b.vy, b.tic));
	        }
    	}
	}
	

	private void readOutPuts() {
	    for(Player player : gameManager.getPlayers()) {      
	        player.execute();
	        
	        ArrayList<Shooter> ships = shooters.stream()
	        		.filter(s->s.owner==player.getIndex())
	        		.sorted(Comparator.comparing(s->s.id))
	        		.collect(Collectors.toCollection(ArrayList<Shooter>::new));
	        
	        
	        // Read outputs
	        try {
	        	int expectedOutPutLines = shooters.size()/2;
	            for(int i =0; i <expectedOutPutLines;i++) {
	            	String out = player.getOutputs().get(i);  
	            	String[] output = out.split(";");
	            	
	            	Shooter unit= ships.get(i);
	            	
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
		            
		            //shoot command
		            if(output.length<=1){continue;}
		            String command = output[1];
					if (command.split(" ")[0].equals("SHOOT")) {
						int targetShootX = Integer.parseInt(command.split(" ")[1]);
						int targetShootY = Integer.parseInt(command.split(" ")[2]);
						if( targetShootX == unit.x && targetShootY == unit.y) {
							gameManager.addToGameSummary(String.format("%s tried to shoot himself. Shoot is canceled.", player.getNicknameToken()));
						}
						else {
				            Bomb b = UnitFactory.createBomb((int)unit.x, (int)unit.y,unit.vx,unit.vy);
				            Point target = new Point(targetShootX , targetShootY);
				            Utils.aim(b, new Point(targetShootX , targetShootY),Constants.BOMB_THRUST);
				            bombs.add(b);
				            draw(b,target);
				            gameManager.addToGameSummary(String.format("Player %s played shoot (%d %d) ", player.getNicknameToken(), targetShootX, targetShootY));
						}
					}
					//heal command
					else if (command.split(" ")[0].equals("HEAL")) {
						int friendId = Integer.parseInt(command.split(" ")[1]);
						if(friendId<0 || friendId >=shooters.size() ){
							throw new Exception(" Wrong id for heal command");
						}
						Shooter friend = shooters.get(friendId);
						if(!friend.isAlive()){
							gameManager.addToGameSummary(String.format("Player %s played HEAL without effect since target is dead", player.getNicknameToken()));
						}
						else if(friend.owner != unit.owner){
							gameManager.addToGameSummary(String.format("Player %s played HEAL without effect, aiming wrong team", player.getNicknameToken()));
						}
						else if(friend.id == unit.id){
							gameManager.addToGameSummary(String.format("Player %s played HEAL without effect, self-heal is not allowed", player.getNicknameToken()));
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
						unit.message.setText(msg);
					}
	            }
		       } catch (NumberFormatException e) {
		           player.deactivate("Wrong output! Number is expected");
		           player.setScore(-1);
		           gameManager.endGame();
		       } catch (TimeoutException e) {
		           gameManager.addToGameSummary(GameManager.formatErrorMessage(player.getNicknameToken() + " timeout!"));
		           player.deactivate(player.getNicknameToken() + " timeout!");
		           player.setScore(-1);
		           gameManager.endGame();
		       }catch (Exception e) {
		           gameManager.addToGameSummary(GameManager.formatErrorMessage(player.getNicknameToken() + e.getMessage()));
		           player.deactivate(player.getNicknameToken() + " timeout!"+e.getMessage());
		           player.setScore(-1);
		           gameManager.endGame();
		       }
		        
    	}
        
	}


	private void checkWinner(int turn) {
		boolean win0=false,win1 =false;
		int winner = -1;
		
		List<Shooter> team0 = shooters.stream()
        		.filter(s->s.owner==0 && s.isAlive())
        		.collect(Collectors.toList());
		
		List<Shooter> team1 = shooters.stream()
				.filter(s->s.owner==1 && s.isAlive())
				.collect(Collectors.toList());
		
		
		if(team0.isEmpty()){
			win1=true;winner = 1;
		}
		if(team1.isEmpty()){
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
		int score0 = shooters.stream()
        		.filter(s->s.owner==0 && s.isAlive())
        		.map(s->s.hp)
        		.reduce(0, (a,b)-> a+b);
		
		int score1 = shooters.stream()
				.filter(s->s.owner==1 && s.isAlive())
				.map(s->s.hp)
				.reduce(0, (a,b)-> a+b);
		
    	if(score0>score1){
			gameManager.addToGameSummary(GameManager.formatSuccessMessage(gameManager.getPlayer(0).getNicknameToken() + " won on HP tie breaker!"));
			gameManager.getPlayer(0).setScore(1);
    	}
    	if(score0<score1){
    		gameManager.addToGameSummary(GameManager.formatSuccessMessage(gameManager.getPlayer(1).getNicknameToken() + " won on HP tie breaker!"));
    		gameManager.getPlayer(1).setScore(1);
    	}
    	if(score0==score1){
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

	private void updateHpCount() {
		for(Shooter s: shooters){
			s.hpText.setText(s.hp+"");
		}
	}

    private void updateSprites(Shooter p, double t) {
		p.s.setX((int) p.x).setY((int) p.y);
		p.s.setRotation(Utils.angle(p, new Point(p.x+p.vx, p.y+p.vy)));
		p.message.setX((int) p.x).setY((int) p.y- (int)Constants.PLAYER_RADIUS-10);
		p.circle.setX((int) p.x).setY((int) p.y);
		graphicEntityModule.commitEntityState(t, p.s);
//		graphicEntityModule.commitEntityState(t, p.message);// comment dont go out of map with this tricky commented line
		graphicEntityModule.commitEntityState(t, p.circle);
		
		p.dynamicHealthBar.setScaleX(Math.min(1.0,Math.max(0,(double)Math.max(0,p.hp)/Constants.BASE_PLAYER_HP)) +t*Constants.EPSILON);//t factor allows to bypass unexpected interpolation
		graphicEntityModule.commitEntityState(t, p.dynamicHealthBar);
	}
    
	
	private void draw(Bomb b, Point target) {
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
  				.setScale(2*Constants.VORTEX_RADIUS/100)
  				.setAnchor(0.5)
  				.setZIndex(1);
  	  //vortex center
	  	graphicEntityModule.createSprite()
		    .setImage("BlackHoleBlue.png")
			.setX(Constants.WIDTH/2)
			.setY(Constants.HEIGHT/2)
			.setScale(2*Constants.VORTEX_RADIUS/300)
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
        	  	Shooter shooter = UnitFactory.createShooter(player.getIndex(),i);
        	    shooters.add(shooter);
        	    drawShooter(shooter,i);

          } 
      }
              		
	}

	private void drawShooter(Shooter shooter,int nbr) {
		int owner = shooter.owner;
		int x = (int)shooter.x;
		int y = (int)shooter.y;
        //create hp bars
	    shooter.staticHealthBar = graphicEntityModule.createRectangle().setFillColor(0xE41515).setWidth(Constants.BASE_PLAYER_HP).setHeight(8)
        		.setY(110+nbr*50)
        		.setX((int) (100 +Constants.PLAYER_RADIUS + owner*(Constants.WIDTH -Constants.BASE_PLAYER_HP   - 200 -2*Constants.PLAYER_RADIUS)) )
        		.setZIndex(100);
        		
        		
	    shooter.dynamicHealthBar = graphicEntityModule.createRectangle().setFillColor(0x00FF00).setWidth(Constants.BASE_PLAYER_HP).setHeight(8)
        		.setY(110+nbr*50)
        		.setX((int) (100 +Constants.PLAYER_RADIUS + owner*(Constants.WIDTH -Constants.BASE_PLAYER_HP   - 200 -2*Constants.PLAYER_RADIUS)) )
        		.setZIndex(100);
        
        //create ship sprite
        Sprite s = graphicEntityModule.createSprite()
		.setX(x)
		.setY(y)
                .setZIndex(20)
                .setImage("alienspaceship.png")
                .setScale(2*Constants.PLAYER_RADIUS/100)
                .setAnchor(0.5);
        
        shooter.s=s;
        shooter.register(tooltipModule);
        
        
        //create message sprite for players
        String text = owner==0 ? "=]" : "=[";
        Text msg = graphicEntityModule.createText(text)
                .setX(x)
                .setY(y- (int)Constants.PLAYER_RADIUS-10)
                .setZIndex(500)
                .setFontSize((int)(Constants.PLAYER_RADIUS/2))
                .setFillColor(0xFFAC59)
                .setAnchor(0.5);
        shooter.message=msg;
        
        
       //create circle around player's ship
        int color = gameManager.getPlayer(owner).getColorToken();
        Circle circle = graphicEntityModule.createCircle()
        .setX((int)shooter.x)
        .setY((int)shooter.y)
        .setRadius((int)(Constants.PLAYER_RADIUS -1))
        .setFillAlpha(0.15)
        .setFillColor(color)
        .setLineWidth(2)
        .setZIndex(20)
        .setLineColor(shooter.invulTimer>0 ? 0xFFFF00 : color); 
        shooter.circle=circle;
        
        //set up hp count
        shooter.hpText = graphicEntityModule.createText("100")
        		.setX((int) (165 +2*Constants.PLAYER_RADIUS +owner*(Constants.WIDTH -385 - 4*Constants.PLAYER_RADIUS)))
        		.setY(100+nbr*50)
                .setFontSize(30)
                .setFillColor(color)
                .setAnchor(0);
        
	}


	private void moveBackGround(int turn) {
    	vortex.s.setRotation(((turn+1)%4)*0.5*Math.PI);
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
