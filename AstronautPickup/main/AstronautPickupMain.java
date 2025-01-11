
public class AstronautPickupMain {
	
	public static void main(String[] args)
	{
		boolean stopApplication = false;
		
		while (stopApplication == false) {
			
			AstronautPickupAnimation animation = new AstronautPickupAnimation();
			AstronautPickupFrame frame = new AstronautPickupFrame((Animation)animation);
			frame.start();
			
			while (frame.getStopAnimation() == false) {
				try {
					Thread.sleep(100);
				}
				catch(Exception e) {    					
				} 
			}

			stopApplication = frame.getStopApplication(); 
			frame.setVisible(false);
			frame = null;
						
		}
							
	}

}
