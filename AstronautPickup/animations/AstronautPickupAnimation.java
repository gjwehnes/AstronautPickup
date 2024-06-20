
public class AstronautPickupAnimation implements Animation {

	private int universeCount = 0;
	private Universe current = null;
	private static int score = 0;
	private boolean universeSwitched = false;
	private boolean animationComplete;

	public AstronautPickupAnimation() {
		switchUniverse(null);
		universeSwitched = false;	}
	
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
	
	
	public Universe switchUniverse(Object event) {

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
			this.animationComplete = true;
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

	@Override
	public boolean getUniverseSwitched() {
		return universeSwitched;
	}

	@Override
	public void acknowledgeUniverseSwitched() {
		this.universeSwitched = false;		
	}

	@Override
	public boolean isComplete() {
		return animationComplete;
	}

	@Override
	public void setComplete(boolean complete) {
		this.animationComplete = true;		
	}

	@Override
	public void update(KeyboardInput keyboard, long actual_delta_time) {
		
		if (keyboard.keyDownOnce(KeyboardInput.KEY_F4)) {
			//cheat code... end the level
			((Level01Universe)(this.current)).setComplete(true);
			((Level01Universe)(this.current)).setSuccessful(true);
		}
		
	}	
	
}
