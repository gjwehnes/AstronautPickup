import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class BulletSprite implements DisplayableSprite {

	private static final int WIDTH = 20;
	private static final int HEIGHT = 20;
	
	private static Image image;	
	private double centerX = 0;
	private double centerY = 0;
	private double width = 50;
	private double height = 50;
	private boolean dispose = false;	
	private double velocityX = 0;
	private double velocityY = 0;

	private AudioPlayer bulletSound = null;	
	private long lifeTime = 1000;
	
	public BulletSprite(double centerX, double centerY, double velocityX, double velocityY) {

		super();
		this.centerX = centerX;
		this.centerY = centerY;
		this.width =  WIDTH;
		this.height = HEIGHT;

		this.velocityX = velocityX;
		this.velocityY = velocityY;
		
		if (image == null) {
			try {
				image = ImageIO.read(new File("res/bullet.png"));
			}
			catch (IOException e) {
				System.err.println(e.toString());
			}
		}
		
		if (bulletSound == null) {
			bulletSound = new AudioPlayer();
		}
		
		bulletSound.playAsynchronous("res/missile.wav");
		
	}

	//DISPLAYABLE

	public Image getImage() {
		return image;
	}
		
	public boolean getVisible() {
		return true;
	}
	
	public double getMinX() {
		return centerX - (width / 2);
	}

	public double getMaxX() {
		return centerX + (width / 2);
	}

	public double getMinY() {
		return centerY - (height / 2);
	}

	public double getMaxY() {
		return centerY + (height / 2);
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public double getCenterX() {
		return centerX;
	};

	public double getCenterY() {
		return centerY;
	};
	
	public boolean getDispose() {
		return dispose;
	}

	public void setDispose(boolean dispose) {
		this.dispose = dispose;
	}	

	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
		
	    double movement_x = (this.velocityX * actual_delta_time * 0.001);
	    double movement_y = (this.velocityY * actual_delta_time * 0.001);
	    
	    this.centerX += movement_x;
	    this.centerY += movement_y;

	    lifeTime -= actual_delta_time;
	    if (lifeTime < 0) {
	    	this.dispose = true;
	    }	    			
	
	    checkCollision(universe);
	}
	
	private void checkCollision(Universe universe) {
				
		for (int i = 0; i < universe.getSprites().size(); i++) {
			
			DisplayableSprite sprite = universe.getSprites().get(i);
			
			if (sprite instanceof AstronautSprite || sprite instanceof AsteroidSprite || sprite instanceof UFOSprite) {
				
				if (CollisionDetection.pixelBasedOverlaps(this, sprite))
				{
					if (sprite instanceof AstronautSprite) {
						//dispose of bullet
						this.dispose = true;
						//astronaut hit
						AstronautPickupAnimation.addScore(-100);
						((AstronautSprite)sprite).setDispose(true);
					}
					
					if (sprite instanceof AsteroidSprite) {
						//dispose of bullet
						this.dispose = true;
						//asteroid hit
						AstronautPickupAnimation.addScore(5);
						double relativeVelocityX = (this.velocityX - ((AsteroidSprite)sprite).getVelocityX());
						double relativeVelocityY = (this.velocityY - ((AsteroidSprite)sprite).getVelocityY());
						double energy = Math.sqrt(relativeVelocityX * relativeVelocityX + relativeVelocityY * relativeVelocityY) * 0.1;
						((AsteroidSprite) sprite).explode(universe, energy);
					}
					
					if (sprite instanceof UFOSprite) {
						//dispose of bullet
						this.dispose = true;
						//ufo hit
						AstronautPickupAnimation.addScore(250);
						((UFOSprite)sprite).setDispose(true);
					}
					
					break;
				
				}
				
			}
		}
		
	}
}
