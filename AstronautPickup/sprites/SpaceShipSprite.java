import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpaceShipSprite implements DisplayableSprite {

	private final double ACCELERATION = 400;          			//PIXELS PER SECOND PER SECOND 
	private final double ROTATION_SPEED = 180;					//degrees per second
	private final double BULLET_VELOCITY = 751;
	private final int RELOAD_TIME = 100;

	private static Image image;	
	private double centerX = 0;
	private double centerY = 0;
	private double width = 50;
	private double height = 50;
	private boolean dispose = false;	
	private double velocityX = 000;        	//PIXELS PER SECOND
	private double velocityY = 0;          	//PIXELS PER SECOND

	private static Image[] rotatedImages = new Image[360];
	private AudioPlayer thrustSound = new AudioPlayer();
	private AudioPlayer bulletSound = new AudioPlayer();
	
	private double reloadTime = RELOAD_TIME;	
	private double currentAngle = 0;
	private double fuel = 100;
	private int maxAmmo = 300;
	private int ammo = maxAmmo;
	private double health = 100;
	private int astronautsRescued = 0;
	
	public SpaceShipSprite(double centerX, double centerY) {

		super();
		this.centerX = centerX;
		this.centerY = centerY;

		Image image = null;
		try {
			image = ImageIO.read(new File("res/spaceship.png"));
		}
		catch (IOException e) {
			System.out.print(e.toString());
		}

		//create rotated images
		if (image != null) {
			for (int i = 0; i < 360; i++) {
				rotatedImages[i] = ImageRotator.rotate(image, i);			
			}
			this.height = image.getHeight(null);
			this.width = image.getWidth(null);
		}		
				
	}
	
	//DISPLAYABLE
	
	public Image getImage() {
		return rotatedImages[(int)currentAngle];
	}
	
	public Image getImage(int angle) {
		return rotatedImages[angle];
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

	public double getFuel() {
		return fuel;
	}

	public void setFuel(double fuel) {
		this.fuel = fuel;
	}
	
	//exposed to GUI
	public int getMaxAmmo() {
		return maxAmmo;
	}

	//exposed to GUI
	public void setMaxAmmo(int maxAmmo) {
		this.maxAmmo = maxAmmo;
	}

	//exposed to GUI
	public int getAmmo() {
		return ammo;
	}

	//exposed to GUI
	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}
	
	//exposed to GUI
	public double getHealth() {
		return health;
	}

	//exposed to GUI
	public void setHealth(double health) {
		this.health = health;
	}
	
	public int getAstronautsRescued() {
		return this.astronautsRescued;
	}
	
	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
		//LEFT	
		if (keyboard.keyDown(37) && this.fuel > 0 ) {
			currentAngle -= (ROTATION_SPEED * (actual_delta_time * 0.001));
			this.fuel -= 1 * actual_delta_time * 0.001;
		}
		// RIGHT
		if (keyboard.keyDown(39) && this.fuel > 0) {
			currentAngle += (ROTATION_SPEED * (actual_delta_time * 0.001));
		}
		//UP
		if (keyboard.keyDown(38) && this.fuel > 0) {
			if (thrustSound.isPlayCompleted()) {
				thrustSound.playAsynchronous("res/thrust.wav");
			}
			//add accelaration to velocity based on angle
			double angleInRadians = Math.toRadians(currentAngle);
			velocityX += Math.cos(angleInRadians) * ACCELERATION * actual_delta_time * 0.001;
			velocityY += Math.sin(angleInRadians) * ACCELERATION * actual_delta_time * 0.001;
			this.fuel -= 1 * actual_delta_time * 0.001;
		}
		else {
			if (thrustSound.isPlayCompleted() == false) {
				thrustSound.setStop(true);
			}
		}
		//SPACE
		if (keyboard.keyDown(32) && this.ammo > 0) {
			shoot(universe);	
		}

		//ensure that we don't have a negative angle
	    if (currentAngle < 0) {
	    	currentAngle += 360;
	    }	
	    
 	    //ensure that we don't have an angle >= 360
	    currentAngle %= 360;
		
	    this.height = rotatedImages[(int)currentAngle].getHeight(null);
	    this.width = rotatedImages[(int)currentAngle].getWidth(null);
	    
		//calculate new position assuming there are no changes in direction
	    double movement_x = (this.velocityX * actual_delta_time * 0.001);
	    double movement_y = (this.velocityY * actual_delta_time * 0.001);
	    this.centerX = this.getCenterX() + movement_x;
	    this.centerY = this.getCenterY()  + movement_y;
	    
	    checkAstronautPickup(universe);	    
			
		reloadTime -= actual_delta_time;
	}
	
	private void checkAstronautPickup(Universe universe) {
		
		for (DisplayableSprite sprite : universe.getSprites()) {
			
			if (sprite instanceof AstronautSprite || sprite instanceof AsteroidSprite || sprite instanceof UFOSprite) {
								
				if (CollisionDetection.pixelBasedOverlaps(this, sprite))
				{
					if (sprite instanceof AstronautSprite) {
						((AstronautSprite)sprite).setDispose(true);
						//astronaut pickup
						AstronautPickupAnimation.addScore(100);
						astronautsRescued++;						
					}
					if (sprite instanceof AsteroidSprite) {
						((AsteroidSprite)sprite).setDispose(true);
						//collision with asteroid
						//damage is proportional to size of asteroid
						this.health -= sprite.getWidth() * 0.2;
					}
					if (sprite instanceof UFOSprite) {
						//collision with UFO
						((UFOSprite)sprite).setDispose(true);
						this.health -= 10;
					}
				}
			}
		}
		
	}
	
	public void shoot(Universe universe) {
		
		if (reloadTime <= 0) {

			double angleInRadians = Math.toRadians(currentAngle);
			double bulletVelocityX = Math.cos(angleInRadians) * BULLET_VELOCITY + velocityX;
			double bulletVelocityY = Math.sin(angleInRadians) * BULLET_VELOCITY + velocityY;
			
			double bulletCurrentX = this.getCenterX();
			double bulletCurrentY = this.getCenterY();

			BulletSprite bullet = new BulletSprite(bulletCurrentX, bulletCurrentY, bulletVelocityX, bulletVelocityY);
			universe.getSprites().add(bullet);
			if (bulletSound.isPlayCompleted()) {
				bulletSound.playAsynchronous("res/missile.wav");
			}
			
			this.ammo--;
			
			reloadTime = RELOAD_TIME;
			
		}
	}
	

}
