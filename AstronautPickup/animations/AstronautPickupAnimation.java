
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

	public Universe getNextUniverse() {

		universeCount++;
		
		if (universeCount == 1) {
			this.current = new Level01Universe();
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
