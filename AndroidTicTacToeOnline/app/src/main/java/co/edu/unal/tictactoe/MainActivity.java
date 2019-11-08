package co.edu.unal.tictactoe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnTouchListener;

public class MainActivity extends AppCompatActivity {

    private TicTacToeIA mGame;
    private boolean mGameOver;
    private BoardView mBoardView;
    private TextView mInfoTextView;
    private boolean mSoundOn;
    private SharedPreferences mPrefs;
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

            if (!mGameOver && setMove(TicTacToeIA.X_PLAYER, pos)) {
                if (mSoundOn) {
                    mHumanMediaPlayer.start();
                }
                handler.postDelayed(new Runnable() {
                    public void run() {

                        winner = mGame.checkForWinner();
                        if (winner == 0) {
                            mInfoTextView.setText(getString(R.string.android_turn));
                            int move = mGame.getComputerMove();
                            setMove(TicTacToeIA.O_PLAYER, move);
                            mBoardView.invalidate();
                            if (mSoundOn) {
                                mComputerMediaPlayer.start();
                            }
                            winner = mGame.checkForWinner();
                        }

                        if (winner == 0)
                            mInfoTextView.setText(getString(R.string.human_turn));
                        else if (winner == 1)
                            mInfoTextView.setText(R.string.result_tie);
                        else if (winner == 2) {
                            String defaultMessage = getResources().getString(R.string.result_human_wins);
                            mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                            mInfoTextView.setText(getString(R.string.result_human_wins));
                            mGameOver = true;
                        } else {
                            mInfoTextView.setText(getString(R.string.result_android_wins));
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
        mGame = new TicTacToeIA();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeIA.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeIA.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeIA.DifficultyLevel.Expert);



        startNewGame();
    }

    private void startNewGame() {

        mGame.clearBoard();
        mGameOver = false;
        mBoardView.invalidate();
        mInfoTextView.setText(getString(R.string.first_turn));
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

            case R.id.online:
                startActivity(new Intent(this, OnlineActivity.class));
                return true;

            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
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
        switch(id) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESULT_CANCELED) {
            mSoundOn = mPrefs.getBoolean("sound", true);
            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));
            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeIA.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeIA.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeIA.DifficultyLevel.Expert);
        }
    }

}


