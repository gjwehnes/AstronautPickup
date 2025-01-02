import java.io.Serializable;

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
