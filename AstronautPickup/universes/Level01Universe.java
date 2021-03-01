import java.awt.Rectangle;
import java.util.ArrayList;

public class Level01Universe implements Universe {

	private final int ASTRONAUTS = 25;
	private final int ASTEROIDS = 50;
	private final int INITIAL_UFOS = 1;
	private final int UFO_FREQUENCY = 500;
	private final double UFO_RANGE = 5000;
	private final double UFO_HALF_RANGE = UFO_RANGE / 2;
	
	private boolean complete = false;	
	private Background background = null;	
	private SpaceShipSprite player1 = null;
	private ArrayList<DisplayableSprite> sprites = new ArrayList<DisplayableSprite>();
	private ArrayList<DisplayableSprite> disposalList = new ArrayList<DisplayableSprite>();
	private double xCenter = 0;
	private double yCenter = 0;
	protected long elapsedTime = 0;
	protected long updates = 0;
	
	
	public Level01Universe () {
		
		super();		
		this.setXCenter(0);
		this.setYCenter(0);
		
		background = new StarfieldBackground();
		
		//add random Astronauts
		for (int i = 0; i < ASTRONAUTS; i++) {
			//only add Astronauts within a certain distance of the player starting point
			double x = Math.random() * UFO_RANGE - UFO_HALF_RANGE;
			double y = Math.random() * UFO_RANGE - UFO_HALF_RANGE;
			//random velocity and rotation
			double velocityX = Math.random() * 100 - 50;
			double velocityY = Math.random() * 100 - 50;
			double rotation = Math.random() * 120 - 60;
			
			AstronautSprite astronaut = new AstronautSprite(x, y,velocityX, velocityY, rotation );
			sprites.add(astronaut);
		}
		
		//add random Asteroids
		for (int i = 0; i < ASTEROIDS; i++) {
			double x = Math.random() * UFO_RANGE - UFO_HALF_RANGE;
			double y = Math.random() * UFO_RANGE - UFO_HALF_RANGE;
			double velocityX = Math.random() * 200 - 50;
			double velocityY = Math.random() * 200 - 50;
			double rotation = Math.random() * 60 - 60;
			
			AsteroidSprite asteroid = new AsteroidSprite(x, y,velocityX, velocityY, rotation );
			sprites.add(asteroid);
		}
						
		player1 = new SpaceShipSprite(0,0);
		sprites.add(player1);

		for (int i = 0; i < INITIAL_UFOS; i++) {
			spawnUFOSprite();
		}

	}
	
	private void spawnUFOSprite() {
		double x = player1.getCenterX() + (Math.random() * UFO_RANGE - UFO_HALF_RANGE);
		double y = player1.getCenterY() + Math.random() * UFO_RANGE - UFO_HALF_RANGE;
		double velocityX = Math.random() * 100 - 200;
		double velocityY = Math.random() * 100 - 100;

		//do not spawn too close to the player!
		x += Math.signum(x) * 500;
		y += Math.signum(y) * 500;
		
		UFOSprite ufo = new UFOSprite(x, y,velocityX, velocityY);
		sprites.add(ufo);
	}
	
	public boolean centerOnPlayer() {
		return true;
	}		

	public void update(KeyboardInput keyboard, long actual_delta_time) {

		elapsedTime += actual_delta_time;
		updates++;

		//escape ends this universe
		if (keyboard.keyDownOnce(27)) {
			complete = true;
		}
		
		//spawn new UFO after a given # of updates
		if (updates % UFO_FREQUENCY == 0) {
			spawnUFOSprite();
		}
		
		updateSprites(keyboard, actual_delta_time);
		wrapStraySprites();
		disposeSprites();
	}

	 protected void updateSprites(KeyboardInput keyboard, long actual_delta_time) {

			if (player1.getHealth() < 0) {
				//end of game
				complete = true;
			}
			
			//update each sprite in turn
			for (int i = 0; i < sprites.size(); i++) {
				DisplayableSprite sprite = sprites.get(i);
	    		sprite.update(this, keyboard, actual_delta_time);
	    	}    	
	    }

	    protected void disposeSprites() {
	        
	    	//collect a list of sprites to dispose
	    	//this is done in a temporary list to avoid a concurrent modification exception
			for (int i = 0; i < sprites.size(); i++) {
				DisplayableSprite sprite = sprites.get(i);
	    		if (sprite.getDispose() == true) {
	    			disposalList.add(sprite);
	    		}
	    	}
			
			//go through the list of sprites to dispose
			//note that the sprites are being removed from the original list
			for (int i = 0; i < disposalList.size(); i++) {
				DisplayableSprite sprite = disposalList.get(i);
				sprites.remove(sprite);
				System.out.println("Remove: " + sprite.toString());
	    	}
			
			//clear disposal list if necessary
	    	if (disposalList.size() > 0) {
	    		disposalList.clear();
	    	}
	    }
	 
	 
	private void wrapStraySprites() {
		
		//Find Astronaut and Asteroid sprites that have 1) strayed outside a box around the player
		//and 2) are moving away from the player. Move these sprites from the edge to the opposite edge, thereby
		//keeping them within range
		for (DisplayableSprite sprite : this.sprites) {
			if (sprite instanceof AstronautSprite || sprite instanceof AsteroidSprite) {
				//only move sprites if they are far enough away to not be visible...
				DisplayableSprite displayable = (DisplayableSprite)sprite;
				MovableSprite movable = (MovableSprite)sprite;
				double distanceX = sprite.getCenterX() - this.player1.getCenterX();
				double distanceY = sprite.getCenterY() - this.player1.getCenterY();
				if (distanceX > UFO_HALF_RANGE) {
					movable.setCenterX(displayable.getCenterX() - UFO_RANGE);
				}
				if (distanceX < -UFO_HALF_RANGE) {
					movable.setCenterX(displayable.getCenterX() + UFO_RANGE);
				}
				if (distanceY > UFO_HALF_RANGE) {
					movable.setCenterY(displayable.getCenterY() - UFO_RANGE);
				}
				if (distanceY < -UFO_HALF_RANGE) {
					movable.setCenterY(displayable.getCenterY() + UFO_RANGE);
				}				
			}			
		}
		
	}

	public double getScale() {
		return 1;
	}

	public double getXCenter() {
		return xCenter;
	}


	public double getYCenter() {
		return yCenter;
	}
	
	public void setXCenter(double xCenter) {
		this.xCenter = xCenter;
	}

	public void setYCenter(double yCenter) {
		this.yCenter = yCenter;
	}
	
	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	@Override
	public Background getBackground() {
		return this.background;
	}

	@Override
	public DisplayableSprite getPlayer1() {
		return this.player1;
	}

	@Override
	public ArrayList<DisplayableSprite> getSprites() {
		return this.sprites;
	}	
	
}
