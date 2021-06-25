import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AstronautSprite implements DisplayableSprite, MovableSprite {

	private final static int FRAMES = 360;
	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;
	private static float scale = 1;	

	private static Image[] rotatedImages = new Image[FRAMES];
	private static boolean framesLoaded = false;	

	private double centerX = 0;
	private double centerY = 0;
	private double width = 50;
	private double height = 50;
	private boolean dispose = false;	
	private double velocityX = 0;
	private double velocityY = 0;


	private double currentAngle = 0;
	private double rotationSpeed = 10;	//degrees per second
	
	public AstronautSprite(double centerX, double centerY, double velocityX, double velocityY, double rotationSpeed) {

		super();
		
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = WIDTH;
		this.height = HEIGHT;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.rotationSpeed = rotationSpeed;
		
		Image image = null;		

		if (framesLoaded == false) {
			try {
				image = ImageIO.read(new File("res/astronaut.png"));
				scale = WIDTH / (float)image.getWidth(null);
				
				//create rotated images.... not that these are placed in a static array; no need for each instance to have copies
				//there will be many instances of this sprite, having each store copies would require a great amount of memory; 
				for (int i = 0; i < FRAMES; i++) {
					rotatedImages[i] = ImageRotator.rotate(image, i);
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
	    if (rotatedImages[frame] != null) {
		    this.height = ((int) (rotatedImages[frame].getHeight(null) * scale));
		    this.width = ((int) (rotatedImages[frame].getWidth(null) * scale));
	    }
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
