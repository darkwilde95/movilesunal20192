package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    private boolean mGameOver;
    private BoardView mBoardView;
    private TextView mInfoTextView;
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    Handler handler = new Handler();
    private int winner;



    private OnTouchListener mTouchListener = new OnTouchListener() {

        private boolean setMove(char player, int location) {
            if (mGame.setMove(player, location)) {
                mBoardView.invalidate();
                // Redraw the board
                return true;
            }
            return false;

        }

        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                mHumanMediaPlayer.start();

                handler.postDelayed(new Runnable() {
                    public void run() {

                        winner = mGame.checkForWinner();
                        if (winner == 0) {
                            mInfoTextView.setText("It's Android's turn.");
                            int move = mGame.getComputerMove();
                            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            mBoardView.invalidate();
                            mComputerMediaPlayer.start();
                            winner = mGame.checkForWinner();
                        }

                        if (winner == 0)
                            mInfoTextView.setText("It's your turn.");
                        else if (winner == 1)
                            mInfoTextView.setText("It's a tie!");
                        else if (winner == 2) {
                            mInfoTextView.setText("You won!");
                            mGameOver = true;
                        } else {
                            mInfoTextView.setText("Android won!");
                            mGameOver = true;
                        }

                    }
                }, 750);

            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];

        mInfoTextView = (TextView) findViewById(R.id.information);
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);


        startNewGame();
    }

    private void startNewGame() {

        mGame.clearBoard();
        mGameOver = false;
        mBoardView.invalidate();
        mInfoTextView.setText("You go first.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuBuilder m = (MenuBuilder) menu;
        m.setOptionalIconsVisible(true);
        return true;

    }


    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.cross_sound);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.circle_sound);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;

            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;

            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int selected = 0;
        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle("Choose difficult");

                final CharSequence[] levels = {
                    "Easy",
                    "Harder",
                    "Expert"
                };

                switch(mGame.getDifficultyLevel()) {

                    case Easy:
                        selected = 0;
                        break;

                    case Harder:
                        selected = 1;
                        break;

                    case Expert:
                        selected = 2;
                        break;
                }

                builder.setSingleChoiceItems(levels, selected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.dismiss();

                        switch (item) {
                            case 0:
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                break;

                            case 1:
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                break;

                            case 2:
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                break;
                        }

                        Toast.makeText(getApplicationContext(), levels[item], Toast.LENGTH_SHORT)
                            .show();
                    }
                });
                dialog = builder.create();
                break;

            case DIALOG_QUIT_ID:
                builder.setMessage("Are you sure you want to quit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int id) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", null);
                dialog = builder.create();
                break;

        }
        return dialog;
    }
}

