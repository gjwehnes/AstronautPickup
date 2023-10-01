import java.awt.Rectangle;
import java.util.ArrayList;

public class Level01Universe implements Universe {

	private int astronauts = 0;
	private int target = 0;
	private int asteroids = 0;
	private int ufo_frequency = 0;
	
	private final double ASTRONAUT_RANGE = 2500;
	private final double ASTRONAUT_HALF_RANGE = ASTRONAUT_RANGE / 2;
	private final double UFO_RANGE = 5000;
	private final double UFO_HALF_RANGE = UFO_RANGE / 2;
	
	private boolean complete = false;
	private boolean successful = false;
	private Background background = null;	
	private ArrayList<Background> backgrounds = null;
	private SpaceShipSprite player1 = null;
	private ArrayList<DisplayableSprite> sprites = new ArrayList<DisplayableSprite>();
	private ArrayList<DisplayableSprite> disposalList = new ArrayList<DisplayableSprite>();
	protected long elapsedTime = 0;
	protected long updates = 0;
	

	public Level01Universe(int astronauts, int target, int asteroids, int ufo_frequency) {
		super();
		
		this.astronauts = astronauts;
		this.target = target;
		this.asteroids = asteroids;
		this.ufo_frequency = ufo_frequency;

		this.setXCenter(0);
		this.setYCenter(0);
		
		background = new StarfieldBackground();
		backgrounds =new ArrayList<Background>();
		backgrounds.add(background);
		
		//add random Astronauts
		for (int i = 0; i < astronauts; i++) {
			//only add Astronauts within a certain distance of the player starting point
			double x = Math.random() * ASTRONAUT_RANGE - ASTRONAUT_HALF_RANGE;
			double y = Math.random() * ASTRONAUT_RANGE - ASTRONAUT_HALF_RANGE;
			//random velocity and rotation
			double velocityX = Math.random() * 100 - 50;
			double velocityY = Math.random() * 100 - 50;
			double rotation = Math.random() * 120 - 60;
			
			AstronautSprite astronaut = new AstronautSprite(x, y,velocityX, velocityY, rotation );
			sprites.add(astronaut);
		}
		
		//add random Asteroids
		for (int i = 0; i < asteroids; i++) {
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
	
	public int getTarget() {
		return this.target;
	}

	public void update(KeyboardInput keyboard, long actual_delta_time) {

		elapsedTime += actual_delta_time;
		updates++;

		//escape ends this universe
		if (keyboard.keyDownOnce(27)) {
			complete = true;
		}
		
		//spawn new UFO after a given # of updates
		if (updates % ufo_frequency == 0) {
			spawnUFOSprite();
		}
		
		updateSprites(keyboard, actual_delta_time);
		wrapStraySprites();
		disposeSprites();
	}

	 protected void updateSprites(KeyboardInput keyboard, long actual_delta_time) {

		 	if (player1.getAstronautsRescued() >= target) {
				//end of game
		 		successful = true;
				complete = true;
		 	}

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
		return player1.getCenterX();
	}


	public double getYCenter() {
		return player1.getCenterY();
	}
	
	public void setXCenter(double xCenter) {
	}

	public void setYCenter(double yCenter) {
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public boolean isSuccessful() {
		return successful;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	@Override
	public ArrayList<Background> getBackgrounds() {
		return backgrounds;
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
