
public class Main {
	
	public static void main(String[] args)
	{
		AstronautPickupAnimation animation = new AstronautPickupAnimation();
		AstronautPickupFrame frame = new AstronautPickupFrame((Animation)animation);
		frame.setVisible(true);
		frame.start();
	}

}
