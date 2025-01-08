import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * As part of good development practice, a unit test is often included within a project to test (and
 * retest) low-level algorithms. A high score mechanism saved to file is a good example - simple in concept,
 * but with multiple edge cases (e.g. what if file does not exist, what if score does not rank, etc).
 * 
 * This test is not run as part of the regular execution of the program, but can (and should be) re-run
 * whenever a change is made to the high score mechanism, or if this program is being tested to run on a
 * different operating system (where the saving to file may not work, e.g. for lack of priviliges)
 */

class HighScoresTest {

	@Test
	void test() {
		
		deleteFile(HighScores.SCORES_FILE_PATH);

		HighScores.setHighScore(new NameAndScore("AAA", 100));
		assertEquals("AAA", HighScores.getHighScore(0).getName());
		assertEquals(100,HighScores.getHighScore(0).getScore());
		
		HighScores.setHighScore(new NameAndScore("BBB", 200));
		assertEquals("BBB", HighScores.getHighScore(0).getName());
		assertEquals(200,HighScores.getHighScore(0).getScore());
		assertEquals("AAA", HighScores.getHighScore(1).getName());
		assertEquals(100,HighScores.getHighScore(1).getScore());

		HighScores.setHighScore(new NameAndScore("CCC", 150));
		assertEquals("BBB", HighScores.getHighScore(0).getName());
		assertEquals(200,HighScores.getHighScore(0).getScore());
		assertEquals("CCC", HighScores.getHighScore(1).getName());
		assertEquals(150,HighScores.getHighScore(1).getScore());
		assertEquals("AAA", HighScores.getHighScore(2).getName());
		assertEquals(100,HighScores.getHighScore(2).getScore());
		
		for (int i = 0; i < 10; i++) {
			HighScores.setHighScore(new NameAndScore("DDD", (i+1) * 10));
		}
		assertEquals("BBB", HighScores.getHighScore(0).getName());
		assertEquals(200,HighScores.getHighScore(0).getScore());
		assertEquals("CCC", HighScores.getHighScore(1).getName());
		assertEquals(150,HighScores.getHighScore(1).getScore());
		assertEquals("AAA", HighScores.getHighScore(2).getName());
		assertEquals(100,HighScores.getHighScore(2).getScore());
		
		assertEquals("DDD", HighScores.getHighScore(3).getName());
		assertEquals(100,HighScores.getHighScore(3).getScore());
		assertEquals("DDD", HighScores.getHighScore(4).getName());
		assertEquals(90,HighScores.getHighScore(4).getScore());
		assertEquals("DDD", HighScores.getHighScore(5).getName());
		assertEquals(80,HighScores.getHighScore(5).getScore());
		assertEquals("DDD", HighScores.getHighScore(6).getName());
		assertEquals(70,HighScores.getHighScore(6).getScore());
		assertEquals("DDD", HighScores.getHighScore(7).getName());
		assertEquals(60,HighScores.getHighScore(7).getScore());
		assertEquals("DDD", HighScores.getHighScore(8).getName());
		assertEquals(50,HighScores.getHighScore(8).getScore());
		assertEquals("DDD", HighScores.getHighScore(9).getName());
		assertEquals(40,HighScores.getHighScore(9).getScore());
		
		assertEquals(null,HighScores.getHighScore(10));

		
	}

	private static void deleteFile(String file) {

		Path path = Paths.get(file);
		//clear destination files
		try {
		    Files.delete(path);
		} catch (NoSuchFileException x) {
//		    System.err.format("deleteFile: %s: no such" + " file or directory%n", path);
		} catch (DirectoryNotEmptyException x) {
//		    System.err.format("deleteFile: %s not empty%n", path);
		} catch (IOException x) {
		    // File permission problems are caught here.
//		    System.err.println(x);
		}		
	}
	
	
}
