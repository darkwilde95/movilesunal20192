package co.edu.unal.tictactoe;


import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class OnlineGame extends Fragment {

    private TextView info;
    private BoardView onlineBoard;
    private DatabaseReference mDatabase;
    private String roomId;
    private boolean iam; // true -> creatorPlayer, false -> guestPlayer
    private Room prevState;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;


    public OnlineGame(String roomId, boolean iam) {
        this.iam = iam;
        this.roomId = roomId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View onlineGameFragment = inflater.inflate(R.layout.fragment_online_game, container, false);

        info = (TextView) onlineGameFragment.findViewById(R.id.online_information);
        onlineBoard = (BoardView) onlineGameFragment.findViewById(R.id.online_board);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prevState = null;

        mDatabase.child("rooms").child(this.roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room currentState = dataSnapshot.getValue(Room.class);
                if (iam) {
                    // Detectar inicio
                    if (prevState == null && currentState.guestPlayer == null) {
                        info.setText("Waiting for opponent");
                    } else if (prevState != null && currentState.guestPlayer != null) {
                        info.setText(currentState.guestPlayer + " has joined!");
                    } else if (prevState.guestPlayer != null && currentState.guestPlayer == null) {
                        info.setText(prevState.guestPlayer + " has left");
                    }

                } else {
                    if (prevState == null) {
                        info.setText("You has joined!");
                    } else if (prevState.creatorPlayer != null && currentState.creatorPlayer == null) {
                        info.setText(prevState.creatorPlayer + " has left");
                    }
                }

                prevState = currentState;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return onlineGameFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (iam) {
            mDatabase.child("rooms").child(roomId).child("creatorPlayer").setValue(null);
            if (prevState.guestPlayer == null) {
                mDatabase.child("rooms").child(roomId).removeValue();
            }
        } else {
            mDatabase.child("rooms").child(roomId).child("guestPlayer").setValue(null);
            if (prevState.creatorPlayer == null) {
                mDatabase.child("rooms").child(roomId).removeValue();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
