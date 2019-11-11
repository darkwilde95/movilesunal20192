package co.edu.unal.tictactoe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class OnlineGame extends Fragment {
    private TicTacToe mGame;
    private TextView info;
    private BoardView onlineBoard;
    private DatabaseReference mDatabase;
    private String roomId;
    private boolean iam; // true -> creatorPlayer, false -> guestPlayer
    private boolean gameOver;
    private Room prevState;
    Handler handler = new Handler();
    public int winner;
    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Room currentState = dataSnapshot.getValue(Room.class);

            if (!currentState.ready) {
                if (iam) {
                    if (currentState.guestPlayer == null) {
                        info.setText("Waiting for opponent");
                    } else {
                        info.setText(currentState.guestPlayer + " has joined");
                    }
                } else {
                    info.setText("You have joined");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDatabase.child("rooms").child(roomId).child("ready").setValue(true);
                        }
                    }, 1500);
                }
            } else {
                if (currentState.move == -1) {

                    if (currentState.nextTurn) info.setText(iam ? "Your turn" : currentState.creatorPlayer + "'s turn");
                    else info.setText(iam ? currentState.guestPlayer + "'s turn" : "Your turn");

                } else {

                    if (currentState.guestPlayer != null && currentState.creatorPlayer != null) {
                        if (currentState.nextTurn) {
                            mGame.setMove(TicTacToe.X_PLAYER, currentState.move);
                            info.setText(iam ? "Your turn" : currentState.creatorPlayer + "'s turn");
                        } else {
                            mGame.setMove(TicTacToe.O_PLAYER, currentState.move);
                            info.setText(iam ? currentState.guestPlayer + "'s turn" : "Your turn");
                        }
                        winner = mGame.checkForWinner();

                        if (winner == 1) {
                            info.setText(R.string.result_tie);
                            gameOver = true;
                        } else if (winner == 2) {
                            info.setText(iam ? "you win!" : prevState.guestPlayer + " won!");
                            gameOver = true;
                        } else if (winner == 3) {
                            info.setText(!iam ? "you win!" : prevState.creatorPlayer + " won!");
                            gameOver = true;
                        }

                        onlineBoard.invalidate();
                    } else if (currentState.creatorPlayer == null) {
                        info.setText(prevState.creatorPlayer + " has left");
                        gameOver = true;
                    } else {
                        info.setText(prevState.guestPlayer + " has left");
                        gameOver = true;
                    }
                }
            }
            prevState = currentState;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {


        public boolean onTouch(View v, MotionEvent event) {

            int col = (int) event.getX() / onlineBoard.getBoardCellWidth();
            int row = (int) event.getY() / onlineBoard.getBoardCellHeight();
            int pos = row * 3 + col;

            // creatorPlayer Turn
            if (!gameOver) {
                if (iam && prevState.nextTurn || !iam && !prevState.nextTurn) {
                    //Toast.makeText(getContext(), "Entré", Toast.LENGTH_SHORT).show();
                    if (mGame.setMove(iam ? TicTacToe.X_PLAYER : TicTacToe.O_PLAYER, pos)) {
                        Toast.makeText(getContext(), "Entré " + mGame.mBoard.toString(), Toast.LENGTH_SHORT).show();
                        onlineBoard.invalidate();
                        mDatabase.child("rooms").child(roomId).child("move").setValue(pos);
                        mDatabase.child("rooms").child(roomId).child("nextTurn").setValue(!prevState.nextTurn);
                        winner = mGame.checkForWinner();
                    }
                }
                if (winner == 1) {
                    info.setText(R.string.result_tie);
                    gameOver = true;
                } else if (winner == 2) {
                    info.setText(iam ? "you win!" : prevState.guestPlayer + " won!");
                    gameOver = true;
                } else if (winner == 3) {
                    info.setText(!iam ? "you win!" : prevState.creatorPlayer + " won!");
                    gameOver = true;
                }
            }
            return false;
        }
    };


    public OnlineGame(String roomId, boolean iam) {
        this.iam = iam;
        this.roomId = roomId;
        this.prevState = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View onlineGameFragment = inflater.inflate(R.layout.fragment_online_game, container, false);
        mGame = new TicTacToe();
        info = (TextView) onlineGameFragment.findViewById(R.id.online_information);
        onlineBoard = (BoardView) onlineGameFragment.findViewById(R.id.online_board);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        onlineBoard.setGame(mGame);
        mGame.clearBoard();
        onlineBoard.setOnTouchListener(mTouchListener);
        mDatabase.child("rooms").child(this.roomId).addValueEventListener(listener);

        return onlineGameFragment;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iam) {
            mDatabase.child("rooms").child(roomId).child("creatorPlayer").removeValue();
            if (prevState.guestPlayer == null) {
                mDatabase.child("rooms").child(roomId).removeValue();
                mDatabase.child("rooms").child(roomId).removeEventListener(listener);
            }
        } else {
            mDatabase.child("rooms").child(roomId).child("guestPlayer").removeValue();
            if (prevState.creatorPlayer == null) {
                mDatabase.child("rooms").child(roomId).removeValue();
                mDatabase.child("rooms").child(roomId).removeEventListener(listener);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(getContext(), "stop", Toast.LENGTH_SHORT).show();
        if ((iam && prevState.guestPlayer == null) || !iam && prevState.creatorPlayer == null) {
            mDatabase.child("rooms").child(roomId).onDisconnect().removeValue();
        } else if (iam) {
            mDatabase.child("rooms").child(roomId).child("creatorPlayer").onDisconnect().removeValue();
        } else {
            mDatabase.child("rooms").child(roomId).child("guestPlayer").onDisconnect().removeValue();
        }
    }


}
