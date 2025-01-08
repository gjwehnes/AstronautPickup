
public class AstronautPickupMain {
	
	public static void main(String[] args)
	{
		boolean stop = false;
		
		while (stop == false) {
			
			AstronautPickupAnimation animation = new AstronautPickupAnimation();
			AstronautPickupFrame frame = new AstronautPickupFrame((Animation)animation);
			frame.start();
			
			while (frame.stop == false) {
				try {
					Thread.sleep(100);
				}
				catch(Exception e) {    					
				} 
			}

			stop = frame.getwindowClosed(); 
			frame.setVisible(false);
			frame = null;
						
		}
							
	}

}
