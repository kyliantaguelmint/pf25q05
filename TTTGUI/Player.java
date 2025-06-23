package TTTGUI;

public class Player {
    private String username;
    private int score;

    public Player(String username) {
        this.username = username;
        this.score = 0;
    }

    public Player(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    @Override
    public String toString() {
        return username + "," + score;
    }
}
}
