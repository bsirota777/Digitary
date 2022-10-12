import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testValidateInputSuccessStrike() {
        String[] tokens = { "X" };
        assertTrue(Game.isInputValid(tokens));
    }

    @Test
    public void testValidateInputSuccessSpare() {

        String[] tokens = new String[9];
        for(int i = 0; i < 9; ) {
            String value = ++i + "/";
            tokens[i-1] = value;
        }
        assertTrue(Game.isInputValid(tokens));
    }

    @Test
    public void testValidateInputSuccessMiss() {

        String[] tokens = new String[9];
        for(int i = 0; i < 9; ) {
            String value = ++i + "-";
            tokens[i-1] = value;
        }
        assertTrue(Game.isInputValid(tokens));
    }

    @Test
    public void testValidateInputSuccessMissReverseOrder() {

        String[] tokens = new String[9];
        for(int i = 0; i < 9; ) {
            String value = "-" + ++i;
            tokens[i-1] = value;
        }
        assertTrue(Game.isInputValid(tokens));
    }

    @Test
    public void testValidateInputSuccess() {

        String[] tokens = new String[9];
        for(int i = 0; i < 9; ) {
            String value = "1" + ++i;
            tokens[i-1] = value;
        }
        assertTrue(Game.isInputValid(tokens));
    }

    @Test
    public void testValidateInputInvalidAlpha() {
        String[] tokens = new String[52]; // All alpha chars but 'X'

        int countChars = 0;
        for(int i = 65; i <= 90; i++, countChars++)
        {
            if (i != 88 ) {
                tokens[countChars] = String.valueOf((char)i) + "/";
            }
        }

        for(int i = 97; i <= 122; i++, countChars++)
        {
            tokens[countChars] = String.valueOf((char)i);
        }

        assertFalse(Game.isInputValid(tokens));
    }

    @Test
    public void testValidateInputEmpty() {
        String[] tokens = { };
        assertFalse(Game.isInputValid(tokens));
    }

    @Test
    public void testValidateInputNoChars() {
        String[] tokens = { "" };
        assertFalse(Game.isInputValid(tokens));
    }

    @Test
    public void testValidateInputInvalidChars() {
        String[] tokens = { "a", "!", "@", "#", "$", "%", "^" };
        assertFalse(Game.isInputValid(tokens));
    }

    @Test
    public void testValidateInputInvalidOptionsZero() {
        String[] tokens = { "0-", "0/" };
        assertFalse(Game.isInputValid(tokens));
    }

    @Test
    public void testValidateInputInvalidOptionsTen() {
        String[] tokens = { "10-", "10/" };
        assertFalse(Game.isInputValid(tokens));
    }

    @Test
    public void testStrike() {
        assertTrue(Game.isResultStrike("X"));
    }

    @Test
    public void testSpare() {
        assertTrue(Game.isResultSpare("1/"));
    }

    @Test
    public void testFrameStrike() {
        Game game = new Game(1);
        Frame frame = game.parseFrame("X");
        assertTrue(frame.isStrike());
    }

    @Test
    public void testFrameSpare() {
        Game game = new Game(1);
        Frame frame = game.parseFrame("1/");
        assertTrue(frame.isSpare());
    }

    @Test
    public void testRegularScore() {
        Game game = new Game(1);
        Frame frame = game.parseFrame("12");

        assertFalse(frame.isStrike());
        assertFalse(frame.isSpare());

        List<Attempt> attempts = frame.getAttempts();

        assertEquals(2,attempts.size());
        assertEquals(1, attempts.get(0).getScore());
        assertEquals(2, attempts.get(1).getScore());
        assertEquals(3, frame.getScore());
    }

    @Test
    public void calculateTotalScore() {
        String[] tokens = new String[1];
        Game game = new Game(tokens.length);
        tokens[0] = "54";
        game.parseGameResult(tokens);
        game.calculateTotalScore();

        assertEquals(9,game.getTotalScore());

        game = new Game(tokens.length);
        tokens[0] = "-4";
        game.parseGameResult(tokens);
        game.calculateTotalScore();

        assertEquals(4,game.getTotalScore());

        game = new Game(tokens.length);
        tokens[0] = "8-";
        game.parseGameResult(tokens);
        game.calculateTotalScore();

        assertEquals(8,game.getTotalScore());

        game = new Game(tokens.length);
        tokens[0] = "--";
        game.parseGameResult(tokens);
        game.calculateTotalScore();

        assertEquals(0,game.getTotalScore());
    }

    @Test
    public void calculateTotalScoreFailure() {

        NumberFormatException thrown = Assertions.assertThrows(NumberFormatException.class, () -> {
            String[] tokens = new String[1];
            Game game = new Game(tokens.length);
            tokens[0] = "";
            game.parseGameResult(tokens);
            game.calculateTotalScore();
        });

        Assertions.assertEquals("Empty result 1", thrown.getMessage());

        thrown = Assertions.assertThrows(NumberFormatException.class, () -> {
            String[] tokens = new String[1];
            Game game = new Game(tokens.length);
            tokens[0] = "1";
            game.parseGameResult(tokens);
            game.calculateTotalScore();
        });

        Assertions.assertEquals("Empty result 2", thrown.getMessage());
    }

    @Test
    public void allStrikes() {
        String[] tokens = { "X","X","X","X","X","X","X","X","X","X","X","X" };
        Game game = new Game(tokens.length);
        game.start(tokens);
        assertEquals(300,game.getTotalScore());
    }

    @Test
    public void all5Spare() {
        String[] tokens = { "5/","5/","5/","5/","5/","5/","5/","5/","5/","5/5" };
        Game game = new Game(tokens.length);
        game.start(tokens);
        assertEquals(150,game.getTotalScore());
    }

    @Test
    public void allStrikes9Miss() {
        String[] tokens = { "9-","9-","9-","9-","9-","9-","9-","9-","9-","9-" };
        Game game = new Game(tokens.length);
        game.start(tokens);
        assertEquals(90,game.getTotalScore());
    }

    @Test
    public void allStrikes10thSpare11thSpare() {
        String[] tokens = { "X","X","X","X","X","X","X","X","X","4/5" };
        Game game = new Game(tokens.length);
        game.start(tokens);
        assertEquals(275,game.getTotalScore());
    }
}