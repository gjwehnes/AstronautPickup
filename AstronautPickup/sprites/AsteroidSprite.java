import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AsteroidSprite implements DisplayableSprite, MovableSprite {

	private static final int DEFAULT_WIDTH = 100	;
	private static final int DEFAULT_HEIGHT = 100;
	private final static int FRAMES = 360;
	
	private static Image[] rotatedImages = new Image[FRAMES];
	private static boolean framesLoaded = false;

	private double centerX = 0;
	private double centerY = 0;
	private double width = 50;
	private double height = 50;
	private boolean dispose = false;	

	private double currentAngle = 0;
	private double rotationSpeed = 10;	//degrees per second
	private double velocityX = 0;
	private double velocityY = 0;
	
	public AsteroidSprite(double centerX, double centerY, double velocityX, double velocityY, double rotationSpeed) {

		super();
		
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.rotationSpeed = rotationSpeed;
		

		Image defaultImage = null;

		if (framesLoaded == false) {
			try {
				defaultImage = ImageIO.read(new File("res/asteroid.png"));
				
				for (int i = 0; i < FRAMES; i++) {
					rotatedImages[i] = ImageRotator.rotate(defaultImage, i);
				}

			}
			catch (IOException e) {
			}
			framesLoaded = true;
		}		
	}
	
	//DISPLAYABLE
	public Image getImage() {
		return rotatedImages[(int)currentAngle];
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
		this.centerX += (actual_delta_time * 0.001 * velocityX);
		this.centerY += (actual_delta_time * 0.001 * velocityY);
				
		currentAngle -= (rotationSpeed * (actual_delta_time * 0.001));
	    if (currentAngle >= 360) {
	    	currentAngle -= 360;
	    }
	    if (currentAngle < 0) {
	    	currentAngle += 360;
	    }
	    
	    int frame = (int)currentAngle;
		
	}
	
	public void explode(Universe universe, double energy) {
		
		//separate into three smaller asteroids; preserve the surface area	
		//A1 = pi*r*r
		//A2 = 3 * (pi*r2*r2)
		//A1 = A2
		//
		//pi*r*r = 3 * pi * r2 * r2
		//r*r = 3*r2*r2
		//(1/3)*r*r = r2 * r2
		//r2 = sqrt((1/3)*r*r)
		//r2 = sqrt(1/3) * sqrt(r*r)
		//r2 = 1 / sqrt(3) * r
		//r2 = 0.577 * r	
		final double SIZE_REDUCTION = 0.577;
		//place center of smaller asteroids 1/2 of their radius from the center, so that they all touch center of original asteroid  
		final double DISTANCE_FROM_CENTER = SIZE_REDUCTION / 2;
		
		if (this.getWidth() > 50) {
			
			double centerX1 = this.getCenterX() + Math.cos(Math.toRadians(0)) * DISTANCE_FROM_CENTER * this.getWidth();
			double centerY1 = this.getCenterY() + Math.sin(Math.toRadians(0)) * DISTANCE_FROM_CENTER * this.getHeight();
			
			double centerX2 = this.getCenterX() +  Math.cos(Math.toRadians(120)) * DISTANCE_FROM_CENTER * this.getWidth();
			double centerY2 = this.getCenterY() + Math.sin(Math.toRadians(120)) * DISTANCE_FROM_CENTER * this.getHeight();

			double centerX3 = this.getCenterX() + Math.cos(Math.toRadians(240)) * DISTANCE_FROM_CENTER * this.getWidth();
			double centerY3 = this.getCenterY() + Math.sin(Math.toRadians(240)) * DISTANCE_FROM_CENTER * this.getHeight();

			AsteroidSprite fragment1 = new AsteroidSprite(centerX1, 
					centerY1 , 
					this.velocityX + (Math.random() * 2 - 1) * energy, 
					-this.velocityY + (Math.random() * 2 - 1) * energy, 
					this.rotationSpeed * 2 + 30);
			fragment1.setHeight(this.getHeight() * SIZE_REDUCTION);
			fragment1.setWidth(this.getWidth() * SIZE_REDUCTION);
			universe.getSprites().add(fragment1);

			AsteroidSprite fragment2 = new AsteroidSprite(centerX2,
					centerY2,
					this.velocityX + (Math.random() * 2 - 1) * energy, 
					this.velocityY + (Math.random() * 2 - 1) * energy, 
					this.rotationSpeed * 2 + 30);
			fragment2.setHeight(this.getHeight() * SIZE_REDUCTION);
			fragment2.setWidth(this.getWidth() * SIZE_REDUCTION);
			universe.getSprites().add(fragment2);

			AsteroidSprite fragment3 = new AsteroidSprite(centerX3, 
					centerY3, 
					this.velocityX + (Math.random() * 2 - 1) * energy, 
					this.velocityY + (Math.random() * 2 - 1) * energy, 
					this.rotationSpeed * 2 + 30);
			fragment3.setHeight(this.getHeight() * SIZE_REDUCTION);
			fragment3.setWidth(this.getWidth() * SIZE_REDUCTION);
			universe.getSprites().add(fragment3);
		}
				
		this.dispose = true;
		
	}

	public double getVelocityX() {
		return velocityX;
	}

	public double getVelocityY() {
		return velocityY;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public String toString() {
		return String.format("Asteroid: centerX: %6.2f; centerY: %6.2f; height: %6.2f; width: %6.2f", this.getCenterX(), this.getCenterY(), this.getHeight(), this.getWidth());
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}
	
	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	public void moveX(double pixelsPerSecond) {
		// not implemented
	}

	public void moveY(double pixelsPerSecond) {
		// not implemented
	}

	public void stop() {
		// not implemented		
	}

}
