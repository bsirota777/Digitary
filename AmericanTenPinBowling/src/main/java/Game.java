import java.util.ArrayList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class represents American 10 pin bowling game
 */
public class Game {

    private static final int MAX_FRAMES = 10;

    private static final String STRIKE_REGEX = "[X]{1}";

    private static final String SPARE_REGEX = "[1-9]{1}[/]{1}";

    private static final String LAST_SPARE_REGEX = "[1-9]{1}[/]{1}[1-9]{1}[0]?";

    private static final String REGULAR_REGEX = "([1-9]{1}[/-]{1})|([-]{1}[1-9]{1})|([1-9]{2})";

    private static final String MISS = "-";

    private static final int STRIKE_SCORE = 10;

    private static final int SPARE_SCORE = 10;

    private static final int MAX_SCORE = 10;

    private static final Pattern regularPattern = Pattern.compile(REGULAR_REGEX);

    private static final Pattern strikePattern = Pattern.compile(STRIKE_REGEX);

    private static final Pattern sparePattern = Pattern.compile(SPARE_REGEX);

    private static final Pattern lastSparePattern = Pattern.compile(LAST_SPARE_REGEX);

    // The number of turns is represented by the number of frames
    private int numFrames = 10; // default

    private int totalScore = 0;

    private List<Frame> frames = null;

    private Game() {} // Always force a Game object created  with a number of frames

    // In case we want to have more than 10 frames
    public Game(int numFrames) {
        this.numFrames = numFrames;
        frames = new ArrayList<Frame>(numFrames);
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void start(String[] args) {
        System.out.println("\n...start()");

        try {
            parseGameResult(args);
            calculateTotalScore();
        } catch (NumberFormatException nfe) {
            System.err.println("Failed to process results: " + nfe.getMessage());
        }
    }

    protected void parseGameResult(String[] tokens) throws NumberFormatException {
        System.out.println("...parseGameResult()");

        // 1. Parse all tokens and get them converted to frames.
        //    That will include any bonus processing at the end

        for (int i=0; i<tokens.length; i++) {
            String token = tokens[i];
            Frame frame = parseFrame(tokens[i]);
            frames.add(frame);

            // Last frame, i.e. 10th
            if (i == MAX_FRAMES-1) {
                if (frame.isStrike()) { // Check for 2 Bonus throws
                    frame.addScore(MAX_SCORE);

                    // Expect 2 more throws
                    String nextToken = tokens[i+1];
                    if (isResultStrike(nextToken)) {
                        // Keep going
                        String nextNextToken = tokens[i+2];
                        if (isResultStrike(nextNextToken)) {
                            frame.addScore(MAX_SCORE);
                        } else if (isResultSpare(nextNextToken)) {
                            frame.addScore(Integer.valueOf(nextNextToken.substring(0,1)));
                        } else {
                            frame.addScore(calculateAttemptsScore(nextNextToken));
                        }
                    } else if (isResultSpare(nextToken)) {
                        // 2 throws result in maximum points. i.e. 10
                        frame.addScore(MAX_SCORE);
                    } else {
                        frame.addScore(calculateAttemptsScore(nextToken));
                    }
                    break;
                } else if (isResultLastSpare(token)) {
                    // Check for 1 Bonus throw
                    if (token.length() == 3) {
                        frame.addScore(Integer.valueOf(token.substring(2, 3)));
                    } else if (token.length() == 4) {
                        frame.addScore(Integer.valueOf(token.substring(2, 4)));
                    } else {
                        // Not permitted. Should be checked during input validation.
                    }
                    break;
                } else {
                    // Processed already
                }
            } else {
                // No Strike or Spare, so no Bonus!
            }
        }

        // 2. Check all frames but last
        updateFramesScores();
    }

    private int calculateAttemptsScore(String token) throws NumberFormatException {
        int score1 = Integer.valueOf(token.substring(0,1));
        int score2 = Integer.valueOf(token.substring(1,2));
        return score1 + score2;
    }

    private void updateFramesScores() {

        for (int i=0; i<frames.size()-1; i++) {
            Frame frame = frames.get(i);
            if (frame.isStrike()) {
                // check next frame, looking for 2 rolls
                if (i < frames.size() - 1) {
                    Frame nextFrame = frames.get(i + 1);
                    if (nextFrame.isStrike()) {
                        frame.addScore(MAX_SCORE);

                        if (i < frames.size() - 2) {
                            Frame nextNextFrame = frames.get(i + 2);
                            if (nextNextFrame.isStrike()) {
                                frame.addScore(MAX_SCORE);
                            } else if (nextNextFrame.isSpare()) {
                                // only the 1st roll
                                // Add 1st attempt
                                frame.addScore(frame.getAttempts().get(0).getScore());
                            } else {
                                frame.addScore(nextNextFrame.getScore());
                            }
                        } else {
                            // Probably the 9th frame
                            // Add 1st attempt
                            frame.addScore(frame.getAttempts().get(0).getScore());
                        }
                    } else if (nextFrame.isSpare()) {
                        // 2 rolls have been done, add max score
                        frame.addScore(MAX_SCORE);
                    } else {
                        // 2 rolls have been done, add whatever sum score
                        frame.addScore(nextFrame.getScore());
                    }
                } else {
                    // Last frame, ignore as it is already calculated
                }
            } else if (frame.isSpare()) {
                if (i < frames.size() - 1) {
                    Frame nextFrame = frames.get(i + 1);
                    if (nextFrame.isStrike()) {
                        frame.addScore(MAX_SCORE);
                    } else if (nextFrame.isSpare()) {
                        // Add 1st attempt
                        frame.addScore(frame.getAttempts().get(0).getScore());
                    } else {
                        // Add 1st attempt
                        frame.addScore(frame.getAttempts().get(0).getScore());
                    }
                } else {
                    // Last frame, ignore as it is already calculated
                }
            } else {
                // Regular frame, value already assigned
            }
        }
    }

    public static Frame parseFrame(String token) throws NumberFormatException {
        Frame frame = new Frame();

        if (isResultStrike(token)) {
            frame.setStrike(true);
            frame.setScore(STRIKE_SCORE);

            Attempt attempt = new Attempt();
            attempt.setScore(MAX_SCORE);
            frame.getAttempts().add(attempt);
        }
        else
        if (isResultSpare(token)) {
            frame.setSpare(true);
            frame.setScore(SPARE_SCORE);

            Attempt attempt = new Attempt();
            String attemptStr1 = token.substring(0,1);
            attempt.setScore(Integer.valueOf(attemptStr1));
            frame.getAttempts().add(attempt);
        }
        else
        if (isResultLastSpare(token)) {
            frame.setSpare(true);
            frame.setScore(SPARE_SCORE);

            Attempt attempt = new Attempt();
            String attemptStr1 = token.substring(0,1);
            attempt.setScore(Integer.valueOf(attemptStr1));
            frame.getAttempts().add(attempt);
        }
        else {
            // Assume 2 attempts score
            if (token.isBlank()) {
                throw new NumberFormatException("Empty result 1");
            }

            String attemptStr1 = token.substring(0,1);
            Attempt attempt = new Attempt();
            frame.getAttempts().add(attempt);
            if (attemptStr1.equals(MISS)) {
                frame.addScore(0);
            } else {
                attempt.setScore(Integer.valueOf(attemptStr1));
                frame.addScore(attempt.getScore());
            }

            if (token.length() == 1) {
                throw new NumberFormatException("Empty result 2");
            }

            String attemptStr2 = token.substring(1,2);
            attempt = new Attempt();
            frame.getAttempts().add(attempt);
            if (attemptStr2.equals(MISS)) {
                frame.addScore(0);
            } else {
                attempt.setScore(Integer.valueOf(attemptStr2));
                frame.addScore(attempt.getScore());
            }
        }

        return frame;
    }

    protected void calculateTotalScore() {
        System.out.println("...calculateTotalScore()");
        for (int i=0; i<frames.size(); i++) {
            totalScore += frames.get(i).getScore();
        }
        System.out.println("\nTotal score is: "+ totalScore + "\n");
    }

    public static boolean isInputValid(String[] tokens) throws NumberFormatException {

        boolean valid = false;

        for (int i=0; i<tokens.length; i++) {
            if (!isResultStrike(tokens[i])) {
                if (!isResultSpare(tokens[i])) {
                    if (!isResultRegular(tokens[i])) {
                        // Check if the last spare which is in a different format
                        if (i == MAX_FRAMES) {
                            if (!isResultLastSpare(tokens[i])) {
                                valid = true;
                            }
                        }
                        return false;
                    } else {
                        Frame frame = parseFrame(tokens[i]);
                        int score1 = frame.getAttempts().get(0).getScore();
                        int score2 = frame.getAttempts().get(1).getScore();
                        int scoreSum = score1 + score2;
                        if ( scoreSum <= MAX_SCORE) {
                            valid = true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    valid = true;
                }
            } else {
                valid = true;
            }
        }

        return valid;
    }

    public static boolean isResultStrike(String result) {
        if (result != null && !result.isBlank()) {
            Matcher strikeMatcher = strikePattern.matcher(result);
            if (strikeMatcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isResultSpare(String result) {
        if (result != null && !result.isBlank()) {
            Matcher spareMatcher = sparePattern.matcher(result);
            if (spareMatcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isResultLastSpare(String result) {
        if (result != null && !result.isBlank()) {
            Matcher lastSpareMatcher = lastSparePattern.matcher(result);
            if (lastSpareMatcher.matches()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isResultRegular(String result) {
        if (result != null && !result.isBlank()) {
            Matcher regularMatcher = regularPattern.matcher(result);
            if (regularMatcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("Run with arguments: " + args.length);

        if (args.length == 10 || args.length == 12) {
            if (Game.isInputValid(args)) {
                Game game = new Game();
                game.start(args);
            }
        } else {
            System.err.println("Invalid number of parameters: must be 8 or 10");
        }
    }
}
