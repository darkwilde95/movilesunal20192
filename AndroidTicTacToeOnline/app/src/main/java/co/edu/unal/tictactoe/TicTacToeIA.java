package co.edu.unal.tictactoe;

import java.util.Random;

public class TicTacToeIA extends TicTacToe {
    public enum DifficultyLevel {Easy, Harder, Expert};
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    private Random mRand;

    public TicTacToeIA() {

        // Seed the random number generator
        mRand = new Random();
    }

    public DifficultyLevel getDifficultyLevel() {
        return this.mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.mDifficultyLevel = difficultyLevel;
    }

    public int getComputerMove() {
        int move = -1;

        if (mDifficultyLevel == DifficultyLevel.Easy) {
            move = getRandomMove();
        } else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1) {
                move = getRandomMove();
            }
        } else if (mDifficultyLevel == DifficultyLevel.Expert) {
            move = getWinningMove();
            if (move == -1) {
                move = getBlockingMove();
            }
            if (move == -1) {
                move = getRandomMove();
            }
        }

        return move;
    }

    private int getRandomMove() {
        int move = -1;
        do {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == X_PLAYER || mBoard[move] == O_PLAYER);

        mBoard[move] = O_PLAYER;
        return move;
    }

    private int getWinningMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                char curr = mBoard[i];
                mBoard[i] = O_PLAYER;
                if (checkForWinner() == 3) {
                    return i;
                }
                else
                    mBoard[i] = curr;
            }
        }
        return -1;
    }

    private int getBlockingMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != X_PLAYER && mBoard[i] != O_PLAYER) {
                char curr = mBoard[i];   // Save the current number
                mBoard[i] = X_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = O_PLAYER;
                    return i;
                } else
                    mBoard[i] = curr;
            }
        }
        return -1;
    }
}
