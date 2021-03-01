
public class AstronautPickupAnimation implements Animation {

	private static int universeCount = 0;
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

	public static int getUniverseCount() {
		return universeCount;
	}

	public static void setUniverseCount(int count) {
		AstronautPickupAnimation.universeCount = count;
	}

	public Universe getNextUniverse() {

		universeCount++;
		
		if (universeCount == 1) {
			return new Level01Universe();
		}
		else {
			return null;
		}

	}
	
}
