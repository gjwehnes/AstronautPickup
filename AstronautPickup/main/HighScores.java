import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/*
 * An example of how you can save ('persist') data between runs of an application using the file
 * system. Serialization is used to simplify the process of saving and loading
 */

public class HighScores {

	// score is zero-indexed; an index of -1 indicates that a score does not rank
	
	protected final static String SCORES_FILE_PATH = "res/high-scores/scores.bin";
	public final static int LIST_SIZE = 10;
	
	private static NameAndScore[] scores = null;

	private static void save() {		
		try {
			Serializer.serialize(scores, SCORES_FILE_PATH);
		} catch (Exception e) {
			System.out.println(e.getMessage());						
		}
	
	}
	
	private static void load() {
		try {
			scores = (NameAndScore[]) Serializer.deserialize(SCORES_FILE_PATH);
		} catch (Exception e) {
			System.out.println(e.getMessage());						
		}		

		/*
		 * if somehow the deserialization does not work properly (or if the file does not exist),
		 * create a blank list of high scores
		 */
		if (scores == null || scores.length != LIST_SIZE) {
			scores = new NameAndScore[LIST_SIZE];
		}
		
	}
	
	public static NameAndScore getHighScore(int index) {
		if (scores == null) {
			load();
		}
		if (index >= 0 && index <= LIST_SIZE - 1) {
			return scores[index];
		}
		else {
			return null;
		}
	}
		
	public static void setHighScore(NameAndScore score) {

		int rank = getScoreRank(score.getScore());
		int index = LIST_SIZE - 1;
		
		if (rank >= 0 && rank <= LIST_SIZE) {
			for (index = LIST_SIZE - 1; index > rank; index--) {
				scores[index] = scores[index-1];
			}
			scores[rank] = score;
			save();
		}		
	}
	
	public static int getScoreRank(int score) {
		int rank = -1;
		int index =  LIST_SIZE - 1;
		while (index >= 0 && (getHighScore(index) == null || getHighScore(index).getScore() < score) ) {
			rank = index;
			index -= 1;
		}
		return rank;		
	}

}
