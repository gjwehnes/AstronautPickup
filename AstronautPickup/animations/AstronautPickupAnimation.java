
public class AstronautPickupAnimation implements Animation {

	private int universeCount = 0;
	private Universe current = null;
	private static int score = 0;
	
	public static int getScore() {
		return score;
	}

	public static void setScore(int score) {
		AstronautPickupAnimation.score = score;
	}

	public static void addScore(int score) {
		AstronautPickupAnimation.score += score;
	}

	public int getLevel() {
		return universeCount;
	}
	
	public Universe getNextUniverse() {

		universeCount++;
		
		if (universeCount == 1) {
			this.current = new Level01Universe(5,2,10,5000);
		}
		else if (universeCount == 2) {
			this.current = new Level01Universe(10,5,25,200);
		}
		else if (universeCount == 3) {
			this.current = new Level01Universe(20,10,50,500);
		}
		else {
			this.current = null;
		}
		
		return this.current;

	}

	public Universe getCurrentUniverse() {
		return this.current;
	}
		
	public void restart() {
		universeCount = 0;
		current = null;
		score = 0;		
	}
	
}
