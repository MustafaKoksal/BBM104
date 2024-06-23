public class DiceGame {
    private Player[] players;
    private int numberOfPlayers;
    private String[] lines;
    private int lineNumber;
    private String outputFilePath;
    private int playerIndex;

    // Assign values to all variables.
    public DiceGame(String[] playerNames, String[] lines, String outputFilePath) {
        this.numberOfPlayers = playerNames.length;
        this.players = new Player[numberOfPlayers];
        // Create an object for each player.
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new Player(playerNames[i]);
        }
        this.lineNumber = 2;
        this.outputFilePath = outputFilePath;
        this.lines = lines;
    }

    // Determine the turn status according to values on the dices.
    public void play() {
        while (numberOfPlayers > 0 && lineNumber < lines.length) {
            String[] diceResult = lines[lineNumber].split("-");
            int firstDice = Integer.parseInt(diceResult[0]);
            int secondDice = Integer.parseInt(diceResult[1]);

            // If player threw 1-1, remove the player from game.
            if (firstDice == 1 && secondDice == 1) {
                gameOver(playerIndex);
            } // If player skips (0-0) turn.
            else if (firstDice == 0 && secondDice == 0) {
                skipTurn(playerIndex);
            } // If one of the dices is 1 doesn't change score.
            else if ((firstDice == 1 && secondDice != 1) || (firstDice != 1 && secondDice == 1)){
                doNothing(playerIndex);
            } // If the previous conditions are not met, the values on the dices are added to the score.
            else {
                playTurn(playerIndex, firstDice, secondDice);
            }

            lineNumber++;
        }
        // The last player becomes the winner.
        declareWinner();
    }

    // The player's score remains unchanged and moves on the next turn.
    private void doNothing(int playerIndex) {
        Player currentPlayer = players[playerIndex];
        FileOutput.writeToFile(outputFilePath, currentPlayer.getName() + " threw " + lines[lineNumber] + " and " +
                currentPlayer.getName() + "’s score is " + currentPlayer.getScore() + ".", true, true);

        if (playerIndex == numberOfPlayers - 1) {
            this.playerIndex = 0;
        } else {
            this.playerIndex++;
        }
    }

    // The player leaves the game and the remaining players continue.
    private void gameOver(int playerIndex) {
        Player currentPlayer = players[playerIndex];
        FileOutput.writeToFile(outputFilePath, currentPlayer.getName() + " threw 1-1. Game over " +
                currentPlayer.getName() + "!", true, true);

        removePlayer(playerIndex);
    }

    // Player wants to skip the turn and the turn passes to the next player.
    private void skipTurn(int playerIndex) {
        Player currentPlayer = players[playerIndex];
        FileOutput.writeToFile(outputFilePath, currentPlayer.getName() +" skipped the turn and " +
                currentPlayer.getName() + "’s score is " + currentPlayer.getScore() + ".", true, true);

        if (playerIndex == numberOfPlayers - 1) {
            this.playerIndex = 0;
        } else {
            this.playerIndex++;
        }
    }

    // Add the values of the dices threw by the player to his/her score.
    private void playTurn(int playerIndex, int firstDice, int secondDice) {
        Player currentPlayer = players[playerIndex];
        currentPlayer.setScore(currentPlayer.getScore() + firstDice + secondDice);
        FileOutput.writeToFile(outputFilePath, String.format("%s threw %d-%d and %s’s score is %d." ,
                        currentPlayer.getName(), firstDice, secondDice, currentPlayer.getName(), currentPlayer.getScore()),
                true, true);

        if (playerIndex == numberOfPlayers - 1) {
            this.playerIndex = 0;
        } else {
            this.playerIndex++;
        }
    }

    // Removes the eliminated player from the game.
    private void removePlayer(int playerIndex) {
        if (playerIndex == numberOfPlayers - 1) {
            this.playerIndex = 0;
            numberOfPlayers--;
        } else {
            for (int i = playerIndex; i < numberOfPlayers - 1; i++) {
                players[i] = players[i + 1];
            }
            numberOfPlayers--;
        }
    }

    // Determines the winning player.
    private void declareWinner() {
        Player winner = players[0];
        for (int i = 1; i < numberOfPlayers; i++) {
            if (players[i].getScore() > winner.getScore()) {
                winner = players[i];
            }
        }
        FileOutput.writeToFile(outputFilePath, winner.getName() + " is the winner of the game with " +
                "the score of " + winner.getScore() + ". Congratulations " + winner.getName() + "!" , true, false);
    }

    public static void main(String[] args) {
        String[] lines = FileInput.readFile(args[0], false, false); // Take inputs from file.

        FileOutput.writeToFile(args[1], "", false, false); // Reinitialize the output file.

        DiceGame game = new DiceGame(lines[1].split(","), lines, args[1]);

        game.play();
    }
}