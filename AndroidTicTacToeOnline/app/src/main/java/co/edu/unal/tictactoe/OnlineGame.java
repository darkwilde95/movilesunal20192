package co.edu.unal.tictactoe;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class OnlineGame extends Fragment {

    private TextView info;
    private BoardView onlineBoard;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View onlineGameFragment = inflater.inflate(R.layout.fragment_online_game, container, false);

        info = (TextView) onlineGameFragment.findViewById(R.id.online_information);
        onlineBoard = (BoardView) onlineGameFragment.findViewById(R.id.online_board);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        info.setText("Waiting for a player");

        return onlineGameFragment;
    }

}
