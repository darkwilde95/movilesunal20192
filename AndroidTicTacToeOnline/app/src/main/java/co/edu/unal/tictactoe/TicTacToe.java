package co.edu.unal.tictactoe;

public class TicTacToe {

    public char mBoard[] = {'1','2','3','4','5','6','7','8','9'};
    public static final int BOARD_SIZE = 9;

    public static final char X_PLAYER = 'X';
    public static final char O_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';


    public void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            mBoard[i] = OPEN_SPOT;
        }
    }


    public boolean setMove(char player, int location) {
        if (mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player;
            return true;
        }
        return false;
    }

    public char getBoardOccupant(int i) {
        return mBoard[i];
    }

    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    public int checkForWinner() {

        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == X_PLAYER &&
                    mBoard[i+1] == X_PLAYER &&
                    mBoard[i+2]== X_PLAYER)
                return 2;
            if (mBoard[i] == O_PLAYER &&
                    mBoard[i+1]== O_PLAYER &&
                    mBoard[i+2] == O_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == X_PLAYER &&
                    mBoard[i+3] == X_PLAYER &&
                    mBoard[i+6]== X_PLAYER)
                return 2;
            if (mBoard[i] == O_PLAYER &&
                    mBoard[i+3] == O_PLAYER &&
                    mBoard[i+6]== O_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == X_PLAYER &&
                mBoard[4] == X_PLAYER &&
                mBoard[8] == X_PLAYER) ||
                (mBoard[2] == X_PLAYER &&
                        mBoard[4] == X_PLAYER &&
                        mBoard[6] == X_PLAYER))
            return 2;
        if ((mBoard[0] == O_PLAYER &&
                mBoard[4] == O_PLAYER &&
                mBoard[8] == O_PLAYER) ||
                (mBoard[2] == O_PLAYER &&
                        mBoard[4] == O_PLAYER &&
                        mBoard[6] == O_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != X_PLAYER && mBoard[i] != O_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }
}