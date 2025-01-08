import java.io.Serializable;

/*
 * A simple class that stores information about a high score. Note the implementation of the Serializable
 * interface, which is required because we wish to serialize instances of this class (see HighScores class)
 */
public class NameAndScore implements Serializable {

	private String name;
	private int score;
		
	public NameAndScore(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public String getName() {
		return name;
	}
	public int getScore() {
		return score;
	}

	
}
