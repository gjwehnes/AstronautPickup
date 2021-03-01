import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UFOSprite implements DisplayableSprite {

	private final double ACCELERATION = 200;          			//PIXELS PER SECOND PER SECOND 
	private final double MAXIMUM_VELOCITY = 300;				//PIXELS PER SECOND PER SECOND
	private final double MAXIMUM_VELOCITY_SQUARED = MAXIMUM_VELOCITY * MAXIMUM_VELOCITY;

	private static Image image;	
	private double centerX = 0;
	private double centerY = 0;
	private double width = 50;
	private double height = 50;
	private boolean dispose = false;	
	private double velocityX = 0;        						//PIXELS PER SECOND
	private double velocityY = 0;          						//PIXELS PER SECOND

	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;
		
	public UFOSprite(double centerX, double centerY, double velocityX, double velocityY) {

		super();
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = WIDTH;
		this.height = HEIGHT;
		
		this.velocityX = velocityX;
		this.velocityY = velocityY;

		if (image == null) {
			try {
				image = ImageIO.read(new File("res/ufo2.png"));
			}
			catch (IOException e) {
				System.err.println(e.toString());
			}		
		}		
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
		this.dispose = true;
	}

	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {

		double deltaX = universe.getPlayer1().getCenterX() - this.getCenterX();
		double deltaY = universe.getPlayer1().getCenterY() - this.getCenterY();
		double angleToTarget = Math.toDegrees(Math.atan((-deltaY) / deltaX)) + (deltaX < 0 ? 180 : 0);
						
		velocityX += Math.cos(Math.toRadians(angleToTarget)) * ACCELERATION * actual_delta_time * 0.001;
		velocityY -= Math.sin(Math.toRadians(angleToTarget)) * ACCELERATION * actual_delta_time * 0.001;

		double velocitySquared = (velocityX * velocityX + velocityY * velocityY);
		//maximum velocity is 300 pixels / second; if higher, then reduce by 1%;
		if (velocitySquared > MAXIMUM_VELOCITY_SQUARED ) {
			velocityX *= 0.99;
			velocityY *= 0.99;
		}
		
		//calculate new position
	    double movement_x = (this.velocityX * actual_delta_time * 0.001);
	    double movement_y = (this.velocityY * actual_delta_time * 0.001);
	    this.centerX += movement_x;
	    this.centerY += movement_y;
	}

}
