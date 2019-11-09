package co.edu.unal.tictactoe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class OnlineMenu extends Fragment {

    private Button createGame, joinGame;
    private EditText nickname;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View menuFragment = inflater.inflate(R.layout.fragment_online_menu, container, false);

        createGame = (Button) menuFragment.findViewById(R.id.create_game);
        joinGame = (Button) menuFragment.findViewById(R.id.join_game);
        nickname = (EditText) menuFragment.findViewById(R.id.username);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick = nickname.getText().toString();
                if (nick == null || nick.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "You must have a nickname", Toast.LENGTH_SHORT).show();
                    return ;
                }
                String roomId = mDatabase.push().getKey();
                Room room = new Room(roomId, nick);
                mDatabase.child("rooms").child(roomId).setValue(room);
                OnlineGame newFragment = new OnlineGame(roomId, true);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nick = nickname.getText().toString();

                if (nick == null || nick.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "You must have a nickname", Toast.LENGTH_SHORT).show();
                    return ;
                }

                GamesList newFragment = new GamesList(nick);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return menuFragment;
    }

}
