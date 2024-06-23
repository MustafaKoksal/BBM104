public class Player {
    private String name;
    private int score;

    // Add each player's name and start their score from zero.
    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    // Update player's score.
    public void setScore(int score) {
        this.score = score;
    }

}