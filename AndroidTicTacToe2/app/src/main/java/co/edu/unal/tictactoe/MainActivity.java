package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    private Button mBoardButtons[];
    private TextView mInfoTextView;
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
        mInfoTextView = (TextView) findViewById(R.id.information);
        mGame = new TicTacToeGame();
        startNewGame();
    }

    private void startNewGame() {

        mGame.clearBoard();

        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        mInfoTextView.setText("You go first.");
    }

    private class ButtonClickListener implements OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        private void setMove(char player, int location) {
            mGame.setMove(player, location);
            mBoardButtons[location].setEnabled(false);
            mBoardButtons[location].setText(String.valueOf(player));
            if (player == TicTacToeGame.HUMAN_PLAYER)
                mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
            else
                mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
        }

        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText("It's Android's turn.");
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0)
                    mInfoTextView.setText("It's your turn.");
                else if (winner == 1)
                    mInfoTextView.setText("It's a tie!");
                else if (winner == 2)
                    mInfoTextView.setText("You won!");
                else
                    mInfoTextView.setText("Android won!");
            }
        }
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

