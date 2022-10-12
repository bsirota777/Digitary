import java.util.ArrayList;
import java.util.List;

public class Frame {

    private final int totalNumOfTries = 2;

    private List<Attempt> attempts = new ArrayList<>(totalNumOfTries);

    private boolean isSpare = false; // if all pins knocked down in two strikes
    private boolean isStrike = false; // if all pins knocked down in one strike

    private int score = 0;

    public boolean isSpare() {
        return isSpare;
    }

    public void setSpare(boolean spare) {
        isSpare = spare;
    }

    public boolean isStrike() {
        return isStrike;
    }

    public void setStrike(boolean strike) {
        isStrike = strike;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public List<Attempt> getAttempts() {
        return attempts;
    }
}
